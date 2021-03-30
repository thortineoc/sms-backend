### Deployment and development tutorial

1. Backend services should be deployed under **/home/SMS/subsystems/backend/<service-name>**
2. Runnable .jars should be under the **/<service-name>** directory along with a **run.sh** script
accepting start, status and stop positional parameters (analogous to the keycloak run.sh script, just copy the code and change the jar name).
3. Backend .jars should log to a file **/<service-name>/log/<service-name>.log** which should ideally overflow after its more than 25MB in size and a *.log2 file should be created,
   up to *.log9 (10 log files, each 25MB in size total), after that it should reset the first log and start from beginning (all configurable in spring app properties).
4. (TODO: idk about frontend yet)