package common.gateway;

import com.google.api.client.http.HttpHeaders;
import common.util.NetUtil;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.MetadataUtils;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class RPCGateway implements ServerInterceptor {
    private static final String INSIDE_AUTH_CHECK_URL = "https://inside.eevee.xyz/auth/check";
    private static final String AUTH_TOKEN_HEADER_NAME = "X-Inside-Token";
    private static final Metadata.Key<String> AUTH_TOKEN_HEADER = Metadata.Key.of(
        AUTH_TOKEN_HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER
    );
    private static final ServerCall.Listener NOOP_LISTENER = new ServerCall.Listener(){};

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> serverCall,
        Metadata metadata,
        ServerCallHandler<ReqT, RespT> next
    ) {
        if (!metadata.containsKey(AUTH_TOKEN_HEADER)) {
            log.warn("Received incoming RPC without an application token.");

            serverCall.close(Status.UNAUTHENTICATED.withCause(
                new MissingAppTokenException("You must specify an app token to use this service.")
            ).withDescription("You must specify an app token to use this service."), metadata);

            return NOOP_LISTENER;
        }

        String appToken = metadata.get(AUTH_TOKEN_HEADER);

        log.info(String.format(
            "Received incoming RPC using application token: %s", appToken
        ));

        try {
            NetUtil.getPage(INSIDE_AUTH_CHECK_URL, new HttpHeaders().set(
                AUTH_TOKEN_HEADER_NAME, appToken
            ));

            return next.startCall(serverCall, metadata);
        } catch (IOException e) {
            log.warn(String.format(
                "An invalid application token was specified: %s", appToken
            ), e);
            serverCall.close(Status.PERMISSION_DENIED.withCause(
                new InvalidAppTokenException("The specified app token is not valid.")
            ).withDescription("The specified app token is not valid."), metadata);
        }

        return NOOP_LISTENER;
    }

    public static <T extends AbstractStub> T attachHeaders(T stub, String appToken) {
        Metadata header = new Metadata();
        Metadata.Key<String> insideTokenHeader = Metadata.Key.of(
            AUTH_TOKEN_HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER
        );
        header.put(insideTokenHeader, appToken);

        return (T) MetadataUtils.attachHeaders(stub, header);
    }

    public class MissingAppTokenException extends RuntimeException {
        MissingAppTokenException(String message) {
            super(message);
        }
    }

    public class InvalidAppTokenException extends RuntimeException {
        InvalidAppTokenException(String message) {
            super(message);
        }
    }
}
