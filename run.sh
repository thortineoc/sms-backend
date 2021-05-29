#!/bin/bash

PID=""
running() {
        PID=$(ps aux | grep "java.*keycloak" | grep -v "grep" | awk '{print $2}')
        [[ $PID == "" ]]
}

case $1 in
	start)
	(cd docker/linux && docker-compose up) &
	sleep 20
	(cd docker && docker exec -i postgres psql -U sms -d sms < db_dump_text) &
	#keycloak run
	#setsid ./keycloak*/bin/standalone.sh -b0.0.0.0 -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=docker/keycloak_db.json -Dkeycloak.migration.strategy=OVERWRITE_EXISTING -Djboss.http.port=24080 -Djboss.management.http.port=24082 &#  > /dev/null 2>&1 < /dev/null &	
		;;
	stop)
	  (cd docker/linux && docker-compose down)
	  if $(running); then
                        echo "Keycloak is not running"
	  else
          	kill $PID
	  fi
		;;
	status)
		;;
	*)
		echo "Invalid argument"
		echo "Valid arguments: start|stop|status"
		exit 1
		;;
esac

./scripts/run-backend.sh grades-service $1
./scripts/run-backend.sh homework-service $1
./scripts/run-backend.sh presence-service $1
./scripts/run-backend.sh timetable-service $1
./scripts/run-backend.sh usermanagement-service $1



