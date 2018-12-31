package xyz.eevee.coffee.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.eevee.coffee.conf.ExitCodes;
import xyz.eevee.coffee.conf.Globals;
import xyz.eevee.coffee.rpc.EntryType;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Builder
@Log4j2
public class DataRepository {
    private Node rootNode;
    private DataWriterService dataWriterService;

    private static DataRepository dataRepository;

    public static DataRepository getInstance() {
        if (dataRepository == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            File dataFile = new File(Globals.DATA_PATH);

            DataReader dataReader = DataReader.builder()
                                              .dataFile(dataFile)
                                              .objectMapper(objectMapper)
                                              .build();

            Optional<Node> nodeOptional = dataReader.read();

            if (!nodeOptional.isPresent()) {
                log.error("Unable to get root node from datafile.");
                System.exit(ExitCodes.UNABLE_TO_LOAD_DATAFILE);
            }

            Node rootNode = nodeOptional.get();

            DataWriterService dataWriterService = DataWriterService.builder()
                                                                   .objectMapper(objectMapper)
                                                                   .dataFile(dataFile)
                                                                   .rootNode(rootNode)
                                                                   .build();

            dataRepository = DataRepository.builder()
                                           .rootNode(rootNode)
                                           .dataWriterService(dataWriterService)
                                           .build();

            dataWriterService.run();
        }

        return dataRepository;
    }

    public Node set(List<String> key, Object value, EntryType entryType) {
        log.info(String.format("Received new set request for key \"%s\" with value \"%s\".", key, value));
        Node newNode = rootNode.set(key, value, entryType);
        log.info("Signaling writer service to write-through updated data nodes.");
        dataWriterService.signal();
        return newNode;
    }

    public Optional<Node> get(List<String> key) {
        log.info(String.format("Received get request for key: %s", key));

        if (key == null || key.size() == 0) {
            return Optional.of(rootNode);
        }

        Optional<Node> node = rootNode.get(key);

        if (!node.isPresent()) {
            log.warn(String.format("Found nothing for key: %s", key));
        }

        return node;
    }

    public Optional<Node> remove(List<String> key) {
        log.info(String.format("Received remove request for key \"%s\".", key));

        Optional<Node> removedNode = rootNode.remove(key);

        if (!removedNode.isPresent()) {
            log.warn(String.format("Could not find valid entry for key: %s", key));
        } else {
            log.info("Signaling writer service to write-through deleted data nodes.");
            dataWriterService.signal();
        }

        return removedNode;
    }
}
