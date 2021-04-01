#!/bin/bash

PID=""
running() {
	PID=$(ps aux | grep "java.*presence-service" | grep -v "grep" | awk '{print $2}')
	[[ $PID == "" ]]
}

running

case $1 in 
	start)
		echo "Starting presence-service at port 24028"
	  setsid java -jar ./presence-service.jar \
	  -Xmx 256m \
	  > /dev/null 2>&1 < /dev/null &
		;;
	stop)
		echo "Stopping presence-service"
		
		if $(running); then
			echo "presence-service is not running"
			exit 1;
		fi
		kill $PID
		;;
	status) 
		echo "presence-service status:"
		
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
