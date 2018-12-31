load("@io_bazel_rules_docker//container:container.bzl", "container_image", "container_layer", "container_push")

java_binary(
    name = "eevee",
    data = [
        ":conf",
    ],
    main_class = "xyz.eevee.eevee.Eevee",
    resources = [":resources"],
    runtime_deps = [":eevee_lib"],
)

java_library(
    name = "eevee_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "//:lombok",
        "//coffee:coffee_client",
        "//common/util:net",
        "//common/util:ratelimiter",
        "//common/util:time",
        "//proto:munchlax_java_proto",
        "@amqp_maven//:compile",
        "@bt_maven//:compile",
        "@google_cloud_translate_maven//:compile",
        "@jackson_maven//:compile",
        "@jda_maven//:compile",
        "@logging_maven//:compile",
        "@mongo_maven//:compile",
        "@twitter4j_maven//:compile",
    ],
)

filegroup(
    name = "resources",
    srcs = glob(["src/main/resources/**"]),
)

filegroup(
    name = "conf",
    srcs = glob([
        "src/main/resources/build.json",
        "conf/*",
    ]),
)

container_layer(
    name = "conf_layer",
    data_path = ".",
    # Thanks runfiles :^)
    directory = "/eevee/eevee",
    files = [
        ":conf",
    ],
)

container_image(
    name = "eevee_image",
    base = "@java8_docker//image",
    cmd = [
        "java",
        "-jar",
        "/eevee/eevee_deploy.jar",
    ],
    directory = "/eevee",
    env = {
        "RUNTIME_ENV": "PROD",
    },
    files = [
        ":eevee_deploy.jar",
    ],
    layers = [
        ":conf_layer",
    ],
    workdir = "/eevee",
)

container_push(
    name = "eevee_deploy",
    format = "Docker",
    image = ":eevee_image",
    registry = "infra.eevee.xyz",
    repository = "eevee",
    tag = "latest",
)

sh_test(
    name = "checkstyle",
    size = "small",
    srcs = ["src/test/checkstyle.sh"],
    data = [
        "//tooling:checkstyle-8.12-all.jar",
        "conf/checkstyle.xml",
    ] + glob(["src/**/*.java"]),
)

py_test(
    name = "sanity",
    size = "small",
    srcs = ["src/test/sanity_check.py"],
    data = glob(["**/*"]),
    main = "src/test/sanity_check.py",
    srcs_version = "PY3",
)

py_test(
    name = "pmd",
    size = "small",
    srcs = ["src/test/pmd.py"],
    data = [
        "//tooling:pmd-bin-6.7.0",
    ] + glob(["**/*"]),
    main = "src/test/pmd.py",
    srcs_version = "PY3",
)
