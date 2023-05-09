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

WORKDIR /usr/src/dataingestion

#Build report service along with any required libraries
RUN ./gradlew :report-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:17

COPY --from=builder /usr/src/dataingestion/report-service/build/libs/report-service*.jar report-service.jar

# Run jar
ENTRYPOINT ["java", "-jar", "report-service.jar"]
CMD ["java", "-jar", "report-service.jar"]
#CMD ["--spring.config.location=/report-service/application.yaml",
#"--DI_CONNECTION_URL=nbs-msgoute-20221213.czya31goozkz.us-east-1.rds.amazonaws.com",
#"--DI_DATABASE_NAME=NBS_DataIngest",
#"--DI_DBNAME=NBS_DataIngest",
#"--DI_DBSERVER=nbs-msgoute-20221213.czya31goozkz.us-east-1.rds.amazonaws.com:1433",
#"--DI_NBS_DATABASE_NAME=nbs_msgoute_dec232022",
#"--DI_NBS_DBNAME=nbs_msgoute_dec232022",
#"--DI_PASSWORD=90w8m4!ZIRAc",
#"--DI_USERNAME=admin"
#]

#EXPOSE 8080
#RUN mkdir -p /app/
#
#ADD build/libs/report-service-0.0.1-SNAPSHOT.jar /app/report-service.jar
#
#ENTRYPOINT ["java", "-Dspring.profiles.active=kubernetes", "-jar", "/app/report-service.jar"]
