#!/bin/bash

# Pull new changes
git pull

# Prepare Jar
mvn clean
mvn package

# Ensure, that docker-compose stopped
docker-compose stop

# Add environment variables
export BOT_USERNAME=$1
export BOT_TOKEN=$2
export BOT_DB_USERNAME=$3
export BOT_DB_PASSWORD=$4

# Start new deployment
docker-compose up --build -d