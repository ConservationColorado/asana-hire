#!/usr/bin/env bash

# Configures a Debian-based virtual machine with Docker, Nginx, and a Let's Encrypt SSL certificate.
# You'll need to have a static IP address for this VM and a hostname on hand to run this.
# Sets up Docker, Nginx, and SSL

# Don't clutter the console with too many messages by redirecting stdout and stderr to a log file
exec &>>/var/log/setup-debian-vm.log

if [ "$EUID" -ne 0 ]; then
  echo "You can only run this script as a super user. Please rerun with sudo."
  exit 1
fi

# We'll use these for conf
read -rp "Please enter the static IP address in use by this VM instance" static_ip_address
read -rp "Please enter the custom hostname to use for this VM instance" hostname

# Update the VM with the latest .deb packages
apt update
apt upgrade -y
apt-get update

# The hostname for this VM needs to match the NGINX configuration we'll set up later
hostnamectl set-hostname "$hostname"
echo "$static_ip_address $hostname" >>/etc/hosts

# Install Docker
# From https://docs.docker.com/engine/install/debian/
apt-get remove docker docker-engine docker.io containerd runc
apt-get install \
  ca-certificates \
  curl \
  gnupg
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" |
  tee /etc/apt/sources.list.d/docker.list >/dev/null
apt-get update
apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

if ! command -v docker &>/dev/null; then
  echo "Error: Docker installation failed." >&2
  exit 1
fi

# We need a reverse proxy to expose any Docker networks we set up later to the internet.
# We'll use NGINX and set up a starter configuration to proxy the client + api servers forwarded from the Docker network
apt-get install nginx

# Let's Encrypt's certbot will generate more configuration when we run it in a bit
# This base configuration is enough to tell certbot what we need to set up
cat >/etc/nginx/sites-available/default <<EOF

# Frontend configuration
server {

    listen 80;
    server_name  $hostname;

    location / {
        proxy_pass http://localhost:3000;
    }

}

# Backend configuration
server {

    listen 80;
    server_name  api.$hostname;

    location / {
        proxy_pass http://localhost:8080;
    }

}}

EOF
if ! nginx -t; then
  echo "Error: NGINX configuration is invalid." >&2
  exit 1
fi

# Currently, starting NGINX on this VM would make it accessible on the internet, but over HTTP only on :80
# We need to generate and install an SSL certificate to use HTTPS and serve over :443
# We'll use Let's Encrypt's certbot, which is auto-renewing and will modify the default NGINX conf we just created
# From https://stackoverflow.com/a/70387205
apt install certbot python3-certbot-nginx
certbot --nginx -d "$hostname" && certbot --nginx -d "api.$hostname" || {
  echo "Error: Certbot failed to generate an SSL certificate" >&2
  echo "Check to make sure you have a DNS A record set up with Name=$hostname Value=$static_ip_address" >&2
  exit 1
}
systemctl reload nginx
