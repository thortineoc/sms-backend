#!/bin/bash

PID=""
running() {
	PID=$(ps aux | grep "java.*timetable-service" | grep -v "grep" | awk '{print $2}')
	[[ $PID == "" ]]
}

running

case $1 in
	start)
		echo "Starting timetable-service at port 24030"
	  setsid java -jar ./timetable-service.jar \
	  -Xmx 256m \
	  > /dev/null 2>&1 < /dev/null &
		;;
	stop)
		echo "Stopping timetable-service"

		if $(running); then
			echo "timetable-service is not running"
			exit 1;
		fi
		kill $PID
		;;
	status)
		echo "timetable-service status:"

		if $(running); then
			echo "Not running"
		else
			echo "Running at pid: $PID";
		fi
		;;
	*)
		echo "Usage: ./run.sh {start|stop|status}"
		exit 1
		;;
esac

exit 0