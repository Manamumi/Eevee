# TODO: Tracking - https://github.com/google/containerregistry/issues/42

load("@inside_deps//:requirements.bzl", "all_requirements")
load("@io_bazel_rules_docker//container:container.bzl", "container_image", "container_layer", "container_push")
load("@subpar//:subpar.bzl", "par_binary")
load("@io_bazel_rules_docker//python:image.bzl", "py_image")

py_binary(
    name = "inside",
    srcs = glob([
        "app.py",
        "inside_app/**/*.py",
    ]),
    data = [
        ":static_files",
    ],
    # TOOD: Change this back.
    # default_python_version = "PY3",
    imports = ["eevee/inside/inside_app"],
    main = "app.py",
    # srcs_version = "PY3",
    deps = all_requirements + [
        "//proto:coffee_python_proto",
    ],
)

filegroup(
    name = "static_files",
    srcs = [
        "static",
        "templates",
    ],
)

container_layer(
    name = "static_files_layer",
    data_path = ".",
    directory = "/inside_eevee/inside",
    files = [
        ":static_files",
    ],
)

container_image(
    name = "inside_eevee",
    base = "@python36_docker//image",
    cmd = [
        "/inside_eevee/inside",
    ],
    directory = "/inside_eevee",
    env = {
        "RUNTIME_ENV": "PROD",
    },
    files = [
        ":inside",
    ],
    layers = [
        ":static_files_layer",
    ],
    ports = ["7744"],
    workdir = "/inside_eevee",
)

container_push(
    name = "inside_eevee_deploy",
    format = "Docker",
    image = ":inside_eevee_py2",
    registry = "infra.eevee.xyz",
    repository = "inside_eevee",
    tag = "latest",
)

py_image(
    name = "inside_eevee_py2",
    srcs = glob([
        "app.py",
        "inside_app/**/*.py",
    ]),
    data = [
        ":static_files",
    ],
    imports = ["eevee/inside/inside_app"],
    main = "app.py",
    deps = all_requirements + [
        "//proto:coffee_python_proto",
    ],
)
