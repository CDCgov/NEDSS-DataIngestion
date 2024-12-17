## Development environment setup
Following this guide will set up a fully functioning local development environment. You will be able to ingest and process Electronic Lab Reports (ELRs), debug the various services, and view the processed patient data within NBS 6.

### Building
1. Provide the necessary [configurations](#configuration)
1. Optional: Build and deploy NBS 6 Wildfly docker container (requires ability to git clone [NEDSSDev](https://github.com/cdcent/))
      ```bash
      ./containers/build_classic.sh
      ```
2. Start Keycloak, Kafka, SRTE data cache, and database container
      ```bash
      docker compose up di-keycloak broker zookeeper di-mssql srte-data-service -d
      ```
3. Start data-ingestion-service with gradle. Allows remote debugging using port `19040`
      ```bash
      ./gradlew data-ingestion-service:bootRun
      ```
4. Start data-processing-service with gradle. Allows remote debugging using port `19041`
      ```bash
      ./gradlew data-processing-service:bootRun
      ```
5. Optional: Start deduplication service with gradle. Allows remote debugging using port `19042` (requires running [Record Linkage service](https://github.com/CDCgov/RecordLinker))
      ```bash
      ./gradlew deduplication:bootRun
      ```

Once the above steps have been completed, the [postman collection](./DataIngestion.postman_collection.json) can be used to fetch an authentication token from Keycloak, submit an ELR, and check the status of the ELR. 

Swagger pages are also available
1. [data-ingestion-service](http://localhost:8081/ingestion/swagger-ui/index.html)
2. [data-processing-service](http://localhost:8082/rti/swagger-ui/index.html)
3. [deduplication](http://localhost:8083/swagger-ui/index.html)

If the optional NBS 6 WildFly container was built, NBS 6 can be accessed [here](http://localhost:7002/nbs/login).


### Configuration
The docker compose file supports pulling information from a `.dataingestion.env` file and each service supports creating an `application-local.yml`. Below are sample configuration files.
#### dataingestion.env - place at the project root, alongside the docker-compose.yml
```bash
DI_AUTH_URI=http://di-keycloak:8080/realms/NBS;
RTI_CACHE_AUTH_URI=http://di-keycloak:8080/realms/NBS

NBS_DBSERVER=di-mssql:1433
NBS_DBUSER=sa
NBS_DBPASSWORD=fake.fake.fake.1234
KC_BOOTSTRAP_ADMIN_USERNAME=admin
KC_BOOTSTRAP_ADMIN_PASSWORD=fake.fake.fake.1234
```

#### data-ingestion-service/src/main/resources/application-local.yaml
```yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8100/realms/NBS
  datasource:
    username: sa
    password: fake.fake.fake.1234
    dataingest:
      url: jdbc:sqlserver://localhost:2433;databaseName=NBS_DATAINGEST;encrypt=true;trustServerCertificate=true;
    msgoute:
      url: jdbc:sqlserver://localhost:2433;databaseName=NBS_MSGOUTE;encrypt=true;trustServerCertificate=true;
    odse:
      url: jdbc:sqlserver://localhost:2433;databaseName=NBS_ODSE;encrypt=true;trustServerCertificate=true;
```

#### data-processing-service/src/main/resources/application-local.yaml
```yml
spring:
  datasource:
    username: sa
    password: fake.fake.fake.1234
    nbs:
      url: jdbc:sqlserver://localhost:2433;databaseName=NBS_MSGOUTE;encrypt=true;trustServerCertificate=true;
    odse:
      url: jdbc:sqlserver://localhost:2433;databaseName=NBS_ODSE;encrypt=true;trustServerCertificate=true;
    srte:
      url: jdbc:sqlserver://localhost:2433;databaseName=NBS_SRTE;encrypt=true;trustServerCertificate=true;

features:
  modernizedMatching:
    enabled: true
    url: http://localhost:8083/api/deduplication/

cache:
  clientId: di-keycloak-client
  secret: OhBq1ar96aep8cnirHwkCNfgsO9yybZI
  token: http://localhost:8084/data/api/auth/token
  srte:
    cacheString: http://localhost:8084/data/srte/cache/string
    cacheContain: http://localhost:8084/data/srte/cache/contain
    cacheObject: http://localhost:8084/data/srte/cache/object
  odse:
    localId: http://localhost:8084/data/odse/localId

```

#### deduplication/src/main/resources/application-local.yaml
```yml
spring:
  datasource:
    deduplication:
      url: jdbc:sqlserver://localhost:2433;database=deduplication;encrypt=true;trustServerCertificate=true;
      username: sa
      password: fake.fake.fake.1234
    nbs:
      url: jdbc:sqlserver://localhost:2433;database=nbs_odse;encrypt=true;trustServerCertificate=true;
      username: sa
      password: fake.fake.fake.1234
    mpi:
      url: jdbc:postgresql://localhost:5432/postgres
      username: postgres
      password: pw
  batch:
    jdbc:
      initialize-schema: always

logging:
  level:
    org.springframework.jdbc.core.JdbcTemplate: debug
```

### Remote debugging in VS Code
The data-ingestion-service and data-processing-service have been configured to allow remote debugging when started through `./gradlew <service-name>:bootRun`. The following `launch.json` will enable VS-Code to connect to these services for debugging.
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Data Ingestion (Attach)",
      "projectName": "data-ingestion-service",
      "request": "attach",
      "hostName": "localhost",
      "port": 19040
    },
    {
      "type": "java",
      "name": "Data Processing (Attach)",
      "projectName": "data-processing-service",
      "request": "attach",
      "hostName": "localhost",
      "port": 19041
    },
    {
      "type": "java",
      "name": "Deduplication (Attach)",
      "projectName": "Modernization-API",
      "request": "attach",
      "hostName": "localhost",
      "port": 19042
    }
  ]
}
```