job "inside" {
  # Specify the datacenters within the region this job can run in.
  datacenters = ["dc1"]

  constraint {
    attribute = "${attr.unique.hostname}"
    operator = "regexp"
    value = "^web-[0-9]+"
  }

  # Configure the job to do rolling updates
  update {
    # Stagger updates every 10 seconds
    stagger = "10s"
    # Update a single task at a time
    max_parallel = 1
  }

  group "inside" {
    count = 1

    task "inside-eevee" {
      driver = "docker"

      config {
        image = "https://infra.eevee.xyz/inside_eevee:latest"
        force_pull = true
        network_mode = "host"

        port_map {
          inside_http = 7744
        }

        labels {
          traefik.frontend.rule = "Host:inside.eevee.xyz"
        }
      }

      service {
        name = "inside-eevee"
        port = "inside_http"
        tags = ["traefik.frontend.rule=Host:inside.eevee.xyz"]
      }

      resources {
        network {
          port "inside_http" {}
        }
      }

      env {
        INSIDE_TOKEN = "secret:inside_token"
        INSIDE_APP_TOKEN = "secret:inside_app_token"
        RUNTIME_ENV = "PROD"
        COMMIT_SHA = "env:CI_COMMIT_SHA"
      }
    }
  }
}
