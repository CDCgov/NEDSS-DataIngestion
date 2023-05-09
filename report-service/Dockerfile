FROM amazoncorretto:17 as builder

#Copy project config
COPY gradle /usr/src/dataingestion/gradle
COPY gradlew /usr/src/dataingestion/gradlew
COPY build.gradle /usr/src/dataingestion/build.gradle
COPY settings.gradle /usr/src/dataingestion/settings.gradle

#Copy sources
COPY hl7-parser /usr/src/dataingestion/hl7-parser
COPY report-service /usr/src/dataingestion/report-service

COPY . /usr/src/dataingestion

#Build report service along with any required libraries
RUN ./gradlew :report-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:17

COPY --from=builder /usr/src/dataingestion/report-service/build/libs/report-service*.jar report-service.jar

# Run jar
CMD ["java", "-jar", "report-service.jar"]
#EXPOSE 8080
#RUN mkdir -p /app/
#
#ADD build/libs/report-service-0.0.1-SNAPSHOT.jar /app/report-service.jar
#
#ENTRYPOINT ["java", "-Dspring.profiles.active=kubernetes", "-jar", "/app/report-service.jar"]
