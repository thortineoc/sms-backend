#!/bin/bash

sudo apt-get update && \
sudo apt-get -y install openjdk-8-jdk && \
sudo apt -y install maven && \
sudo apt-get -y install --no-install-recommends software-properties-common &&\
sudo add-apt-repository ppa:vbernat/haproxy-2.0 &&\
sudo apt-get -y install haproxy=2.0.\* &&\
sudo cp ./docker/haproxy.cfg /etc/haproxy/haproxy.cfg && \
sudo systemctl restart haproxy && \
wget "https://github.com/keycloak/keycloak/releases/download/12.0.4/keycloak-12.0.4.tar.gz" && \
tar -xvf keycloak-12.0.4.tar.gz && \
rm -f keycloak-12.0.4.tar.gz && \
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
sudo chmod +x /usr/local/bin/docker-compose && \
sed -i "s/^[#]*\s*WEB_HOST=.*/WEB_HOST=localhost/" config/scripts/values.properties && \
sed -i "s/^[#]*\s*DATABASE_HOST=.*/DATABASE_HOST=localhost/" config/scripts/values.properties

#keycloak run
#setsid ./keycloak*/bin/standalone.sh -b0.0.0.0 -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=docker/keycloak_db.json -Dkeycloak.migration.strategy=OVERWRITE_EXISTING -Djboss.http.port=24080 -Djboss.management.http.port=24082  > /dev/null 2>&1 < /dev/null &

#setsid ./keycloak*/bin/standalone.sh -Djboss.http.port=24080 -Djboss.management.http.port=24082  > /dev/null 2>&1 < /dev/null &

