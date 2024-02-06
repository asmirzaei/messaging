# Messaging Service

This service manages users and message passing between users. Also, each user has possibility to get messages that
received, sent, and received from specific user.

## Links

[Swagger](http://localhost:8080/swagger-ui/index.html)

# Tech documentation

## Stack

Kotlin with Spring Boot, Postgres as the database

## Local dependencies

Dependencies such as Postgres are managed with Docker Compose.
Before building or running the service locally, run `docker-compose up --force-recreate` to make the dependencies ready.

## Starting the service locally

To run the service locally, use `src/kotlin/com/visable/messaging/MessagingApplication.kt`.

## Building

```
mvn clean verify
```
### Local development containers

I don't use Testcontainers, preferring to start local dev containers once with docker-compose.
This is because Testcontainers need to be started every time an integration test runs, making the edit-compile-test loop much slower.
