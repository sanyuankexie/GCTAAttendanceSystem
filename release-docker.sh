#!/bin/bash
chmod +x ./gradlew && ./gradlew fatjar
docker build -t kexie-attendance .