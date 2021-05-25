#!/bin/bash

apt-get update && \
apt-get install openjdk-8-jdk && \
sed -i "s/^[#]*\s*WEB_HOST=.*/WEB_HOST=localhost/" config/scripts/values.properties && \
sed -i "s/^[#]*\s*DATABASE_HOST=.*/DATABASE_HOST=localhost/" config/scripts/values.properties && \
mvn clean install -Pbuild