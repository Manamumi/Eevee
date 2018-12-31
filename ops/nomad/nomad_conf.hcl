data_dir = "/opt/nomad"

bind_addr = "0.0.0.0"

client {
  enabled = true
  network_interface = "eth0"
  servers = ["infra.eevee.xyz"]
  network_speed = 1000
}