FROM amazoncorretto:25 as builder
# try patching system for security fixes
RUN yum update -y && yum clean all

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

FROM amazoncorretto:25
# try patching system for security fixes
RUN yum update -y && yum clean all

COPY --from=builder /usr/src/dataprocessing/data-processing-service/build/libs/data-processing-service*.jar data-processing-service.jar

# Run jar
#ENTRYPOINT ["java", "-jar", "data-processing-service.jar"]
#CMD ["java", "-jar", "data-processing-service.jar"]
ENV JAVA_XMS=1024m
ENV JAVA_XMX=2048m

ENTRYPOINT ["sh", "-c", "java -Xms$JAVA_XMS -Xmx$JAVA_XMX -jar data-processing-service.jar"]
