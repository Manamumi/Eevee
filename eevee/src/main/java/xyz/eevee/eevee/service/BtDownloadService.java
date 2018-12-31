package xyz.eevee.eevee.service;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.runtime.BtClient;
import com.google.common.io.BaseEncoding;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Log4j2
public class BtDownloadService implements Service {
    private static final String MAGNET_INFO_HASH_PREFIX = "magnet:?xt=urn:btih:";

    private static volatile BtDownloadService serviceInstance;
    private BlockingQueue<BtDownload> blockingQueue;

    private BtDownloadService() {
        blockingQueue = new LinkedBlockingQueue<>();
    }

    public static BtDownloadService getInstance() {
        if (serviceInstance == null) {
            serviceInstance = new BtDownloadService();
        }

        return serviceInstance;
    }

    @Override
    public void start() {
        Thread thread = new Thread("BTDownloadServiceThread") {
            public void run() {
                while (true) {
                    runOnce();
                }
            }
        };

        thread.start();
    }

    /**
     * Enqueue a BtDownload job to be downloaded.
     *
     * @param btDownload A BtDownload job representing a magnet URL to be downloaded.
     */
    public void enqueue(@NonNull BtDownload btDownload) {
        try {
            btDownload = tryFixInfoString(btDownload);
            log.debug(String.format("New BT download job enqueued: %s,", btDownload));
            blockingQueue.put(btDownload);
        } catch (InterruptedException e) {
            log.error("BtDownloadService interrupted while waiting to enqueue job.", e);
        }
    }

    private void runOnce() {
        try {
            BtDownload job = blockingQueue.take();

            log.info(String.format("Found new job: %s.", job));

            File downloadPath = new File(job.getDownloadLocation());
            boolean madeParent = downloadPath.mkdirs();

            if (madeParent) {
                log.info(String.format("Created new directory for BT download: %s.", job.getDownloadLocation()));
            }

            Storage storage = new FileSystemStorage(downloadPath.toPath());

            BtClient btCLient = Bt.client()
                                  .magnet(job.getMagnetUrl())
                                  .storage(storage)
                                  .stopWhenDownloaded()
                                  .autoLoadModules()
                                  .build();

            log.info(String.format("Starting download for: %s.", job));

            btCLient.startAsync(state -> {
                if (state.getPiecesRemaining() == 0) {
                    // onComplete should only run once.
                    if (!job.isComplete()) {
                        log.info(String.format("Completed download for: %s", job));
                        job.getOnComplete().accept(job);
                    }

                    job.setComplete(true);
                } else {
                    log.debug(
                        String.format(
                            "Downloaded %s/%s pieces for: %s",
                            state.getPiecesComplete(),
                            state.getPiecesTotal(),
                            job
                        )
                    );
                }
            }, 1000).join(); // 1000ms interval
        } catch (InterruptedException e) {
            log.error("BtDownloadService interrupted while waiting for job.", e);
        }
    }

    /**
     * The BT client we are using does not support b32 info hashes so we need to convert them to b16.
     *
     * @param btDownload A BtDownload job with a potentially b32 info hash.
     * @return The same info hash but with the info hash encoded in b16.
     */
    private BtDownload tryFixInfoString(@NonNull BtDownload btDownload) {
        String magnetLink = btDownload.getMagnetUrl();
        final char AMPERSAND = '&';

        // Easiest way to check if this is a b32 info hash.
        if (magnetLink.charAt(MAGNET_INFO_HASH_PREFIX.length() + 32) == AMPERSAND) {
            String infoHash = magnetLink.substring(MAGNET_INFO_HASH_PREFIX.length()).substring(0, 32);
            byte[] b32decodedBytes = BaseEncoding.base32().decode(infoHash);
            String b16encodedString = BaseEncoding.base16().encode(b32decodedBytes);

            return BtDownload.builder()
                             .downloadLocation(btDownload.getDownloadLocation())
                             .magnetUrl(magnetLink.replace(infoHash, b16encodedString))
                             .onComplete(btDownload.getOnComplete())
                             .build();
        }

        return btDownload;
    }

    @Data
    @Builder
    public static class BtDownload {
        private String magnetUrl;
        private String downloadLocation;
        private Consumer<BtDownload> onComplete;
        @Builder.Default
        private boolean isComplete = false;

        public String toString() {
            return String.format("[\"%s\" | \"%s\"]", magnetUrl, downloadLocation);
        }
    }
}
