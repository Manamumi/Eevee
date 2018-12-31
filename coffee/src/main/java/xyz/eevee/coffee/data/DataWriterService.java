package xyz.eevee.coffee.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Builder
@Log4j2
class DataWriterService implements Runnable {
    private Node rootNode;
    private ObjectMapper objectMapper;
    private File dataFile;
    @Builder.Default
    private BlockingQueue<Boolean> shouldWriteQueue = new LinkedBlockingDeque<>();

    public void run() {
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdirs();
        }

        DataWriterService that = this;

        Thread thread = new Thread("DataWriterService") {
            public void run() {
                while (true) {
                    log.debug("Waiting for new jobs.");
                    try {
                        shouldWriteQueue.take();
                    } catch (InterruptedException e) {
                        log.error("An unexpected error occurred while taking task from write queue.");
                    }
                    log.debug("Found new job.");
                    writeThrough();
                }
            }
        };

        thread.start();
    }

    void signal() {
        try {
            shouldWriteQueue.put(true);
        } catch (InterruptedException e) {
            log.error("An unexpected error occurred while adding write task to queue.");
        }
    }

    private synchronized void writeThrough() {
        try {
            objectMapper.writeValue(dataFile, rootNode);
        } catch (IOException e) {
            log.error("Failed to persist current data to disk.", e);
        }
    }

}
