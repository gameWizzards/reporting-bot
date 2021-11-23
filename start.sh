#!/bin/bash

# Ensure, that docker-compose stopped
docker-compose stop

# Start new deployment
docker-compose up --build -d