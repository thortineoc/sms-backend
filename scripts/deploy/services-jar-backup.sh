#!/bin/bash

mkdir /home/SMS/scripts/services-backup

cp /home/SMS/subsystems/backend/grades-service/grades-service.jar /home/SMS/scripts/services-backup/
cp /home/SMS/subsystems/backend/homework-service/homework-service.jar /home/SMS/scripts/services-backup/
cp /home/SMS/subsystems/backend/presence-service/presence-service.jar /home/SMS/scripts/services-backup/
cp /home/SMS/subsystems/backend/timetable-service/timetable-service.jar /home/SMS/scripts/services-backup/
cp /home/SMS/subsystems/backend/usermanagement-service/usermanagement-service.jar /home/SMS/scripts/services-backup/

