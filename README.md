# Welcome!

This repository is home to `asana-hire`, an open source ([MIT license](LICENSE)) recruitment management system designed
for small (<50 staff) organizations that use Asana. Use this app to elevate candidate experience, get insight into
hiring demographics and outcomes, and automate administrative work. Best of all, the app is designed for anyone to use
(not just developers).

# Quick demo

<div align="center">
  <img src="docs/demo.gif" alt="asana-hire demo in an animated image">
  <p>Here's a quick demo of the application, including pages for individual jobs.</p>
</div>

# Getting started

To start the application, first clone this repository:

```shell
git clone https://github.com/OliverAbdulrahim/asana-hire.git <destination directory>
```

Run or deploy the application with [Docker Compose](https://docs.docker.com/compose/):

```shell
docker compose --env-file <your env file> up
```

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
