# NEDSS-DataIngestion
Data Ingestion for Modernization of NEDSS Project by Enquizit

# Running Application inside Docker
- Requirement
    - Java 17
    - Docker
    - Gradle

- Building application with Docker
    - Create env file on the top directory and name it as dataingestion.env. Update the content beloew with approriate value
        -  DI_CONNECTION_URL=connection_url
           DI_DATABASE_NAME=database_name
           DI_DBNAME=database_naem
           DI_DBSERVER=connection_url
           DI_NBS_DATABASE_NAME=database_naem
           DI_NBS_DBNAME=database_name
           DI_PASSWORD=database_password
           DI_USERNAME=database_username
           KAFKA_SERVER=kafka_connection_url
    - Run "docker-compose up -d"
    - If encounter gradle exception such as missing wrapper then run the following command
        - "gradle wrapper"

- Build project with gradle (no docker)
    - ./gradlew :hl7-parser:build
    - ./gradlew :report-service:build
    - ./gradlew build


