# SMS Backend

## Local application setup

### 1. Backend services

Navigate to the `config` maven module, set `DATABASE_HOST`, `DATABASE_PORT` to match your local database and
`WEB_HOST`, `WEB_PORT` to match local haproxy. Then run `mvn clean install sms-backend -Pbuild` or run it from
your IDE. In order to start the backend services you can either just run `java -jar <jar-name>.jar` or from 
Intellij IDEA you can set create a new `JAR Application` type run configuration, set the target to the generated
jar: `<module-name>/target/<jar-name>.jar` and run each configuration.

Runnable modules:
- homework-service (70% complete)
- grades-service 
- usermanagement-service
- timetable-service (0% complete)
- presence-service (0% complete)

The rest are static jar libraries.

### 2. Keycloak

We use keycloak (https://www.keycloak.org/) as an external auth service and provide a pre-built docker image
for running it locally, on the test environment server it's deployed as a simple jar application though, heres
how to run the docker image:

`docker run -p <HAPROXY_PORT>:8080 -e KEYCLOAK_USER=kcuser -e KEYCLOAK_PASSWORD=kcuser stadryniak/kc-sms`

### 3. HAProxy

// TODO

### 4. Frontend

// TODO

### 5. Postgres

// TODO