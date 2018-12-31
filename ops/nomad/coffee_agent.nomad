job "coffee-agent" {
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

  group "coffee-agent" {
    count = 1

    task "coffee-agent" {
      driver = "docker"

      config {
        image = "https://infra.eevee.xyz/coffee_agent:latest"
        force_pull = true
        network_mode = "host"

        port_map {
          rpc = 7733
        }
      }

      service {
        name = "coffee_agent"
        port = "rpc"
      }

      env {
        RUNTIME_ENV = "PROD"
        COMMIT_SHA = "env:CI_COMMIT_SHA"
      }

      resources {
        network {
          port "rpc" {
            static = "7733"
          }
        }
      }
    }
  }
}
