#!/bin/bash

case $1 in
	start)
	  (cd docker && \
    docker-compose up && \
    docker exec -i postgres pg_restore -U sms -v -d sms < db_dump_text) &
		;;
	stop)
	  (cd docker && \
	  docker-compose down)
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



