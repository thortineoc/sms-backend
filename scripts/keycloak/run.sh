#!/bin/bash

PID=""
running() {
	PID=$(ps aux | grep "java.*keycloak" | grep -v "grep" | awk '{print $2}')
	[[ $PID == "" ]]
}

running

case $1 in 
	start)
		echo "Starting keycloak at port 24080 "
	
		setsid ./keycloak*/bin/standalone.sh \
		-Djboss.http.port=24080 \
		-Djboss.management.http.port=24082 \
		> /dev/null 2>&1 < /dev/null &	
		;;
	stop)
		echo "Stopping keycloak"
		
		if $(running); then
			echo "Keycloak is not running"
			exit 1;
		fi
		kill $PID
		;;
	status) 
		echo "Keycloak status:"
		
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
