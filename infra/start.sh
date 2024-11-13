#!/bin/bash

cd ../

# Run Maven build
echo "Building the application ..."

if ! mvn clean package -Dnative -Dquarkus.native.container-build=true; then
    echo "Maven build failed. Please check the output for errors."
    exit 1
fi

echo "Maven build completed successfully."

echo "Building application docker image"
docker build -t local/dds .

echo "Docker image build completed successfully."

echo "Starting application"
pwd
docker-compose -f ./infra/docker-compose.yml up