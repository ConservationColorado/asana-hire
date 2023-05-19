# Guide to deploying `asana-hire` to the cloud

This guide provides all the steps you need to deploy the application to the cloud. After following these steps, you'll
be able to access the application from anywhere.

I've provided [a Shell script to get a Debian-based virtual machine set up](../scripts/setup-debian-vm.sh) that greatly
simplifies the steps you'll take. Before you run this, you'll need to meet all the prerequisites. Then, you can proceed
to deploy the application.

## 1️⃣ What you need before you can deploy

Before you deploy the application to the internet, you need to acquire and set up all the following:

- [A virtual machine instance running a Debian-based operating system, with root privileges](#cloud-provider-setup)
- [A static IP address assigned to that virtual machine instance (not an ephemeral address)](#acquire-a-static-ip-address)
- [A hostname that you own, set up with DNS A records for this application](#dns-setup)

### Cloud provider setup

First, you'll need to rent a virtual machine from a cloud provider. Most cloud providers have an always free or free
tier then pay-as-you-go tier.

For example, you could use any of the following services:

- [Amazon Elastic Compute Cloud (EC2)](https://aws.amazon.com/ec2/)
- [Google Cloud Compute Engine](https://cloud.google.com/compute)
- [Azure Virtual Machines](https://azure.microsoft.com/en-us/products/virtual-machines/)

You can always set up your own machine to serve the application, for example, using a Raspberry Pi. The software setup
is the same as the application is platform-independent; however, the exact steps are out of the scope of this guide.

### Acquire a static IP address

To reliably serve the application over the internet, you'll want to acquire a static IP address (not an ephemeral
address) and assign this to your virtual machine. Check your cloud provider's documentation on information on how to do
this.

### DNS setup

To route traffic from your domain name to your virtual machine, set up these DNS records:

```
Type    Name              Value                            TTL
A       api.asana-hire    <your VM's static IP address>    <any value>
A       asana-hire        <your VM's static IP address>    <any value>
```

You can replace `asana-hire` with anything else in `Name`, as long as your environment variables match. Here's an
example configuration:

```
CLIENT_BASE_URL=https://asana-hire.<your domain name and top level domain>
SERVER_BASE_URL=https://api.asana-hire.<your domain name and top level domain>
```

Finally, you may still need to configure your virtual machine's firewall. Check your cloud provider's documentation for
more details.

## 2️⃣ Deploy the application

### Get a copy of this repository

First, SSH into your virtual machine and clone this repository:

```shell
git clone https://github.com/ConservationColorado/asana-hire.git
```

### Run the script

**⚠️ Note!** Running this script _will_ expose your virtual machine's port 443 to the internet over HTTPS! Please
understand the implications of this before continuing.

```shell
sudo asana-hire/scripts/setup-debian-vm.sh
```

This script will:

- Run updates on your virtual machine
- Install Docker
- Install and configure Nginx
- Install and configure an auto-renewing Let's Encrypt SSL certificate

### Start the application

Enter the directory where you cloned this repository and run the following `docker` command in your terminal:

```shell
docker compose --env-file <path to your env file> up
```

You may optionally include the `-d` flag to start the containers in the background of your terminal.
