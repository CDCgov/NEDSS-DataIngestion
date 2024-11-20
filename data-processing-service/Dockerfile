FROM amazoncorretto:21 as builder

#Copy project config
COPY gradle /usr/src/dataprocessing/gradle
COPY gradlew /usr/src/dataprocessing/gradlew
COPY build.gradle /usr/src/dataprocessing/build.gradle
COPY settings.gradle /usr/src/dataprocessing/settings.gradle

#Copy sources
COPY data-processing-service /usr/src/dataprocessing/data-processing-service

COPY . /usr/src/dataprocessing

WORKDIR /usr/src/dataprocessing

#Build report service along with any required libraries
RUN ./gradlew :data-processing-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:21

COPY --from=builder /usr/src/dataprocessing/data-processing-service/build/libs/data-processing-service*.jar data-processing-service.jar

# Run jar
#ENTRYPOINT ["java", "-jar", "data-processing-service.jar"]
#CMD ["java", "-jar", "data-processing-service.jar"]
ENTRYPOINT ["java", "-Xms4g", "-Xmx4g", "-jar", "data-processing-service.jar"]
