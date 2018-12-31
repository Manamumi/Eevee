package xyz.eevee.coffee.agent;

import common.gateway.RPCGateway;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import xyz.eevee.coffee.rpc.BooleanEntry;
import xyz.eevee.coffee.rpc.CoffeeGrpc;
import xyz.eevee.coffee.rpc.Entry;
import xyz.eevee.coffee.rpc.Key;
import xyz.eevee.coffee.rpc.NumberEntry;
import xyz.eevee.coffee.rpc.StringEntry;
import xyz.eevee.coffee.rpc.StringListEntry;
import xyz.eevee.coffee.rpc.WriteResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AgentService {
    public static void main(String[] args) throws IOException, InterruptedException {
        String coffeeHost = System.getenv().getOrDefault("COFFEE_HOST", "coffee.eevee.xyz");
        String insideAppToken = System.getenv("INSIDE_APP_TOKEN");

        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(
            coffeeHost, 7755
        ).usePlaintext(true);
        ManagedChannel channel = channelBuilder.build();
        CoffeeGrpc.CoffeeBlockingStub blockingStub = CoffeeGrpc.newBlockingStub(channel);
        blockingStub = RPCGateway.attachHeaders(blockingStub, insideAppToken);

        Server server = ServerBuilder.forPort(7733).addService(new Agent(blockingStub)).build();
        server.start();

        server.awaitTermination();
    }

    private static class Agent extends CoffeeGrpc.CoffeeImplBase {
        private CoffeeGrpc.CoffeeBlockingStub coffeeClient;
        private Map<String, Object> overrides;

        Agent(CoffeeGrpc.CoffeeBlockingStub client) {
            coffeeClient = client;
            overrides = new HashMap<>();
        }

        @Override
        public void get(Key request, StreamObserver<Entry> responseObserver) {
            String key = String.join(".", request.getValueList());
            responseObserver.onNext(
                (Entry) overrides.getOrDefault(key, coffeeClient.get(request))
            );
            responseObserver.onCompleted();
        }

        @Override
        public void getString(Key request, StreamObserver<StringEntry> responseObserver) {
            String key = String.join(".", request.getValueList());
            responseObserver.onNext(
                (StringEntry) overrides.getOrDefault(key, coffeeClient.getString(request))
            );
            responseObserver.onCompleted();
        }

        @Override
        public void getNumber(Key request, StreamObserver<NumberEntry> responseObserver) {
            String key = String.join(".", request.getValueList());
            responseObserver.onNext(
                (NumberEntry) overrides.getOrDefault(key, coffeeClient.getNumber(request))
            );
            responseObserver.onCompleted();
        }

        @Override
        public void getBoolean(Key request, StreamObserver<BooleanEntry> responseObserver) {
            String key = String.join(".", request.getValueList());
            responseObserver.onNext(
                (BooleanEntry) overrides.getOrDefault(key, coffeeClient.getBoolean(request))
            );
            responseObserver.onCompleted();
        }

        @Override
        public void getStringList(Key request, StreamObserver<StringListEntry> responseObserver) {
            String key = String.join(".", request.getValueList());
            responseObserver.onNext(
                (StringListEntry) overrides.getOrDefault(key, coffeeClient.getStringList(request))
            );
            responseObserver.onCompleted();
        }

        @Override
        public void setString(StringEntry request, StreamObserver<WriteResult> responseObserver) {
            overrides.put(
                String.join(".", request.getKeyList()),
                StringEntry.newBuilder()
                           .addAllKey(request.getKeyList())
                           .setValue(request.getValue())
                           .build()
            );
            responseObserver.onNext(WriteResult.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void setNumber(NumberEntry request, StreamObserver<WriteResult> responseObserver) {
            overrides.put(
                String.join(".", request.getKeyList()),
                NumberEntry.newBuilder()
                           .addAllKey(request.getKeyList())
                           .setValue(request.getValue())
                           .build()
            );
            responseObserver.onNext(WriteResult.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void setBoolean(BooleanEntry request, StreamObserver<WriteResult> responseObserver) {
            overrides.put(
                String.join(".", request.getKeyList()),
                BooleanEntry.newBuilder()
                            .addAllKey(request.getKeyList())
                            .setValue(request.getValue())
                            .build()
            );
            responseObserver.onNext(WriteResult.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void setStringList(StringListEntry request, StreamObserver<WriteResult> responseObserver) {
            overrides.put(
                String.join(".", request.getKeyList()),
                StringListEntry.newBuilder()
                               .addAllKey(request.getKeyList())
                               .addAllValue(request.getValueList())
                               .build()
            );
            responseObserver.onNext(WriteResult.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void delete(Key request, StreamObserver<Entry> responseObserver) {
            String key = String.join(".", request.getValueList());

            if (overrides.containsKey(key)) {
                responseObserver.onNext(
                    (Entry) overrides.remove(String.join(".", request.getValueList()))
                );
            }

            responseObserver.onCompleted();
        }
    }
}
