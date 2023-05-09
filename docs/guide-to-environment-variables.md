# Environment variables for `asana-hire`

To run the app, you'll need to supply the environment variables specified as _required_ below:

| Variable                          | Required? | Description                                                                                                                          |
|-----------------------------------|:---------:|:-------------------------------------------------------------------------------------------------------------------------------------|
| `ASANA_ACCESS_TOKEN`              |     Y     | Your [Asana access token](https://developers.asana.com/docs/authentication#personal-access-token)                                    |
| `ASANA_WORKSPACE_GID`             |     Y     | The Asana global identifier for your workspace. Used to search your workspace globally and to populate custom fields across projects |
| `ASANA_APPLICATION_PORTFOLIO_GID` |     Y     | The Asana global identifier for the portfolio containing your application projects                                                   |
| `ASANA_INTERVIEW_PORTFOLIO_GID`   |     Y     | The Asana global identifier for the portfolio containing your interview projects                                                     |
| `GOOGLE_CLIENT_ID`                |     Y     | Your Google client ID, with the `gmail.modify` scope, limited to your organization                                                   |
| `GOOGLE_CLIENT_SECRET`            |     Y     | Your Google client secret                                                                                                            |
| `CLIENT_BASE_URL`                 |     Y     | The base URL for the front end of this application. [More information here](#base-url-configuration)                                 |
| `SERVER_BASE_URL`                 |     Y     | The base URL for the back end of this application. [More information here](#base-url-configuration)                                  |
| `DB_URL`                          |     Y     | Your SQL database URL                                                                                                                |
| `DB_USERNAME`                     |     Y     | Your SQL database username                                                                                                           |
| `DB_PASSWORD`                     |     N     | Your SQL database password                                                                                                           |
| `SPRING_PROFILES_ACTIVE`          |     N     | Configuration profile for the back end. A 'dev' profile is provided in this project for local development                            |

Store these in an `.env` file that you keep outside your repository. I've provided an [example empty configuration here
in this repository](.env.example).

You'll want to create different `.env` files to separate your environments, for example development and production.

### Base URL configuration

The application supports flexible URL configuration via environment variables.

For local development environments, use these URLs:

- `CLIENT_BASE_URL=http://localhost:3000`
- `SERVER_BASE_URL=http://localhost:8080`

For production environments, you can follow any schema, just make sure that whatever you choose matches your DNS A
records. For example, you could use the following:

- `CLIENT_BASE_URL=https://asana-hire.<your base URL and top-level domain>`
- `SERVER_BASE_URL=https://api.asana-hire.<your base URL and top-level domain>`
