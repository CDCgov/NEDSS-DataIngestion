# NEDSS-DataIngestion
Data Ingestion for Modernization of NEDSS Project by Enquizit

# Running Application inside Docker
- Requirement
    - Java 17
    - Docker
    - Gradle

- Building application with Docker
    - Create env file on the top directory and name it as dataingestion.env. Update the content beloew with approriate value
      ```
           DI_DBSERVER=value

           DI_DBNAME=value
        
           DI_USERNAME=value
           
           DI_PASSWORD=value
           
           DI_NBS_DBSERVER=value
           
           DI_NBS_DBNAME=value
           
           DI_NBS_DBUSER=value
           
           DI_NBS_DBPASSWORD=value
           ```
    - Run "docker-compose up -d"
    - If encounter gradle exception such as missing wrapper then run the following command
        - "gradle wrapper"

- Build project with gradle (no docker)
    - ./gradlew :hl7-parser:build
    - ./gradlew :report-service:build
    - ./gradlew build


