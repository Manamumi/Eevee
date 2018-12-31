#!/usr/bin/env bash
bazel build //coffee:coffee_image.tar
sudo docker load -i bazel-bin/coffee/coffee_image.tar
sudo docker run bazel/coffee:coffee_image