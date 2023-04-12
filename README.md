# Welcome!

This repository is home to `asana-hire`, an open source ([view license here](LICENSE)) recruitment management web
application designed for organizations that use Asana. This app is simple to use, lightweight, gives you insight into
your hiring process, and automates your administrative work. Read on to learn more!

## Quick demo

<div align="center">
  <img src="docs/demo.gif" alt="asana-hire demo in an animated image">
  <p>Here's a quick demo of the application, including pages for individual jobs.</p>
</div>

### Background and motivation

I first started developing this application to do my job more efficiently at my organization. The tool started out as a
few simple Java classes I wrote to copy data from one place to another, run analytics, and send automated emails. As
we've adopted the tool at our organization, the application has necessarily grown from a set of command line tools to a
full stack site.

### What it can do

- Copy applicants from one project to another while hiding sensitive data from the interview committee
- Send applicants update emails, including receipt of application, release from process, or other custom messages
- View anonymized hiring data in charts and graphs through Asana's reporting feature
- User authentication and authorization via organization-bound Google OIDC and OAuth2
- [Run or deploy the application easily using Docker and Docker Compose](#run-or-deploy-the-application-with-docker-compose)
- [Separate your deployments with environment variables](#environment-variables)

### Planned features

- One click creation of jobs materials, including Asana projects
- Admin console within the application
- Custom job forms you can create and embed on your site
- A fork of this repository that is standalone, independent of Asana

# Getting started

### Get a copy of this repository

You'll want to get a local copy of everything in this repository. There are a few ways of doing this. You can use the
`git` command in your terminal, if you have it installed:

```shell
# over HTTPS
git clone https://github.com/OliverAbdulrahim/asana-hire.git
```

```shell
# over SSH
git clone git@github.com:OliverAbdulrahim/asana-hire.git
```

Alternatively, you can
[download a `.zip` file containing of the main branch at this link.](https://github.com/OliverAbdulrahim/asana-hire/archive/refs/heads/main.zip)

### Run or deploy the application with Docker Compose

Once you have the repository copied locally, you can run or deploy it using
[Docker Compose](https://docs.docker.com/compose/). Run the following `docker compose` command in your terminal:

```shell
docker compose --env-file <your env file> up
```

You may optionally include the `-d` flag to start the containers in detached mode (run in the background of your
terminal).

As an alternative to the command line, [Docker Desktop](https://docs.docker.com/compose/install/) has a user interface
you can use to run the application.

### Environment variables

To run the app, you'll need to supply the environment variables specified below:

```
# Asana configuration
asana_access_token=                 # your Asana access token
workspace_gid=                      # the Asana global identifier for your workspace
application_portfolio_gid=          # the Asana global identifier for the portfolio containing your application projects
interview_portfolio_gid=            # the Asana global identifier for the portfolio containing your interview projects

# OAuth2 client ids and secrets
google_client_id=                   # your Google client ID, with the gmail.modify scope, limited to your organization
google_client_secret=               # your Google client secret

# Base urls
REACT_APP_CLIENT_URL=               # default is http://localhost:3000
REACT_APP_API_SERVER_URL=           # default is http://localhost:8080

# Database configuration
DB_URL=                             # your SQL database url
DB_USERNAME=                        # your SQL database username
DB_PASSWORD=                        # your SQL database password

# Spring configuration
SPRING_PROFILES_ACTIVE=             # a "dev" profile is provided in this project
```

I recommend that you store these in an `.env` file that you keep outside your repository. You can also pass each
variable individually into your Docker Compose command. Create separate `.env` files for your production and development
environments.
