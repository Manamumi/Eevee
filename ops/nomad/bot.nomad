job "bot" {
  # Specify the datacenters within the region this job can run in.
  datacenters = ["dc1"]

  constraint {
    attribute = "${attr.unique.hostname}"
    operator = "regexp"
    value = "^bot-[0-9]+"
  }

  # Configure the job to do rolling updates
  update {
    # Stagger updates every 10 seconds
    stagger = "10s"
    # Update a single task at a time
    max_parallel = 1
  }

  group "eevee" {
    count = 1

    task "eevee-bot" {
      driver = "docker"

      config {
        image = "https://infra.eevee.xyz/eevee:latest"
        force_pull = true
        network_mode = "host"

        volumes = [
          "/opt/eevee/conf:/eevee/eevee/conf"
        ]
      }

      artifact {
        source = "s3::https://sfo2.digitaloceanspaces.com/eevee/bot-conf/Eevee.Google.json"
        destination = "/opt/eevee/conf/Eevee.Google.json"
        mode = "file"

        options {
          aws_access_key_id     = "secret:do_space_key"
          aws_access_key_secret = "secret:do_space_secret"
        }
      }

      service {
        name = "bot"
      }

      env {
        GOOGLE_APPLICATION_CREDENTIALS = "/eevee/eevee/conf/Eevee.Google.json"
        COFFEE_HOST = "coffee.eevee.xyz"
        INSIDE_APP_TOKEN = "secret:eevee_app_token"
        RUNTIME_ENV = "PROD"
        COMMIT_SHA = "env:CI_COMMIT_SHA"
      }

      resources {
        memory = 750
        cpu = 1000
      }
    }
  }
}