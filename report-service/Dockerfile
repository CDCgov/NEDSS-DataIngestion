FROM gradle:7.6-jdk17 AS gradle_build
COPY src /home/app/src
COPY build.gradle /home/app
COPY settings.gradle /home/app
WORKDIR /home/app
RUN gradle --no-daemon build

FROM openjdk:17 AS run_gradle_build
RUN mkdir -p /app
COPY --from=gradle_build /home/app/build/libs/report-service-0.0.1-SNAPSHOT.jar /app/report-service.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=kubernetes", "-jar", "/app/report-service.jar"]
