#!/bin/bash

echo "Restoring all services"

/home/SMS/scripts/restore-service-backup.sh grades-service
/home/SMS/scripts/restore-service-backup.sh homework-service
/home/SMS/scripts/restore-service-backup.sh presence-service
/home/SMS/scripts/restore-service-backup.sh timetable-service
/home/SMS/scripts/restore-service-backup.sh usermanagement-service
