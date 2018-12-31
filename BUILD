package(default_visibility = ["//visibility:public"])

java_library(
    name = "lombok",
    exported_plugins = [
        ":lombok_plugin",
    ],
    exports = [
        "@org_projectlombok_lombok//jar",
    ],
)

java_plugin(
    name = "lombok_plugin",
    generates_api = 1,
    processor_class = "lombok.launch.AnnotationProcessorHider$AnnotationProcessor",
    deps = [
        "@org_projectlombok_lombok//jar",
    ],
)

py_runtime(
    name = "python36",
    files = [],
    interpreter_path = "/usr/bin/python3.6",
)

exports_files(["WORKSPACE"])
