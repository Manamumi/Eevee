package xyz.eevee.coffee;

import com.google.common.collect.ImmutableList;
import common.gateway.RPCGateway;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import xyz.eevee.coffee.conf.ExitCodes;
import xyz.eevee.coffee.data.DataRepository;
import xyz.eevee.coffee.data.Node;
import xyz.eevee.coffee.rpc.CoffeeRPCService;
import xyz.eevee.coffee.util.DataTransformUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Optional;

@Log4j2
public class Coffee {
    public static void main(String[] args) {
        Optional<Node> nodeOptional = DataRepository.getInstance().get(
            ImmutableList.of("coffee", "rpcPort")
        );

        if (!nodeOptional.isPresent()) {
            log.error("No RPC service port specified.");
            System.exit(ExitCodes.NO_RPC_PORT_SPECIFIED);
        }

        int rpcPort = 0;

        try {
            rpcPort = DataTransformUtil.transformToInt(nodeOptional.get().getValue());
        } catch (NumberFormatException e) {
            log.error("Found RPC service port in Coffee config but value was not of type integer.");
            System.exit(ExitCodes.INVALID_RPC_PORT_SPECIFIED);
        }

        CoffeeRPCServer coffeeRPCServer = new CoffeeRPCServer(ServerBuilder.forPort(rpcPort));

        try {
            coffeeRPCServer.getServer().start();
            log.info(String.format("Started RPC service on port: %s.", rpcPort));
        } catch (IOException e) {
            log.error("An unexpected error occurred.", e);
            System.exit(ExitCodes.UNABLE_TO_START_RPC_SERVER);
        }
    }

    private static class CoffeeRPCServer {
        @Getter
        private Server server;

        CoffeeRPCServer(ServerBuilder<?> serverBuilder) {
            serverBuilder.intercept(new RPCGateway());
            server = serverBuilder.addService(new CoffeeRPCService()).build();
        }
    }
}
