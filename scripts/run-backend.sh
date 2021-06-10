#!/bin/bash

if [[ $# -ne 2 ]]; then
	echo "Missing argument"
	exit 255
fi

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
		exit 254
		;;
esac


case $2 in
	start)
		;;
	stop)
		;;
	status)
		;;
	*)
		echo "Invalid argument"
		echo "Valid arguments: start|stop|status"
		exit 255
		;;
esac

echo "Service: $1"
echo "Command: $2"

PID=""
notRunning() {
	PID=$(ps aux | grep "java.*$1" | grep -v "grep" | awk '{print $2}')
	[[ $PID == "" ]]
}

case $2 in
	start)
		echo "Starting $1"
		setsid java -jar ./$1/target/$1*.jar \
		-Xmx 256m \
		> /dev/null 2>&1 < /dev/null &
		;;
	stop)
		echo "Stopping $1"
		if notRunning $1; then
			echo "$1 is not running"
			echo ""
			exit 1;
		fi
		kill $PID
		;;
	status)
		echo "$1 status:"
		if notRunning $1; then
			echo "Not running"
			echo ""
			exit 2
		else
			echo "Running at pid: $PID";
		fi;;
	*)
		echo "Usage: ./run.sh {start|stop|status}"
		exit 255
		;;
esac

echo ""
exit 0
