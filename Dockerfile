FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu AS builder

WORKDIR /workspace

# Copy all files from the current directory to the workspace
COPY . .

# Build the project
RUN chmod +x ./gradlew && ./gradlew bootJar

# Stage 2: Create final image
FROM mcr.microsoft.com/java/jre:17-zulu-alpine

WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=builder /workspace/build/libs/attendance.jar .

# Expose port 8088
EXPOSE 8088

# Set the default command to run when the container starts
CMD [ "java", "-jar", "/app/attendance.jar"]
