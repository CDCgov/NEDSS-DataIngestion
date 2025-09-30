# NEDSS-DataIngestion
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=CDCgov_NEDSS-DataIngestion&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=CDCgov_NEDSS-DataIngestion)

Data Ingestion for Modernization of NEDSS Project by Enquizit

## Prerequisites

To build and run the services, **Docker** is required.
To run the full system, you will also need **Docker Compose**, though it is not required
for building.

- [Install Docker](https://docs.docker.com/get-docker/) 
- [Optional] [Install Docker Compose](https://docs.docker.com/compose/install/) 

Additionally, building the services locally requires **Java 21**.

- [Install Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)

## Setup

Docker is used for building the application both locally and inside a container.
If you are using **Docker Desktop**, no further configuration is needed.

However, if you are running another container engine (such as **Podman** or **Colima**),
you may need to configure environment variables. Refer to the
[Testcontainers documentation](https://java.testcontainers.org/supported_docker_environment/)
for details on which variables to set in a local `.env` file.  A custom task has been
added to the root `build.gradle` file to automatically load environment variables declared
in the `.env` file into the JVM environment.


```bash
touch .env
$EDITOR .env
```

For running the services, you will need to create a `dataingestion.env` file with
environment variables required by the services.  You can copy the provided sample
file and update the values as needed.

```bash
> cp dataingestion.env.sample dataingestion.env
> $EDITOR dataingestion.env
```

Getting all of the services up and running in Docker Compose requires some additional
steps, please refer to the [DevSetup.md](docs/DevSetup.md) for details.

## Building / Testing

- Build the entire project: `./gradlew build`
- Build a specific service: `./gradlew :data-ingestion-service:build`
- Test the entire project: `./gradlew test`
- Test a specific service: `./gradlew :data-processing-service:test`
- Run all verification checks: `./gradlew check`

## Running the Application inside Docker

Use docker compose to run the services.

```bash
> docker compose up -d
```

**NOTE**: If you encounter gradle exception such as missing wrapper then run the following command
```bash
> gradle wrapper
```

## Building Docker image for EKS (1)
If you are on Mac OS Environnment, use Docker Buildx, so linux image can be built.  The following
command will build the image specified in the data-ingestion-service/Dockerfile.

```bash
> docker buildx  build --platform linux/amd64 -t <DOCKER_REPOS>/<IMAGE_NAME>:<VERSION> -f data-ingestion-service/Dockerfile . --push
```

## Deploy Docker image on EKS (2)
These steps assume EKS cluster already exist and running, and it is being manage by Helm
Charts. This command will create a new service if it does not exist, otherwise it will
update the existing one.

**Variables**:
  - `values-dev.yaml`: indicate value file, helm charts pull values such as environment variable from this file.
  - `jdbc.X=VALUE`: argument to pass the value in as an enviroment variable, this value is defined in values-dev.yaml.
  - `image.repository='VALUE'`: image repos, say if using registry other than docker hub. Ex: ECR


```bash
> helm upgrade --install dataingestion-service -f ./dataingestion-service/values-dev.yaml --set jdbc.dbserver='VALUE',jdbc.dbname='VALUE',jdbc.username='VALUE',jdbc.password='VALUE',jdbc.nbs.dbserver='VALUE',jdbc.nbs.dbname='VALUE',jdbc.nbs.username='VALUE',jdbc.nbs.password='VALUE',kafka.cluster='VALUE' dataingestion-service
```
For Helm Chart and EKS configuration, please refer to this
[NEDSS-Helm](https://github.com/CDCgov/NEDSS-Helm)
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

# SFTP ENV PARAMS
    DI_SFTP_ENABLED=value  - value should be 'enabled' or 'disabled'
    DI_SFTP_HOST=value - SFTP server host name
    DI_SFTP_USER=value
    DI_SFTP_PWD=value
    DI_SFTP_ELR_FILE_EXTNS=value - Comma separted list of file extensions (ex: txt,hl7)
    DI_PHCR_IMPORTER_VERSION=value - 1 for classic phcrImporter batch job, 2 for RTI
    DI_SFTP_FILEPATHS=value - Comma separted list of file extensions (ex: /ELRFiles,/ELRFiles/lab-1,/ELRFiles/lab-2)
