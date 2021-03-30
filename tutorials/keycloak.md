### Keycloak tutorial:

- Keycloak console http port: **24080**
- Keycloak jboss management console port: **24082**
- Keycloak admin user credentials: **kcuser:kcuser**
- SMS realm admin user credentials: **smsadmin:smsadmin**
- Jboss management user credentials: **kcadmin:kcadmin**

Keycloak is deployed on a wildfly 12 application server as a .war,
the server is located under `/home/SMS/subsystems/keycloak/`,
the jboss management console is accessed through bin/jboss-cli.sh and
a connection to the management console is made through the `connect localhost:24082` command, though this will probably never be necessary.
The keycloak administration console is accessed through `<host>:<haproxy_port>/auth`, the credentials for logging in are listed above (kcuser:kcuser).
