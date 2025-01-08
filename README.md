# NEDSS (National Electronic Disease Surveillance System) Data Ingestion Service Overview
Data Ingestion(DI) API is part of NEDSS modernization initiative. It facilitates the ingestion and processing of electronic case reporting(`eCR`) and electronic lab case reporting (`elCR`) messages, ensuring efficient, reliable, and scalable workflows.

## Key API Endpoints
  - **POST /api/auth/token**: Create JWT Token using Keycloak credentials.
  - **POST /api/ecrs**: Submits eCR document to the DI API for ingestion.
  - **POST /api/elrs/{dlt-id}**: Re-injects dead letter ELR message to the DI API by `dlt-id`.
  - **GET /api/elrs/error-messages**: Returns all dead letter messages.
  - **GET /api/elrs**: Returns dead letter message by `dlt-id`.
  - **GET /api/elrs/status/{elr-id}**: Returns ELR message status by `elr-id`.
  - **GET /api/elrs/status-details/{elr-id}**: Returns detailed status associated to `elr-id`.
  - **POST /api/elrs**: Submits a plain text or XML converted HL7 message.
  - **POST /api/elrs/validate**: Validates where the message is valid HL7 message.
> **Note:** DI Service only excepts `eCR/elCR` messages in `XML` format.
## Requirements
1. **Operating Systems**: 
2. **Software Dependencies**:
3. **Cloud Resources**:
4. **Development Tools**:
5. **Environment Variables**:

   | Name                        | Description | Required |
   |-----------------------------|-------------|----------|
   | NBS_DBSERVER                |             | Yes      |
   | NBS_DBUSER                  |             | Yes      |
   | NBS_DBPASSWORD              |             | Yes      |
   | BOOTSTRAP_SERVERS           |             | ?        |
   | DI_LOG_PATH                 |             |          |
   | DI_AUTH_URI                 |             |          |
   | DI_SFTP_ENABLED             |             |          |
   | DI_SFTP_HOST                |             |          |
   | DI_SFTP_USER                |             |          |
   | DI_SFTP_PWD                 |             |          |
   | KC_BOOTSTRAP_ADMIN_USERNAME |             |          |
   | KC_BOOTSTRAP_ADMIN_PASSWORD |             |          |


     


## Local Development Setup

## Docker Environment Setup

# Running Application inside Docker
- Requirement
  - Java 21
    - Docker
      - Gradle

        - Building application with Docker
          - Create env file on the top directory and name it as dataingestion.env. Update the content below with appropriate value
            ```
              NBS_DBSERVER=value

              NBS_DBUSER=value
      
              NBS_DBPASSWORD=value
         
              BOOTSTRAP_SERVERS=value
            
              DI_LOG_PATH=value
         
              DI_AUTH_URI=value
    
              DI_SFTP_ENABLED=value
         
              DI_SFTP_HOST=value
    
              DI_SFTP_USER=value
    
              DI_SFTP_PWD=value

              KC_BOOTSTRAP_ADMIN_USERNAME=value
              
              KC_BOOTSTRAP_ADMIN_PASSWORD=value
          ```
          - Run "docker compose up -d"
          - If encounter gradle exception such as missing wrapper then run the following command
            - "gradle wrapper"

- Build project with gradle (no docker)
  - ./gradlew :hl7-parser:build
  - ./gradlew :data-ingestion-service:build
  - ./gradlew build

## Local development environment setup
Instructions for setting up a local development environment can be found in the [DevSetup.md](docs/DevSetup.md)

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
  - hl7-parser is greater than 80%.
  - data-ingestion-service is greater than 80%.
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