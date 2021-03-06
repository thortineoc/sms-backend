#!/bin/bash

PID=""
running() {
        PID=$(ps aux | grep "java.*keycloak" | grep -v "grep" | awk '{print $2}')
        [[ $PID == "" ]]
}

case $1 in
	start)
	mvn clean install -Pbuild
	(cd docker/linux && docker-compose up) &
	while ! timeout 1 bash -c "echo > /dev/tcp/localhost/5432"; do echo "Waiting for server..."; sleep 1; done
	(cd docker && docker exec -i postgres psql -U sms -d sms < db_dump_text) &
		;;
	stop)
	  (cd docker/linux && docker-compose down)
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



