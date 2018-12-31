# Eevee Discord Bot

A Discord bot consisting of whatever I find useful.

## Runtime Environment Requirements
- JRE 8
- MongoDB (running on localhost using the default port `27017`)
- Google Cloud Platform Credential File @ `conf/Eevee.Google.json`
- `export GOOGLE_APPLICATION_CREDENTIALS='conf/Eevee.Google.json'`
- `export COFFEE_HOST='coffee.eevee.xyz'`
- YOU WILL NEED TO BE ON VPN TO RUN A LOCAL INSTANCE OF EEVEE IF NOT IN OFFICE!! Contact @Reticence for VPN access.

## Automated Deployment Pipeline
1. Git Commit Pushed
2. Travis Triggered
3. Project Compiled
4. Docker Image Built
5. Docker Image Pushed
6. Docker Image Update Detected
7. Docker Image Pulled
8. Existing Docker Container Swapped