# Running `asana-hire` locally with Docker

You can always run the frontend and backend of the application separately using `npm run` and `mvn exec:java`,
respectively. However, Docker increases the parity between your development and production environments. Containers also
give you a neat way of injecting configuration across the application just in time with environment variables.

These steps will walk you through how to use Docker to run the application locally, for example in a development
environment.

## Prerequisites to running locally

- An internet connection during initial setup
- [Docker](https://docs.docker.com/get-docker/) CLI or GUI

## Steps to run locally

### Get a copy of this repository

There are a few ways to get a local copy of this repository. You can use the `git` command in your terminal, if you have
it installed:

```shell
git clone https://github.com/ConservationColorado/asana-hire.git
```

You can also use `wget`:

```shell
wget -Q https://github.com/ConservationColorado/asana-hire/archive/refs/heads/main.zip && unzip -q main.zip
```

Alternatively, you can
[download a `.zip` file containing of the main branch at this link](https://github.com/ConservationColorado/asana-hire/archive/refs/heads/main.zip),
then extract the contents with your operating system's file explorer.

### Start the application with Docker Compose

Enter the directory where you copied this repository and run the following `docker` command in your terminal:

```shell
docker compose --env-file <path to your env file> up
```

You may optionally include the `-d` flag to start the containers in detached mode (run in the background of your
terminal).

As an alternative to the command line, [Docker Desktop](https://docs.docker.com/get-docker/) has a user interface
you can use to run the application.
