#!/bin/bash

apt-get update && \
apt-get -y install openjdk-8-jdk && \
apt -y install maven && \
curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
chmod +x /usr/local/bin/docker-compose && \
sed -i "s/^[#]*\s*WEB_HOST=.*/WEB_HOST=localhost/" config/scripts/values.properties && \
sed -i "s/^[#]*\s*DATABASE_HOST=.*/DATABASE_HOST=localhost/" config/scripts/values.properties && \
mvn clean install -Pbuild