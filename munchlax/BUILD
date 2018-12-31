java_binary(
    name = "munchlax",
    main_class = "xyz.eevee.munchlax.Munchlax",
    resources = [
        ":resources",
    ],
    runtime_deps = [
        ":munchlax_lib",
    ],
)

java_library(
    name = "munchlax_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "//:lombok",
        "//coffee:coffee_client",
        "//common/util:time",
        "//proto:munchlax_java_proto",
        "@amqp_maven//:compile",
        "@jda_maven//:compile",
        "@logging_maven//:compile",
    ],
)

filegroup(
    name = "resources",
    srcs = glob(["src/main/resources/**"]),
)
