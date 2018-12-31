load("@io_bazel_rules_docker//container:container.bzl", "container_image", "container_layer", "container_push")

java_binary(
    name = "coffee",
    data = [":data"],
    main_class = "xyz.eevee.coffee.Coffee",
    resources = glob(["src/main/resources/**"]),
    runtime_deps = [":coffee_lib"],
)

java_binary(
    name = "coffee_agent",
    srcs = ["src/main/java/xyz/eevee/coffee/agent/AgentService.java"],
    main_class = "xyz.eevee.coffee.agent.AgentService",
    deps = [
        ":coffee_client",
        "//common/gateway",
        "//proto:coffee_java_proto",
    ],
)

java_binary(
    name = "coffee_cli",
    srcs = ["src/main/java/xyz/eevee/coffee/agent/Agent.java"],
    main_class = "xyz.eevee.coffee.agent.Agent",
    deps = [":coffee_client"],
)

java_library(
    name = "coffee_client",
    srcs = glob([
        "src/main/java/**/client/CoffeeRPCClient.java",
        "src/main/java/**/exc/CoffeeClientException.java",
        "src/main/java/**/util/DataTransformUtil.java",
    ]),
    visibility = ["//visibility:public"],
    deps = [
        "//:lombok",
        "//common/gateway",
        "//proto:coffee_java_proto",
        "@jackson_maven//:compile",
        "@logging_maven//:compile",
    ],
)

java_library(
    name = "coffee_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "//:lombok",
        "//common/gateway",
        "//proto:coffee_java_proto",
        "@jackson_maven//:compile",
        "@logging_maven//:compile",
    ],
)

filegroup(
    name = "data",
    srcs = glob([
        "data/*",
    ]),
)

container_layer(
    name = "data_layer",
    data_path = ".",
    # Thanks runfiles :^)
    directory = "/coffee/coffee",
    files = [
        ":data",
    ],
)

container_image(
    name = "coffee_image",
    base = "@java8_docker//image",
    cmd = [
        "java",
        "-jar",
        "/coffee/coffee_deploy.jar",
    ],
    directory = "/coffee",
    env = {
        "RUNTIME_ENV": "PROD",
    },
    files = [
        ":coffee_deploy.jar",
    ],
    layers = [
        ":data_layer",
    ],
    workdir = "/coffee",
)

container_push(
    name = "coffee_deploy",
    format = "Docker",
    image = ":coffee_image",
    registry = "infra.eevee.xyz",
    repository = "coffee",
    tag = "latest",
)

container_image(
    name = "coffee_agent_image",
    base = "@java8_docker//image",
    cmd = [
        "java",
        "-jar",
        "/coffee/coffee_agent_deploy.jar",
    ],
    directory = "/coffee",
    env = {
        "RUNTIME_ENV": "PROD",
    },
    files = [
        ":coffee_agent_deploy.jar",
    ],
    workdir = "/coffee",
)

container_push(
    name = "coffee_agent_deploy",
    format = "Docker",
    image = ":coffee_agent_image",
    registry = "infra.eevee.xyz",
    repository = "coffee_agent",
    tag = "latest",
)
