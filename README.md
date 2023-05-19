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

1. [What this application can do](#what-this-application-can-do)
2. [Background and motivation](#background-and-motivation)
3. Getting started
    - [🔗 Environment variables](docs/guide-to-environment-variables.md)
    - [🔗 Setting up a local environment](docs/guide-to-local-setup.md)
    - [🔗 Deploying to the cloud](docs/guide-to-cloud-deploy.md)

### What this application can do

- Create a pipeline that collects all your hiring data automatically, with strict separation of concerns to protect
  sensitive candidate information
- Track applicants across a single hiring process or across all processes
- Copy applicants from one Asana project to another while hiding sensitive data from the interview committee
- Send applicants personalized update emails, including receipt of application, release from process, or custom messages
- View anonymized hiring data in charts and graphs through Asana's reporting feature
- Secure all this data with organization-bound Google OIDC and OAuth2 user authentication and authorization
- Run or deploy the application easily using Docker, both [during development](docs/guide-to-local-setup.md)
  and [in production](docs/guide-to-cloud-deploy.md)
- Secure secrets Define your deployments just in time and
  with [environment variable configuration](docs/guide-to-environment-variables.md)

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
