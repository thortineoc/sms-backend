#!/bin/bash

scp $1 SMS@52.142.201.18:/home/SMS/subsystems/haproxy \
&& ssh -t SMS@52.142.201.18 "sudo /home/SMS/subsystems/haproxy/restart.sh"