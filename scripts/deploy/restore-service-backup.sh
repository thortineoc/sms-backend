#!/bin/bash

case $1 in
	grades-service)
		;;
	homework-service)
		;;
	presence-service)
		;;
	timetable-service)
		;;
	usermanagement-service)
		;;
	*)
		echo "Invalid service"
		echo "Valid arguments: grades-service homework-service presence-service timetable-service usermanagement-service"
		exit 1
		;;
esac

echo "Resotring service: $1"

cp /home/SMS/scripts/services-backup/$1.jar /home/SMS/subsystems/backend/$1/$1.jar

