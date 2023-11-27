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

         DI_LOG_PATH=value
         ```
  - Run "docker-compose up -d"
  - If encounter gradle exception such as missing wrapper then run the following command
    - "gradle wrapper"

- Build project with gradle (no docker)
  - ./gradlew :hl7-parser:build
  - ./gradlew :data-ingestion-service:build
  - ./gradlew build

# Building Docker image for EKS (1)
- If you are on Mac OS Environnment, look into Docker Buildx, so linux image can be built
- This example assume local machine run Mac OS.
- ```Run "docker buildx  build --platform linux/amd64 -t <DOCKER_REPOS>/<IMAGE_NAME>:<VERSION> -f data-ingestion-service/Dockerfile . --push"```
    - This command look for Dockerfile inside data-ingestion-service directory and build image from the top level of the project's hierarchy 

# Deploy Docker image on EKS (2)
- These steps assume EKS cluster already exist and running, and it is being manage by Helm Charts 
- ```Run "helm upgrade --install dataingestion-service -f ./dataingestion-service/values-dev.yaml --set jdbc.dbserver='VALUE',jdbc.dbname='VALUE',jdbc.username='VALUE',jdbc.password='VALUE',jdbc.nbs.dbserver='VALUE',jdbc.nbs.dbname='VALUE',jdbc.nbs.username='VALUE',jdbc.nbs.password='VALUE',kafka.cluster='VALUE' dataingestion-service"```
    - What this command does is create new if not service not exist and update existing one if it exists.
    -  values-dev.yaml: indicate value file, helm charts pull values such as enviroment variable from this file.
    -  --set jdb.dbserver='VALUE': argument to pass value into enviroment variable, this value is defined in values-dev.yaml.
    -  --set image.repository='VALUE': image repos, say if using registry other than docker hub. Ex: ECR
    -  Docker Image need to be specify in values-dev.yaml
    -  For Helm Chart and EKS configuration, please refer to this [NEDSS-Helm](https://github.com/CDCgov/NEDSS-Helm)
- Other useful commands
    -  ```helm delete <SERVICE-NAME>```: delete service
    -  ```kubectl exec -it <POD-ID>  -- /bin/bash``` : access pod environment
    -  ```kubectl get pods```
    -  ```kubectl describe pod <POD-ID>```: get pod info, useful to inspect configuration and debug
    -  ```kubectl logs <POD-ID>```
     
# Unit Testing and Code Coverage
- Requirement:
  - Code coverage must be greater than 90%
- Progress:
  - HL7-parser at 91%.
  - Report-service at 93%.
    - Excluding classes and files.
      - Unused model classes in Jaxb package
        - models in this package are generated after built based on given xml definition
        - Unused model classes
          -    AnswerType
          -    CaseType
          -    ClinicalInformationType
          -    CodedType
          -    CommonQuestionsType
          -    DiseaseSpecificQuestionsType
          -    EpidemiologicInformationType
          -    HeaderType
          -    HierarchicalDesignationType
          -    HL7NumericType
          -    HL7OBXValueType
          -    HL7SNType
          -    HL7TMType
          -    IdentifiersType
          -    IdentifierType
          -    InvestigationInformationType
          -    LabReportCommmenType
          -    LabReportType
          -    NameType
          -    NoteType
          -    NumericType
          -    ObjectFactory
          -    ObservationsType
          -    ObservationType
          -    OrganizationParticipantType
          -    ParticipantsType
          -    PatientType
          -    PostalAddressType
          -    ProviderNameType
          -    ProviderParticipantType
          -    ReferenceRangeType
          -    ReportingInformationType
          -    SectionHeaderType
          -    SpecimenType
          -    SusceptibilityType
          -    TelephoneType
          -    TestResultType
          -    TestsType
          -    UnstructuredType
          -    ValuesType
      - Configuration classes
        -  DataSourceConfig
        -  NbsDataSourceConfig
        -  OpenAPIConfig
        -  SecurityConfig