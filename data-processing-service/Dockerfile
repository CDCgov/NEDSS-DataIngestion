FROM amazoncorretto:17 as builder

#Copy project config
COPY gradle /usr/src/dataprocessing/gradle
COPY gradlew /usr/src/dataprocessing/gradlew
COPY build.gradle /usr/src/dataprocessing/build.gradle
COPY settings.gradle /usr/src/dataprocessing/settings.gradle

#Copy sources
COPY hl7-parser /usr/src/dataprocessing/hl7-parser
#COPY cdaschema /usr/src/dataingestion/cdaschema
COPY data-processing-service /usr/src/dataprocessing/data-processing-service

COPY . /usr/src/dataprocessing

WORKDIR /usr/src/dataprocessing

#Build report service along with any required libraries
RUN ./gradlew :data-processing-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:17

COPY --from=builder /usr/src/dataprocessing/data-processing-service/build/libs/data-processing-service*.jar data-processing-service.jar

# Run jar
ENTRYPOINT ["java", "-jar", "data-processing-service.jar"]
CMD ["java", "-jar", "data-processing-service.jar"]
