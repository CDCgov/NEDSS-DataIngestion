FROM amazoncorretto:21 as builder
WORKDIR /usr/src/rticache
#Copy project config
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .


#Copy sources
COPY rti-cache rti-cache

COPY . /usr/src/rticache

WORKDIR /usr/src/rticache

#Build report service along with any required libraries
RUN ./gradlew :rti-cache:buildNeeded -x test --no-daemon

FROM amazoncorretto:21

COPY --from=builder /usr/src/rticache/rti-cache/build/libs/rti-cache*.jar rti-cache.jar

# Run jar
ENV JAVA_XMS=1024m
ENV JAVA_XMX=2048m

ENTRYPOINT ["sh", "-c", "java -Xms$JAVA_XMS -Xmx$JAVA_XMX -jar rti-cache.jar"]
