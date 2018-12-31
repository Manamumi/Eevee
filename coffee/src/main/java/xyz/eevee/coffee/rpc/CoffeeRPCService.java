package xyz.eevee.coffee.rpc;

import com.google.common.collect.ImmutableList;
import io.grpc.stub.StreamObserver;
import xyz.eevee.coffee.data.DataRepository;
import xyz.eevee.coffee.data.Node;
import xyz.eevee.coffee.util.DataTransformUtil;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@Log4j2
public class CoffeeRPCService extends CoffeeGrpc.CoffeeImplBase {
    private DataRepository dataRepository;

    public CoffeeRPCService() {
        dataRepository = DataRepository.getInstance();
    }

    @Override
    public void get(Key request, StreamObserver<Entry> responseObserver) {
        Optional<Node> dataNode = dataRepository.get(request.getValueList());

        if (!dataNode.isPresent()) {
            responseObserver.onNext(
                Entry.newBuilder()
                     .setError(
                         String.format(
                             "The specified key %s is not valid.",
                             String.join(".", request.getValueList())
                         )
                     )
                     .addAllKey(request.getValueList())
                     .build()
            );
        } else {
            responseObserver.onNext(
                serializeNodeToEntry(dataNode.get())
            );
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getString(Key request, StreamObserver<StringEntry> responseObserver) {
        Optional<Node> dataNode = dataRepository.get(request.getValueList());

        if (!dataNode.isPresent()) {
            responseObserver.onNext(
                StringEntry.newBuilder()
                           .setError(
                               String.format(
                                   "The specified key %s is not valid.",
                                   String.join(".", request.getValueList())
                               )
                           )
                           .addAllKey(request.getValueList())
                           .build()
            );
        } else if (dataNode.get().getEntryType() != EntryType.String) {
            responseObserver.onNext(
                StringEntry.newBuilder()
                           .setError(
                               String.format(
                                   "The requested entry type does not match the actual type. Requested: \"%s\" vs Actual: \"%s\".",
                                   EntryType.String,
                                   dataNode.get().getEntryType()
                               )
                           ).build()
            );
        } else {
            responseObserver.onNext(
                StringEntry.newBuilder()
                           .addAllKey(request.getValueList())
                           .setValue(dataNode.get().getValue().toString())
                           .build()
            );
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getNumber(Key request, StreamObserver<NumberEntry> responseObserver) {
        Optional<Node> dataNode = dataRepository.get(request.getValueList());

        if (!dataNode.isPresent()) {
            responseObserver.onNext(
                NumberEntry.newBuilder()
                           .setError(
                               String.format(
                                   "The specified key %s is not valid.",
                                   String.join(".", request.getValueList())
                               )
                           )
                           .addAllKey(request.getValueList())
                           .build()
            );
            return;
        } else if (dataNode.get().getEntryType() != EntryType.Number) {
            responseObserver.onNext(
                NumberEntry.newBuilder()
                           .setError(
                               String.format(
                                   "The requested entry type does not match the actual type. Requested: \"%s\" vs Actual: \"%s\".",
                                   EntryType.Number,
                                   dataNode.get().getEntryType()
                               )
                           ).build()
            );
        } else {
            responseObserver.onNext(
                NumberEntry.newBuilder()
                           .addAllKey(request.getValueList())
                           .setValue(DataTransformUtil.transformToDouble(dataNode.get().getValue().toString()))
                           .build()
            );
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getBoolean(Key request, StreamObserver<BooleanEntry> responseObserver) {
        Optional<Node> dataNode = dataRepository.get(request.getValueList());

        if (!dataNode.isPresent()) {
            responseObserver.onNext(
                BooleanEntry.newBuilder()
                            .setError(
                                String.format(
                                    "The specified key %s is not valid.",
                                    String.join(".", request.getValueList())
                                )
                            )
                            .addAllKey(request.getValueList())
                            .build()
            );
            return;
        } else if (dataNode.get().getEntryType() != EntryType.Boolean) {
            responseObserver.onNext(
                BooleanEntry.newBuilder()
                            .setError(
                                String.format(
                                    "The requested entry type does not match the actual type. Requested: \"%s\" vs Actual: \"%s\".",
                                    EntryType.Boolean,
                                    dataNode.get().getEntryType()
                                )
                            ).build()
            );
        } else {
            responseObserver.onNext(
                BooleanEntry.newBuilder()
                            .addAllKey(request.getValueList())
                            .setValue(DataTransformUtil.transformToBoolean(dataNode.get().getValue().toString()))
                            .build()
            );
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getStringList(Key request, StreamObserver<StringListEntry> responseObserver) {
        Optional<Node> dataNode = dataRepository.get(request.getValueList());

        if (!dataNode.isPresent()) {
            responseObserver.onNext(
                StringListEntry.newBuilder()
                               .setError(
                                   String.format(
                                       "The specified key %s is not valid.",
                                       String.join(".", request.getValueList())
                                   )
                               )
                               .addAllKey(request.getValueList())
                               .build()
            );
        } else if (dataNode.get().getEntryType() != EntryType.StringList) {
            responseObserver.onNext(
                StringListEntry.newBuilder()
                               .setError(
                                   String.format(
                                       "The requested entry type does not match the actual type. Requested: \"%s\" vs Actual: \"%s\".",
                                       EntryType.StringList,
                                       dataNode.get().getEntryType()
                                   )
                               ).build()
            );
        } else {
            try {
                List<String> stringList = (List<String>) dataNode.get().getValue();
                responseObserver.onNext(
                    StringListEntry.newBuilder()
                                   .addAllKey(request.getValueList())
                                   .addAllValue(stringList)
                                   .build()
                );
            } catch (ClassCastException e) {
                responseObserver.onNext(
                    StringListEntry.newBuilder()
                                   .setError(
                                       String.format(
                                           "The specified key %s is not of type StringList.",
                                           String.join(".", request.getValueList())
                                       )
                                   )
                                   .addAllKey(request.getValueList())
                                   .build()
                );
            }
        }

        responseObserver.onCompleted();
    }

    @Override
    public void setString(StringEntry request, StreamObserver<WriteResult> responseObserver) {
        String key = request.getKeyList().stream().collect(joining("."));

        if (key.length() == 0 || key.equalsIgnoreCase("root")) {
            responseObserver.onError(
                    new IllegalArgumentException("The root node may not be set.")
            );
        }

        dataRepository.set(request.getKeyList(), request.getValue(), EntryType.String);
        responseObserver.onNext(WriteResult.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void setNumber(NumberEntry request, StreamObserver<WriteResult> responseObserver) {
        String key = request.getKeyList().stream().collect(joining("."));

        if (key.length() == 0 || key.equalsIgnoreCase("root")) {
            responseObserver.onError(
                    new IllegalArgumentException("The root node may not be set.")
            );
        }

        dataRepository.set(request.getKeyList(), request.getValue(), EntryType.Number);
        responseObserver.onNext(WriteResult.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void setBoolean(BooleanEntry request, StreamObserver<WriteResult> responseObserver) {
        String key = request.getKeyList().stream().collect(joining("."));

        if (key.length() == 0 || key.equalsIgnoreCase("root")) {
            responseObserver.onError(
                    new IllegalArgumentException("The root node may not be set.")
            );
        }

        dataRepository.set(request.getKeyList(), request.getValue(), EntryType.Boolean);
        responseObserver.onNext(WriteResult.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void setStringList(StringListEntry request, StreamObserver<WriteResult> responseObserver) {
        String key = request.getKeyList().stream().collect(joining("."));

        if (key.length() == 0 || key.equalsIgnoreCase("root")) {
            responseObserver.onError(
                    new IllegalArgumentException("The root node may not be set.")
            );
        }

        dataRepository.set(request.getKeyList(), request.getValueList(), EntryType.StringList);
        responseObserver.onNext(WriteResult.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void delete(Key request, StreamObserver<Entry> responseObserver) {
        String key = request.getValueList().stream().collect(joining("."));

        if (key.length() == 0 || key.equalsIgnoreCase("root")) {
            responseObserver.onError(
                new IllegalArgumentException("The root node may not be deleted.")
            );
        }

        Optional<Node> removedNodeOptional = dataRepository.remove(request.getValueList());

        if (!removedNodeOptional.isPresent()) {
            responseObserver.onNext(
                Entry.newBuilder()
                     .setError("The requested key does not exist.")
                     .build()
            );
        } else {
            Node removedNode = removedNodeOptional.get();
            responseObserver.onNext(serializeNodeToEntry(removedNode));
        }

        responseObserver.onCompleted();
    }

    private Entry serializeNodeToEntry(Node node) {
        Map<String, Entry> children = new HashMap<>();

        node.getChildren().forEach((key, child) -> {
            children.put(key, serializeNodeToEntry(child));
        });

        Entry.Builder entryBuilder = Entry.newBuilder()
                                          .addAllKey(ImmutableList.copyOf(node.getKey().split("\\.")))
                                          .setEntryType(node.getEntryType())
                                          .putAllChildren(children);

        if (node.getValue() != null) {
            entryBuilder.setValue(node.getValue().toString());
        }

        return entryBuilder.build();
    }
}
