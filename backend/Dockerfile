FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /opt/app

COPY /backend/.mvn .mvn
COPY /backend/mvnw /backend/pom.xml ./
RUN ./mvnw dependency:go-offline

COPY /backend/src ./src
RUN ./mvnw clean install

FROM eclipse-temurin:17-jre-alpine

WORKDIR /opt/app
EXPOSE 8080

COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar

ENTRYPOINT ["java", "-jar", "/opt/app/*.jar"]
