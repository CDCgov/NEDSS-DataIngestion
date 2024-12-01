FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml ./
COPY src src

RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8081

# Run the application
CMD ["java", "-jar", "/app/app.jar"]
