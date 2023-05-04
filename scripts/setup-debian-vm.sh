#!/usr/bin/env bash

# Configures a Debian-based virtual machine with Docker, Nginx, and a Let's Encrypt SSL certificate.
# You'll need to have a static IP address for this VM and a hostname on hand to run this.
# Sets up Docker, Nginx, and SSL

# Redirect stdout and stderr to a log file
exec &>>/var/log/setup-debian-vm.log

if [ "$EUID" -ne 0 ]; then
  echo "You can only run this script as a super user. Please rerun with sudo."
  exit 1
fi

# The user needs to specify a few variables to start
read -rp "Please enter the static IP address in use by this VM instance" static_ip_address
read -rp "Please enter the custom hostname to use for this VM instance" hostname

# Install any Debian updates
apt update
apt upgrade -y
apt-get update

# Configure self hostname for this VM
hostnamectl set-hostname "$hostname"
echo "$static_ip_address $hostname" >>/etc/hosts

# Install Docker
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
# We'll use nginx and set up a starter configuration to proxy the client + api servers forwarded from the Docker network
apt-get install nginx

# Let's Encrypt's certbot will generate more configuration when we run it in a bit
cat >/etc/nginx/sites-available/default <<'EOF'
server {
    listen 80 default_server;
    listen [::]:80 default_server;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
EOF
if ! nginx -t; then
  echo "Error: Nginx configuration is invalid." >&2
  exit 1
fi
systemctl reload nginx

# Currently, this VM is accessible on the internet! But over HTTP only.
# We need to generate and install an SSL certificate to use HTTPS.
# We'll use Let's Encrypt's certbot, which is auto-renewing
# From https://stackoverflow.com/a/70387205
apt install certbot python3-certbot-nginx
certbot --nginx -d "$hostname" || {
  echo "Error: Certbot failed to generate an SSL certificate." >&2
  exit 1
}
