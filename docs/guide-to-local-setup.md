# Running `asana-hire` locally with Docker Compose

These steps are useful for running the application locally, for example in development.

## Prerequisites to running locally

- An internet connection during initial setup
- [Docker](https://docs.docker.com/get-docker/) CLI or GUI

## Steps to run locally

### Get a copy of this repository

There are a few ways to get a local copy of this repository of doing this. You can use the `git` command in your
terminal, if you have it installed:

```shell
git clone https://github.com/ConservationColorado/asana-hire.git
```

You can also use `wget`:

```shell
wget -Q https://github.com/ConservationColorado/asana-hire/archive/refs/heads/main.zip && unzip -q main.zip
```

Alternatively, you can
[download a `.zip` file containing of the main branch at this link](https://github.com/ConservationColorado/asana-hire/archive/refs/heads/main.zip),
then extract the contents.

### Start the application with Docker Compose

Enter the directory where you copied this repository and run the following `docker` command in your terminal:

```shell
docker compose --env-file <path to your env file> up
```

You may optionally include the `-d` flag to start the containers in detached mode (run in the background of your
terminal).

As an alternative to the command line, [Docker Desktop](https://docs.docker.com/get-docker/) has a user interface
you can use to run the application.
