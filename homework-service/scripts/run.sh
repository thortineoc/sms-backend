#!/bin/bash

PID=""
running() {
	PID=$(ps aux | grep "java.*homework-service" | grep -v "grep" | awk '{print $2}')
	[[ $PID == "" ]]
}

running

case $1 in 
	start)
		echo "Starting homework-service at port 24026"
	  setsid java -jar ./homework-service.jar \
	  -Xmx 256m \
	  > /dev/null 2>&1 < /dev/null &
		;;
	stop)
		echo "Stopping homework-service"
		
		if $(running); then
			echo "homework-service is not running"
			exit 1;
		fi
		kill $PID
		;;
	status) 
		echo "homework-service status:"
		
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
