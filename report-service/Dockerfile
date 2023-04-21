FROM openjdk:17

EXPOSE 8080
RUN mkdir -p /app/

ADD build/libs/report-service-0.0.1-SNAPSHOT.jar /app/report-service.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=kubernetes", "-jar", "/app/report-service.jar"]
