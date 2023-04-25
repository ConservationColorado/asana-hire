FROM node:17-alpine3.12 AS builder

WORKDIR /app

COPY package.json package.json
COPY package-lock.json package-lock.json

ARG REACT_APP_API_SERVER_URL
ENV REACT_APP_API_SERVER_URL=${REACT_APP_API_SERVER_URL}

RUN npm ci --silent --production

COPY . .

RUN npm run build

FROM nginx:1.21.0-alpine AS production

RUN apk update

COPY /nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/build /usr/share/nginx/html

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
