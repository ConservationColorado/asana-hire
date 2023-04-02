# Welcome!

This repository is home to `asana-hire`, an open source ([view license here](LICENSE)) recruitment management system designed
for small (<50 staff) organizations that use Asana. Use this app to elevate candidate experience, get insight into
hiring demographics and outcomes, and automate administrative work. Best of all, the app is designed for anyone to use
(not just developers). Read on to learn more.

# Quick demo

<div align="center">
  <img src="docs/demo.gif" alt="asana-hire demo in an animated image">
  <p>Here's a quick demo of the application, including pages for individual jobs.</p>
</div>

# Getting started

### Get a copy of this repository
You'll want to get a local copy of everything in this repository. There are a few ways of doing this. You can use the
`git` command, if you have it installed: 
```shell
# over HTTPS
git clone https://github.com/OliverAbdulrahim/asana-hire.git

# over SSH
git clone git@github.com:OliverAbdulrahim/asana-hire.git
```

Alternatively, you can [download a `.zip` file containing of the main branch at this link.](https://github.com/OliverAbdulrahim/asana-hire/archive/refs/heads/main.zip)

### Run or deploy the application with Docker Compose
Once you have the repository copied locally, you can run or deploy it using [Docker Compose](https://docs.docker.com/compose/)

```shell
docker compose --env-file <your env file> up
```
You may optionally include the `-d` flag to start the containers in detached mode (run in the background of your 
terminal). 

You may also use [Docker Desktop](https://docs.docker.com/compose/install/) user interface to run or deploy the 
application

### Environment variables
To run the app, you'll need to supply these environment variables in an `.env` file (pass that into Docker Compose):
```shell
asana_access_token=<your Asana access token>
application_portfolio_gid=<the global identifier for the Asana portfolio with your application projects>
interview_portfolio_gid=<the global identifier for the Asana portfolio with your interview projects>
email_username=<email address to send applicant updates from>
email_password=<app password to that email (authorized for a mail app)>
```

Alternatively, if you use a secret manager, or if you cannot pass in an `.env` file, supply each environment variable
individually when using Docker Compose.

Create separate `.env` files for your production and development environments.

# Core tenants and specifications
Here are the specifications `asana-hire` aims to meet:

* Easy application process for job seekers
    * Distinct application forms for each job
    * Applicants only enter in their information once
    * Communication tailored to each step to keep applicants in the loop
* Streamlined process for the hiring team
    * Manage the interview process in one place, start to finish
    * View live (always on), anonymous reports on how recruiting is going
* Data safety and security in operations
    * Automation wherever possible for efficiency and error reduction
* Application maintainability and robustness
    * Helpful error messages, ease of configurability
    * Containerized for platform independence and portability

# Features

This application extends the functionality of the Asana work management tool using its REST API. This app requires an
Asana Premium plan. Core features of asana-hire include:

* Easily communicate with candidates, up to full automation of base communications
* Anonymous data reports that anyone in your organization with Asana's reporting feature
* Strict separation of concerns: hiring managers and the interview committee are blind to all sensitive applicant
  information
* One click creation jobs materials, including Asana projects
