FROM amazoncorretto:21 as builder
WORKDIR /usr/src/srtedataservice
#Copy project config
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .


#Copy sources
COPY srte-data-service srte-data-service

COPY . /usr/src/srtedataservice

WORKDIR /usr/src/srtedataservice

#Build report service along with any required libraries
RUN ./gradlew :srte-data-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:21

COPY --from=builder /usr/src/srtedataservice/srte-data-service/build/libs/srte-data-service*.jar srte-data-service.jar

# Run jar
ENV JAVA_XMS=1024m
ENV JAVA_XMX=2048m

ENTRYPOINT ["sh", "-c", "java -Xms$JAVA_XMS -Xmx$JAVA_XMX -jar srte-data-service.jar"]
