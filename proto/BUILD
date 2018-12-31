package(default_visibility = ["//visibility:public"])

load("@org_pubref_rules_protobuf//java:rules.bzl", "java_proto_library")
load("@org_pubref_rules_protobuf//python:rules.bzl", "py_proto_library")

java_proto_library(
    name = "coffee_java_proto",
    protos = ["coffee/coffee.proto"],
    with_grpc = True,
)

py_proto_library(
    name = "coffee_python_proto",
    protos = ["coffee/coffee.proto"],
    with_grpc = True,
)

java_proto_library(
    name = "munchlax_java_proto",
    protos = ["munchlax/munchlax.proto"],
)

java_proto_library(
    name = "reporter_java_proto",
    protos = ["reporter/reporter.proto"],
    with_grpc = True,
)
