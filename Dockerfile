FROM mcr.microsoft.com/java/jre:11-zulu-alpine
WORKDIR /app
COPY build/libs/attendance.jar .

EXPOSE 8088
CMD [ "java", "-jar", "/app/attendance.jar"]