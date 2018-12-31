#!/usr/bin/env bash
# TODO: Change this back!!!
bazel build //inside:inside_eevee_py2.tar # --force_python=PY3 --python_top=//:python36 --python_path=/usr/bin/python3
sudo docker load -i bazel-bin/inside/inside_eevee_py2.tar
sudo docker run -e RUNTIME_ENV=DUMMY -p 7744:7744 bazel/inside:inside_eevee_py2