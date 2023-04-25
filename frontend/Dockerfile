FROM node:17-alpine3.12 AS builder

WORKDIR /app
COPY . .
RUN npm install && npm run build

FROM nginx:1.21.0 as production

ENV NODE_ENV=${REACT_APP_ENVIRONMENT}
ENV JSFOLDER=/usr/share/nginx/html/static/js/*.js

COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf
COPY ./scripts/docker-start-nginx.sh /usr/bin/docker-start-nginx.sh

EXPOSE 80

RUN chmod +x /usr/bin/docker-start-nginx.sh
WORKDIR /usr/share/nginx/html
COPY --from=builder /app/build .
ENTRYPOINT [ "docker-start-nginx.sh" ]
