<div align="center">

![Server build status](https://github.com/ConservationColorado/asana-hire/actions/workflows/maven-build-test-and-report.yml/badge.svg)
![Client build status](https://github.com/ConservationColorado/asana-hire/actions/workflows/node-build-test.yml/badge.svg)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-success.svg)](https://www.gnu.org/licenses/gpl-3.0)

</div>

# Welcome!

This repository is home to `asana-hire`, an open source ([view license here](LICENSE)) recruitment management web
application designed for organizations that use Asana. This app is simple to use, lightweight, gives you insight into
your hiring process, and automates your administrative work. Read on to learn more!

## Quick demo

<div align="center">
  <img src="docs/demo.gif" alt="asana-hire demo in an animated image">
  <p>Here's a quick demo of the application, including pages for individual jobs.</p>
</div>

## Table of contents

1. [Background and motivation](#background-and-motivation)
2. [What this application can do](#what-this-application-can-do)
3. [Getting started](#getting-started)
    1. [Environment variables](#environment-variables)
    2. [Running locally with Docker Compose](#running-locally-with-docker-compose)
    3. [Deploying to the cloud](#deploying-to-the-cloud)

### Background and motivation

I first started developing this application to do my job more efficiently at my organization. The tool started out as a
few simple Java classes I wrote to copy data from one place to another, run analytics, and send automated emails. As
we've adopted the tool at our organization, the application has necessarily grown from a set of command line tools to a
full stack site.

This application has administrator, hiring manager, and applicant users, with hundreds of monthly users. Since anyone
can apply to any open position at any time, and since hiring managers can review applications asynchronously, the
availability requirement is high. To achieve this, the application uses Asana as a data store.

Asana is not perfect for this use case. The graph model used under the hood there is slow to retrieve large amounts of
distributed information. This introduces latency to this application. However, Asana has good usability for our staff.
It also comes at no additional cost; we already use the tool, while would have to pay for a managed database service.
Also, we can easily export and report on data within Asana.

In short, Asana gives high availability and non-techie user-friendliness in exchange for additional latency when this
application is used manually. This is a worthwhile tradeoff for our use case!

### What this application can do

- Create a pipeline that collects all your hiring data automatically, with strict separation of concerns to protect
  sensitive candidate information
- Track applicants across a single hiring process or across all processes
- Copy applicants from one Asana project to another while hiding sensitive data from the interview committee
- Send applicants personalized update emails, including receipt of application, release from process, or custom messages
- View anonymized hiring data in charts and graphs through Asana's reporting feature
- Secure all this data with organization-bound Google OIDC and OAuth2 user authentication and authorization
- [Run or deploy the application easily using Docker and Docker Compose](#deploying-to-the-cloud)
- [Separate your deployments with environment variables](#environment-variables)

# Getting started

### Environment variables

To run the app, you'll need to supply the environment variables specified as _required_ below:

```shell
# Asana configuration
ASANA_ACCESS_TOKEN=                 # required: your Asana access token
ASANA_WORKSPACE_GID=                # required: the Asana global identifier for your workspace
ASANA_APPLICATION_PORTFOLIO_GID=    # required: the Asana global identifier for the portfolio containing your application projects
ASANA_INTERVIEW_PORTFOLIO_GID=      # required: the Asana global identifier for the portfolio containing your interview projects

# OAuth2 configuration
GOOGLE_CLIENT_ID=                   # required: your Google client ID, with the gmail.modify scope, limited to your organization
GOOGLE_CLIENT_SECRET=               # required: your Google client secret

# Base URL configuration
REACT_APP_CLIENT_URL=               # required: default is http://localhost:3000
REACT_APP_API_SERVER_URL=           # required: default is http://localhost:8080

# Database configuration
DB_URL=                             # required: your SQL database url
DB_USERNAME=                        # required: your SQL database username
DB_PASSWORD=                        # optional: your SQL database password

# Spring configuration
SPRING_PROFILES_ACTIVE=             # optional: a 'dev' profile is provided in this project
```

Store these in an `.env` file that you keep outside your repository. You'll want to create different `.env` files to
separate your environments, for example development and production.

### Running locally with Docker Compose

<details> 
<summary>Show steps to run the application locally</summary>

#### Get a copy of this repository

There are a few ways to get a local copy of this repository of doing this. You can use the `git` command in your
terminal, if you have it installed:

```shell
git clone https://github.com/ConservationColorado/asana-hire.git
```

You can also use the `wget` command:

```shell
wget -Q https://github.com/ConservationColorado/asana-hire/archive/refs/heads/main.zip && unzip -q main.zip
```

Alternatively, you can
[download a `.zip` file containing of the main branch at this link](https://github.com/ConservationColorado/asana-hire/archive/refs/heads/main.zip),
then extract the contents.

#### Start the application with [Docker Compose](https://docs.docker.com/compose/)

Enter the directory where you copied this repository and run the following `docker compose` command in your terminal:

```shell
docker compose --env-file <your env file> up
```

You may optionally include the `-d` flag to start the containers in detached mode (run in the background of your
terminal).

As an alternative to the command line, [Docker Desktop](https://docs.docker.com/compose/install/) has a user interface
you can use to run the application.

</details>

### Deploying to the cloud

<details>
<summary>Show steps to deploy the application to your favorite cloud provider</summary>

#### Get a copy of this repository

Your virtual machine likely has `git` installed. Clone this repository:

```shell
git clone https://github.com/ConservationColorado/asana-hire.git
```

I've provided [a Shell script to get a Debian-based virtual machine set up](scripts/setup-debian-vm.sh) with everything
you need.

**⚠️ Note!** Running this script _will_ expose your virtual machine to the internet! Please understand this before
continuing.

This installs and configures:

- Docker and Docker Compose
- Nginx as a reverse proxy
- An auto-renewing SSL certificate (through [Let's Encrypt](https://letsencrypt.org/))

To run it, you will need:

- Root privileges on your virtual machine instance
- A static IP address assigned to that virtual machine instance
- A hostname that you own

After running, you may still need to configure your virtual machine's firewall. Check your cloud provider's
documentation for more details. You will also need a DNS A record to point your VM's static IP address to the hostname
you specify.
</details>
