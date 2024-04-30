#!/bin/bash
#chmod +x ./gradlew && ./gradlew fatjar

docker run --rm \
-v "$PWD:/workspace" \
-w /workspace \
mcr.microsoft.com/openjdk/jdk:17-ubuntu \
/bin/bash -c "chmod +x ./gradlew && ./gradlew bootJar"

docker build -t kexie-attendance .

# docker run --name kexie-attendance --network=host --rm kexie-attendance