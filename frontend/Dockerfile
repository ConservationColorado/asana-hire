FROM node:17-alpine3.12 AS builder

COPY ../scripts/process-config-json.sh /usr/bin/process-config-json.sh
RUN chmod +x /usr/bin/process-config-json.sh

RUN apk --no-cache add \
    jq \
    bash

WORKDIR /app

COPY frontend/package.json package.json
COPY frontend/package-lock.json package-lock.json

RUN npm ci --silent --production

COPY frontend/ .

# Prepares config.json for environment variable injection
RUN process-config-json.sh src/config.json

RUN npm run build

FROM nginx:1.21.0-alpine AS production

ENV SERVER_BASE_URL=${SERVER_BASE_URL}
ENV CLIENT_BASE_URL=${CLIENT_BASE_URL}

COPY frontend/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/build /usr/share/nginx/html

RUN apk --no-cache add \
    bash \
    moreutils

# Injects this instance's environment variables to config.json
COPY ../scripts/frontend-docker-entrypoint.sh /usr/bin/frontend-docker-entrypoint.sh
RUN chmod +x /usr/bin/frontend-docker-entrypoint.sh

EXPOSE 3000

ENTRYPOINT ["frontend-docker-entrypoint.sh"]
