#!/bin/bash

case $1 in
	start)
		;;
	stop)
		;;
	status)
		;;
	*)
		echo "Invalid argument"
		echo "Valid arguments: start|stop|status"
		exit 1
		;;
esac

/home/SMS/scripts/service-runner.sh grades-service $1
/home/SMS/scripts/service-runner.sh homework-service $1
/home/SMS/scripts/service-runner.sh presence-service $1
/home/SMS/scripts/service-runner.sh timetable-service $1
/home/SMS/scripts/service-runner.sh usermanagement-service $1

