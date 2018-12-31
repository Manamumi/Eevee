#!/usr/bin/env bash
bazel build //eevee:eevee_image.tar
sudo docker load -i bazel-bin/eevee/eevee_image.tar
sudo docker run bazel/eevee:eevee_image