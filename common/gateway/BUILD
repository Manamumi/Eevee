package(default_visibility = ["//visibility:public"])

java_library(
    name = "gateway",
    srcs = ["RPCGateway.java"],
    deps = [
        "//:lombok",
        "//common/util:net",
        "@grpc_maven//:compile",
        "@http_client_maven//:compile",
        "@logging_maven//:compile",
    ],
)
