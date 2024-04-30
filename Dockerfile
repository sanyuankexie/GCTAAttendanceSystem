FROM mcr.microsoft.com/openjdk/jdk:11-ubuntu
WORKDIR /app
COPY build/libs/attendance.jar .

EXPOSE 8088
CMD [ "java", "-jar", "/app/attendance.jar"]