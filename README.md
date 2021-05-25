# SMS Backend

## LOCAL ENV
### 1. Keycloak, Haproxy and Postgres

- Go to docker directory: 
`cd docker`

- Run composer:
`docker compose up`
  
- Initialize database (Windows: use WSL)
`docker exec -i postgres  pg_restore -U sms -v -d sms < db_dump_text`

### 2. Backend

- Go to `config/scripts/values.properties` and set:
    - `WEB_HOST=localhost`
    - `DATABASE_HOST=localhost`
- `mvn clean install -Pbuild` or you can click on clean and then install in the maven gui for intellij IDEA under the sms-backend module with the `build` profile checked
- [Create a new intellij run configuration and choose the jar application type, then set the target to the built jar files](tutorials/run-configuration.png)
- Now you can run all 5 of the backend services
- `mvn clean install -Pintegration-tests` to run tests