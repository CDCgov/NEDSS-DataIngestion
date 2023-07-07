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