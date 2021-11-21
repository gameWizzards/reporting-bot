FROM maven:3.6.0-jdk-11-slim AS build
COPY src /src
COPY pom.xml /
RUN mvn -f /pom.xml clean package

FROM adoptopenjdk/openjdk11:ubi
ARG JAR_FILE=target/*.jar
ARG BOT_USERNAME=unspecified
ARG BOT_TOKEN=unspecified
ARG BOT_DB_USERNAME=unspecified
ARG BOT_DB_PASSWORD=unspecified
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.datasource.password=${BOT_DB_PASSWORD}", "-Dbot.username=${BOT_NAME}", "-Dbot.token=${BOT_TOKEN}", "-Dspring.datasource.username=${BOT_DB_USERNAME}", "-jar", "app.jar"]