FROM amazoncorretto:25 as builder

# try patching system for security fixes
RUN yum update -y && yum clean all

#Copy project config
COPY gradle /usr/src/dataingestion/gradle
COPY gradlew /usr/src/dataingestion/gradlew
COPY build.gradle /usr/src/dataingestion/build.gradle
COPY settings.gradle /usr/src/dataingestion/settings.gradle

#Copy sources
COPY hl7-parser /usr/src/dataingestion/hl7-parser
COPY cdaschema /usr/src/dataingestion/cdaschema
COPY data-ingestion-service /usr/src/dataingestion/data-ingestion-service

COPY . /usr/src/dataingestion

WORKDIR /usr/src/dataingestion

#Build report service along with any required libraries
RUN ./gradlew :data-ingestion-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:25

# try patching system for security fixes
RUN yum update -y && yum clean all

COPY --from=builder /usr/src/dataingestion/data-ingestion-service/build/libs/data-ingestion-service*.jar data-ingestion-service.jar

# Run jar
ENTRYPOINT ["java", "-jar", "data-ingestion-service.jar"]
CMD ["java", "-jar", "data-ingestion-service.jar"]
