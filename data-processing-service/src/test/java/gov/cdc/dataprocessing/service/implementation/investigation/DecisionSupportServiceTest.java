package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.*;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxLabIdentiferDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationReasonDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.DsmAlgorithm;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationReason;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PublicHealthCaseStoredProcRepository;
import gov.cdc.dataprocessing.service.interfaces.IAutoInvestigationService;
import gov.cdc.dataprocessing.utilities.component.EdxPhcrDocumentUtil;
import gov.cdc.dataprocessing.utilities.component.ValidateDecisionSupport;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DecisionSupportServiceTest {

    DsmAlgorithmService dsmAlgorithmService;

    DecisionSupportService decisionSupportService;

    EdxPhcrDocumentUtil edxPhcrDocumentUtil;
    IAutoInvestigationService autoInvestigationService;
    ValidateDecisionSupport validateDecisionSupport;
    PublicHealthCaseStoredProcRepository publicHealthCaseStoredProcRepository;

    @BeforeEach
    void setup() {
        dsmAlgorithmService = mock(DsmAlgorithmService.class);
        edxPhcrDocumentUtil = mock(EdxPhcrDocumentUtil.class);
        autoInvestigationService = mock(AutoInvestigationService.class);
        validateDecisionSupport = mock(ValidateDecisionSupport.class);
        publicHealthCaseStoredProcRepository = mock(PublicHealthCaseStoredProcRepository.class);
    }

    @Test
    void validateProxyContainerSuccess() throws DataProcessingException {

        when(dsmAlgorithmService.findActiveDsmAlgorithm()).thenReturn(getAlgorithms());

        decisionSupportService = new DecisionSupportService(edxPhcrDocumentUtil, autoInvestigationService, validateDecisionSupport, publicHealthCaseStoredProcRepository, dsmAlgorithmService);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        LabResultProxyContainer container = getLabResultProxy1();

        EdxLabInformationDto result = decisionSupportService.validateProxyContainer(container, edxLabInformationDto);

        assertNotNull(result);
    }

    @Test
    void validateProxyContainerSuccess1() throws DataProcessingException {

        when(dsmAlgorithmService.findActiveDsmAlgorithm()).thenReturn(getAlgorithmsAnd());

        decisionSupportService = new DecisionSupportService(edxPhcrDocumentUtil, autoInvestigationService, validateDecisionSupport, publicHealthCaseStoredProcRepository, dsmAlgorithmService);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        LabResultProxyContainer container = getLabResultProxy();

        EdxLabInformationDto result = decisionSupportService.validateProxyContainer(container, edxLabInformationDto);

        assertNotNull(result);
    }

    @Test
    void validateProxyContainerFailure() throws DataProcessingException {
        when(dsmAlgorithmService.findActiveDsmAlgorithm()).thenReturn(new ArrayList<DsmAlgorithm>());

        decisionSupportService = new DecisionSupportService(edxPhcrDocumentUtil, autoInvestigationService, validateDecisionSupport, publicHealthCaseStoredProcRepository, dsmAlgorithmService);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        LabResultProxyContainer container = getLabResultProxy();

        EdxLabInformationDto result = decisionSupportService.validateProxyContainer(container, edxLabInformationDto);

        assertEquals("No WDS Algorithm found", result.getWdsReports().get(0).getMessage());
    }

    private Collection<ObservationContainer> getObsContainers1() {
        ObservationContainer observationContainer = new ObservationContainer();

        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(10002301L);
        observationDto.setAddUserId(123L);
        observationDto.setAltCd("080186");
        observationDto.setAltCdDescTxt("CULTURE");
        observationDto.setAltCdSystemCd("L");
        observationDto.setAltCdSystemDescTxt("LOCAL");
        observationDto.setCd("77190-7");
        observationDto.setCdDescTxt("ORGANISM COUNT");
        observationDto.setCdSystemCd("LN");
        observationDto.setCdSystemDescTxt("LOINC");
        observationDto.setCtrlCdDisplayForm("LabReport");
        observationDto.setJurisdictionCd("130001");
        observationDto.setObsDomainCd("LabReport");
        observationDto.setObsDomainCdSt1("Result");
        observationDto.setProgAreaCd("HEP");
        observationDto.setRecordStatusCd("ACTIVE");
        observationDto.setStatusCd("D");
        observationDto.setTargetSiteCd("LA");
        observationDto.setProgramJurisdictionOid(1300100011L);
        observationDto.setSuperClassType("Act");

        observationContainer.setTheObservationDto(observationDto);

        Collection<ActIdDto> actIdDtos = new ArrayList<>();

        ActIdDto actIdDto = new ActIdDto();
        actIdDto.setActUid(10002031L);
        actIdDto.setActIdSeq(1);
        actIdDto.setAssigningAuthorityCd("CLIA");
        actIdDto.setAssigningAuthorityDescTxt("Clinical Laboratory Improvement Amendment");
        actIdDto.setRecordStatusCd("ACTIVE");
        actIdDto.setRootExtensionTxt("20120509010020114_251.2");
        actIdDto.setTypeCd("MCID");
        actIdDto.setTypeDescTxt("Message Control ID");

        actIdDtos.add(actIdDto);

        ActIdDto actIdDto1 = new ActIdDto();
        actIdDto1.setActUid(10002031L);
        actIdDto1.setActIdSeq(2);
        actIdDto1.setAssigningAuthorityCd("CLIA");
        actIdDto1.setAssigningAuthorityDescTxt("Clinical Laboratory Improvement Amendment");
        actIdDto1.setRecordStatusCd("ACTIVE");
        actIdDto1.setRootExtensionTxt("20120601114");
        actIdDto1.setTypeCd("FN");
        actIdDto1.setTypeDescTxt("Filler Number");

        actIdDtos.add(actIdDto1);
        observationContainer.setTheActIdDtoCollection(actIdDtos);

        ObservationReasonDto observationReasonDto = new ObservationReasonDto();
        observationReasonDto.setObservationUid(10002031L);
        observationReasonDto.setReasonCd("12365-4");
        observationReasonDto.setReasonDescTxt("TOTALLY CRAZY");

        Collection<ObservationReasonDto> observationReasonDtos = new ArrayList<>();
        observationReasonDtos.add(observationReasonDto);

        observationContainer.setTheObservationReasonDtoCollection(observationReasonDtos);

        Collection<ObservationContainer> result = new ArrayList<>();
        result.add(observationContainer);

        return result;
    }

    private Collection<ObservationContainer> getObsContainers() {
        ObservationContainer observationContainer = new ObservationContainer();

        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(10002301L);
        observationDto.setAddUserId(123L);
        observationDto.setAltCd("080186");
        observationDto.setAltCdDescTxt("CULTURE");
        observationDto.setAltCdSystemCd("L");
        observationDto.setAltCdSystemDescTxt("LOCAL");
        observationDto.setCd("77190-7");
        observationDto.setCdDescTxt("ORGANISM COUNT");
        observationDto.setCdSystemCd("LN");
        observationDto.setCdSystemDescTxt("LOINC");
        observationDto.setCtrlCdDisplayForm("LabReport");
        observationDto.setJurisdictionCd("130001");
        observationDto.setObsDomainCd("LabReport");
        observationDto.setObsDomainCdSt1("Result");
        observationDto.setProgAreaCd("HEP");
        observationDto.setRecordStatusCd("ACTIVE");
        observationDto.setStatusCd("D");
        observationDto.setTargetSiteCd("LA");
        observationDto.setProgramJurisdictionOid(1300100011L);
        observationDto.setSuperClassType("Act");

        observationContainer.setTheObservationDto(observationDto);

        Collection<ActIdDto> actIdDtos = new ArrayList<>();

        ActIdDto actIdDto = new ActIdDto();
        actIdDto.setActUid(10002031L);
        actIdDto.setActIdSeq(1);
        actIdDto.setAssigningAuthorityCd("CLIA");
        actIdDto.setAssigningAuthorityDescTxt("Clinical Laboratory Improvement Amendment");
        actIdDto.setRecordStatusCd("ACTIVE");
        actIdDto.setRootExtensionTxt("20120509010020114_251.2");
        actIdDto.setTypeCd("MCID");
        actIdDto.setTypeDescTxt("Message Control ID");

        actIdDtos.add(actIdDto);

        ActIdDto actIdDto1 = new ActIdDto();
        actIdDto1.setActUid(10002031L);
        actIdDto1.setActIdSeq(2);
        actIdDto1.setAssigningAuthorityCd("CLIA");
        actIdDto1.setAssigningAuthorityDescTxt("Clinical Laboratory Improvement Amendment");
        actIdDto1.setRecordStatusCd("ACTIVE");
        actIdDto1.setRootExtensionTxt("20120601114");
        actIdDto1.setTypeCd("FN");
        actIdDto1.setTypeDescTxt("Filler Number");

        actIdDtos.add(actIdDto1);
        observationContainer.setTheActIdDtoCollection(actIdDtos);

        ObservationReasonDto observationReasonDto = new ObservationReasonDto();
        observationReasonDto.setObservationUid(10002031L);
        observationReasonDto.setReasonCd("12365-4");
        observationReasonDto.setReasonDescTxt("TOTALLY CRAZY");

        Collection<ObservationReasonDto> observationReasonDtos = new ArrayList<>();
        observationReasonDtos.add(observationReasonDto);

        observationContainer.setTheObservationReasonDtoCollection(observationReasonDtos);

        Collection<ObservationContainer> result = new ArrayList<>();
        result.add(observationContainer);

        return result;
    }

    private LabResultProxyContainer getLabResultProxy() {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        labResultProxyContainer.setTheObservationContainerCollection(getObsContainers());
        labResultProxyContainer.setTheMaterialContainerCollection(getMaterialContainers());
        labResultProxyContainer.setTheRoleDtoCollection(getRoles());
        labResultProxyContainer.setEDXDocumentCollection(getEdxDocumentCollection());
        labResultProxyContainer.setThePersonContainerCollection(getPersonContainer());
        labResultProxyContainer.setTheParticipationDtoCollection(getParticipationCollection());
        labResultProxyContainer.setTheActRelationshipDtoCollection(getActRelationshipCollection());
        labResultProxyContainer.setTheOrganizationContainerCollection(getOrganizationCollection());
        labResultProxyContainer.setItNew(true);

        return labResultProxyContainer;
    }

    private LabResultProxyContainer getLabResultProxy1() {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        labResultProxyContainer.setTheObservationContainerCollection(getObsContainers1());
        labResultProxyContainer.setTheMaterialContainerCollection(getMaterialContainers());
        labResultProxyContainer.setTheRoleDtoCollection(getRoles());
        labResultProxyContainer.setEDXDocumentCollection(getEdxDocumentCollection());
        labResultProxyContainer.setThePersonContainerCollection(getPersonContainer());
        labResultProxyContainer.setTheParticipationDtoCollection(getParticipationCollection());
        labResultProxyContainer.setTheActRelationshipDtoCollection(getActRelationshipCollection());
        labResultProxyContainer.setTheOrganizationContainerCollection(getOrganizationCollection());
        labResultProxyContainer.setItNew(true);

        return labResultProxyContainer;
    }

    private Collection<OrganizationContainer> getOrganizationCollection() {
        OrganizationContainer organizationContainer = new OrganizationContainer();

        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setOrganizationUid(10003022L);
        organizationDto.setCd("ORG");
        organizationDto.setCdDescTxt("Laboratory");
        organizationDto.setLocalId("ORG10003022GA01");
        organizationDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        organizationDto.setStandardIndustryClassCd("CLIA");
        organizationDto.setStandardIndustryDescTxt("LABCORP");
        organizationDto.setStatusCd("A");
        organizationDto.setDisplayNm("LABCORP");
        organizationDto.setElectronicInd("Y");
        organizationDto.setSuperClassType("Entity");

        organizationContainer.setTheOrganizationDto(organizationDto);

        Collection<OrganizationContainer> organizationContainers = new ArrayList<>();
        organizationContainers.add(organizationContainer);

        return organizationContainers;
    }

    private Collection<ActRelationshipDto> getActRelationshipCollection() {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        actRelationshipDto.setStatusCd("A");
        actRelationshipDto.setTypeDescTxt("Has Component");
        actRelationshipDto.setTargetActUid(10002033L);
        actRelationshipDto.setSourceActUid(10002034L);
        actRelationshipDto.setSourceClassCd("OBS");
        actRelationshipDto.setTargetClassCd("OBS");
        actRelationshipDto.setTypeCd("COMP");
        actRelationshipDto.setItNew(true);

        Collection<ActRelationshipDto> actRelationshipDtos = new ArrayList<>();
        actRelationshipDtos.add(actRelationshipDto);

        return actRelationshipDtos;
    }

    private Collection<ParticipationDto> getParticipationCollection() {
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setAddReasonCd("because");
        participationDto.setAddUserId(123L);
        participationDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        participationDto.setStatusCd("A");
        participationDto.setTypeCd("AUT");
        participationDto.setTypeDescTxt("Author");
        participationDto.setCd("SF");
        participationDto.setActClassCd("OBS");
        participationDto.setSubjectClassCd("ORG");
        participationDto.setActUid(10002033L);
        participationDto.setItNew(true);

        Collection<ParticipationDto> result = new ArrayList<>();
        result.add(participationDto);

        return result;
    }

    private PersonContainer getPerson() {
        PersonContainer personContainer = new PersonContainer();

        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(10092397L);
        personDto.setPersonParentUid(10092397L);
        personDto.setAddReasonCd("because");
        personDto.setAddUserId(123L);
        personDto.setCd("PAT");
        personDto.setCdDescTxt("Observation Subject");
        personDto.setCurrSexCd("F");
        personDto.setElectronicInd("Y");
        personDto.setLastChgUserId(123L);
        personDto.setLocalId("PSN10092397GA01");
        personDto.setMothersMaidenNm("MOTHER PATERSON");
        personDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        personDto.setStatusCd("A");
        personDto.setFirstNm("FIRSTMAX251");
        personDto.setLastNm("Windler");

        personContainer.setThePersonDto(personDto);

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setPersonNameSeq(1);
        personNameDto.setAddReasonCd("Add");
        personNameDto.setAddUserId(123L);
        personNameDto.setFirstNm("FIRSTMAX251");
        personNameDto.setLastChgUserId(123L);
        personNameDto.setLastNm("Windler");
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        personNameDto.setItNew(true);

        Collection<PersonNameDto> personNameDtos = new ArrayList<>();
        personNameDtos.add(personNameDto);

        personContainer.setThePersonNameDtoCollection(personNameDtos);

        PersonRaceDto personRaceDto = new PersonRaceDto();
        personRaceDto.setRaceCd("W");
        personRaceDto.setPersonUid(-2L);
        personRaceDto.setAddUserId(123L);
        personRaceDto.setRaceCategoryCd("W");
        personRaceDto.setRaceDescTxt("WHITE");
        personRaceDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        personRaceDto.setItNew(true);

        Collection<PersonRaceDto> personRaceDtos = new ArrayList<>();
        personRaceDtos.add(personRaceDto);

        personContainer.setThePersonRaceDtoCollection(personRaceDtos);

        Collection<EntityLocatorParticipationDto> entityLocatorParticipationDtos = new ArrayList<>();

        EntityLocatorParticipationDto entityLocatorParticipationDto = new EntityLocatorParticipationDto();
        entityLocatorParticipationDto.setAddUserId(123L);
        entityLocatorParticipationDto.setCd("H");
        entityLocatorParticipationDto.setClassCd("PST");
        entityLocatorParticipationDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        entityLocatorParticipationDto.setStatusCd("A");
        entityLocatorParticipationDto.setUseCd("H");
        entityLocatorParticipationDto.setEntityUid(-2L);
        entityLocatorParticipationDto.setItNew(true);

        entityLocatorParticipationDtos.add(entityLocatorParticipationDto);

        personContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorParticipationDtos);

        EntityIdDto entityIdDto = new EntityIdDto();
        entityIdDto.setEntityUid(-2L);
        entityIdDto.setEntityIdSeq(1);
        entityIdDto.setAssigningAuthorityCd("OID");
        entityIdDto.setAssigningAuthorityDescTxt("LABC");
        entityIdDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        entityIdDto.setRootExtensionTxt("05540205114");
        entityIdDto.setStatusCd("A");
        entityIdDto.setTypeCd("PN");
        entityIdDto.setAssigningAuthorityIdType("ISO");
        entityIdDto.setItNew(true);

        Collection<EntityIdDto> entityIdDtos = new ArrayList<>();
        entityIdDtos.add(entityIdDto);

        personContainer.setTheEntityIdDtoCollection(entityIdDtos);

        personContainer.setRole("PAT");

        return personContainer;
    }

    private Collection<PersonContainer> getPersonContainer() {
        Collection<PersonContainer> result = new ArrayList<>();

        result.add(getPerson());

        return result;
    }

    private Collection<EDXDocumentDto> getEdxDocumentCollection() {
        Collection<EDXDocumentDto> result = new ArrayList<>();

        EDXDocumentDto edxDocumentDto = new EDXDocumentDto();
        edxDocumentDto.setActUid(10002033L);
        edxDocumentDto.setPayload("<Container xmlns=\"http://www.cdc.gov/NEDSS\">\n" +
                "    <HL7LabReport>\n" +
                "        <HL7MSH>\n" +
                "            <FieldSeparator>|</FieldSeparator>\n" +
                "            <EncodingCharacters>^~\\&amp;</EncodingCharacters>\n" +
                "            <SendingApplication>\n" +
                "                <HL7NamespaceID>LABCORP-CORP</HL7NamespaceID>\n" +
                "                <HL7UniversalID>OID</HL7UniversalID>\n" +
                "                <HL7UniversalIDType>ISO</HL7UniversalIDType>\n" +
                "            </SendingApplication>\n" +
                "            <SendingFacility>\n" +
                "                <HL7NamespaceID>LABCORP</HL7NamespaceID>\n" +
                "                <HL7UniversalID>20D0649525</HL7UniversalID>\n" +
                "                <HL7UniversalIDType>CLIA</HL7UniversalIDType>\n" +
                "            </SendingFacility>\n" +
                "            <ReceivingApplication>\n" +
                "                <HL7NamespaceID>ALDOH</HL7NamespaceID>\n" +
                "                <HL7UniversalID>OID</HL7UniversalID>\n" +
                "                <HL7UniversalIDType>ISO</HL7UniversalIDType>\n" +
                "            </ReceivingApplication>\n" +
                "            <ReceivingFacility>\n" +
                "                <HL7NamespaceID>AL</HL7NamespaceID>\n" +
                "                <HL7UniversalID>OID</HL7UniversalID>\n" +
                "                <HL7UniversalIDType>ISO</HL7UniversalIDType>\n" +
                "            </ReceivingFacility>\n" +
                "            <DateTimeOfMessage>\n" +
                "                <year>2006</year>\n" +
                "                <month>4</month>\n" +
                "                <day>4</day>\n" +
                "                <hours>1</hours>\n" +
                "                <gmtOffset></gmtOffset>\n" +
                "            </DateTimeOfMessage>\n" +
                "            <Security></Security>\n" +
                "            <MessageType>\n" +
                "                <MessageCode>ORU</MessageCode>\n" +
                "                <TriggerEvent>R01</TriggerEvent>\n" +
                "                <MessageStructure>ORU_R01</MessageStructure>\n" +
                "            </MessageType>\n" +
                "            <MessageControlID>20120509010020114_251.2</MessageControlID>\n" +
                "            <ProcessingID>\n" +
                "                <HL7ProcessingID>D</HL7ProcessingID>\n" +
                "                <HL7ProcessingMode></HL7ProcessingMode>\n" +
                "            </ProcessingID>\n" +
                "            <VersionID>\n" +
                "                <HL7VersionID>2.5.1</HL7VersionID>\n" +
                "            </VersionID>\n" +
                "            <AcceptAcknowledgmentType>NE</AcceptAcknowledgmentType>\n" +
                "            <ApplicationAcknowledgmentType>NE</ApplicationAcknowledgmentType>\n" +
                "            <CountryCode>USA</CountryCode>\n" +
                "            <MessageProfileIdentifier>\n" +
                "                <HL7EntityIdentifier>V251_IG_LB_LABRPTPH_R1_INFORM_2010FEB</HL7EntityIdentifier>\n" +
                "                <HL7UniversalID>2.16.840.1.114222.4.3.2.5.2.5</HL7UniversalID>\n" +
                "                <HL7UniversalIDType>ISO</HL7UniversalIDType>\n" +
                "            </MessageProfileIdentifier>\n" +
                "        </HL7MSH>\n" +
                "        <HL7SoftwareSegment>\n" +
                "            <SoftwareVendorOrganization>\n" +
                "                <HL7OrganizationName>Mirth Corp.</HL7OrganizationName>\n" +
                "                <HL7IDNumber/>\n" +
                "                <HL7CheckDigit/>\n" +
                "            </SoftwareVendorOrganization>\n" +
                "            <SoftwareCertifiedVersionOrReleaseNumber>2.0</SoftwareCertifiedVersionOrReleaseNumber>\n" +
                "            <SoftwareProductName>Mirth Connect</SoftwareProductName>\n" +
                "            <SoftwareBinaryID>789654</SoftwareBinaryID>\n" +
                "            <SoftwareProductInformation/>\n" +
                "            <SoftwareInstallDate>\n" +
                "                <year>2011</year>\n" +
                "                <month>1</month>\n" +
                "                <day>1</day>\n" +
                "                <gmtOffset></gmtOffset>\n" +
                "            </SoftwareInstallDate>\n" +
                "        </HL7SoftwareSegment>\n" +
                "        <HL7PATIENT_RESULT>\n" +
                "            <PATIENT>\n" +
                "                <PatientIdentification>\n" +
                "                    <SetIDPID>\n" +
                "                        <HL7SequenceID>1</HL7SequenceID>\n" +
                "                    </SetIDPID>\n" +
                "                    <PatientIdentifierList>\n" +
                "                        <HL7IDNumber>05540205114</HL7IDNumber>\n" +
                "                        <HL7AssigningAuthority>\n" +
                "                            <HL7NamespaceID>LABC</HL7NamespaceID>\n" +
                "                            <HL7UniversalID>OID</HL7UniversalID>\n" +
                "                            <HL7UniversalIDType>ISO</HL7UniversalIDType>\n" +
                "                        </HL7AssigningAuthority>\n" +
                "                        <HL7IdentifierTypeCode>PN</HL7IdentifierTypeCode>\n" +
                "                    </PatientIdentifierList>\n" +
                "                    <PatientIdentifierList>\n" +
                "                        <HL7IDNumber>17485458372</HL7IDNumber>\n" +
                "                        <HL7AssigningAuthority>\n" +
                "                            <HL7NamespaceID>AssignAuth</HL7NamespaceID>\n" +
                "                            <HL7UniversalID>OID</HL7UniversalID>\n" +
                "                            <HL7UniversalIDType>ISO</HL7UniversalIDType>\n" +
                "                        </HL7AssigningAuthority>\n" +
                "                        <HL7IdentifierTypeCode>PI</HL7IdentifierTypeCode>\n" +
                "                        <HL7AssigningFacility>\n" +
                "                            <HL7NamespaceID>NE CLINIC</HL7NamespaceID>\n" +
                "                            <HL7UniversalID>24D1040593</HL7UniversalID>\n" +
                "                            <HL7UniversalIDType>CLIA</HL7UniversalIDType>\n" +
                "                        </HL7AssigningFacility>\n" +
                "                    </PatientIdentifierList>\n" +
                "                    <PatientName>\n" +
                "                        <HL7FamilyName>\n" +
                "                            <HL7Surname>Windler</HL7Surname>\n" +
                "                        </HL7FamilyName>\n" +
                "                        <HL7GivenName>FIRSTMAX251</HL7GivenName>\n" +
                "                    </PatientName>\n" +
                "                    <MothersMaidenName>\n" +
                "                        <HL7FamilyName>\n" +
                "                            <HL7Surname>MOTHER</HL7Surname>\n" +
                "                        </HL7FamilyName>\n" +
                "                        <HL7GivenName>PATERSON</HL7GivenName>\n" +
                "                    </MothersMaidenName>\n" +
                "                    <DateTimeOfBirth>\n" +
                "                        <year>1960</year>\n" +
                "                        <month>11</month>\n" +
                "                        <day>9</day>\n" +
                "                        <hours>20</hours>\n" +
                "                        <minutes>25</minutes>\n" +
                "                        <gmtOffset></gmtOffset>\n" +
                "                    </DateTimeOfBirth>\n" +
                "                    <AdministrativeSex>F</AdministrativeSex>\n" +
                "                    <Race>\n" +
                "                        <HL7Identifier>W</HL7Identifier>\n" +
                "                        <HL7Text>WHITE</HL7Text>\n" +
                "                        <HL7NameofCodingSystem>HL70005</HL7NameofCodingSystem>\n" +
                "                    </Race>\n" +
                "                    <PatientAddress>\n" +
                "                        <HL7StreetAddress>\n" +
                "                            <HL7StreetOrMailingAddress>5000 Staples Dr</HL7StreetOrMailingAddress>\n" +
                "                        </HL7StreetAddress>\n" +
                "                        <HL7OtherDesignation>APT100</HL7OtherDesignation>\n" +
                "                        <HL7City>SOMECITY</HL7City>\n" +
                "                        <HL7StateOrProvince>ME</HL7StateOrProvince>\n" +
                "                        <HL7ZipOrPostalCode>30342</HL7ZipOrPostalCode>\n" +
                "                        <HL7AddressType>H</HL7AddressType>\n" +
                "                    </PatientAddress>\n" +
                "                    <BirthOrder/>\n" +
                "                </PatientIdentification>\n" +
                "                <NextofKinAssociatedParties>\n" +
                "                    <SetIDNK1>1</SetIDNK1>\n" +
                "                    <Name>\n" +
                "                        <HL7FamilyName>\n" +
                "                            <HL7Surname>TESTNOK114B</HL7Surname>\n" +
                "                        </HL7FamilyName>\n" +
                "                        <HL7GivenName>FIRSTNOK1</HL7GivenName>\n" +
                "                        <HL7SecondAndFurtherGivenNamesOrInitialsThereof>X</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "                        <HL7Suffix>JR</HL7Suffix>\n" +
                "                        <HL7Prefix>DR</HL7Prefix>\n" +
                "                        <HL7Degree>MD</HL7Degree>\n" +
                "                    </Name>\n" +
                "                    <Relationship>\n" +
                "                        <HL7Identifier>FTH</HL7Identifier>\n" +
                "                    </Relationship>\n" +
                "                    <Address>\n" +
                "                        <HL7StreetAddress>\n" +
                "                            <HL7StreetOrMailingAddress>12 MAIN STREET</HL7StreetOrMailingAddress>\n" +
                "                        </HL7StreetAddress>\n" +
                "                        <HL7OtherDesignation>SUITE 16</HL7OtherDesignation>\n" +
                "                        <HL7City>COLUMBIA</HL7City>\n" +
                "                        <HL7StateOrProvince>SC</HL7StateOrProvince>\n" +
                "                        <HL7ZipOrPostalCode>30329</HL7ZipOrPostalCode>\n" +
                "                        <HL7Country>USA</HL7Country>\n" +
                "                        <HL7CountyParishCode>RICHLAND</HL7CountyParishCode>\n" +
                "                    </Address>\n" +
                "                    <PhoneNumber>\n" +
                "                        <HL7CountryCode/>\n" +
                "                        <HL7AreaCityCode>\n" +
                "                            <HL7Numeric>803</HL7Numeric>\n" +
                "                        </HL7AreaCityCode>\n" +
                "                        <HL7LocalNumber>\n" +
                "                            <HL7Numeric>5551212</HL7Numeric>\n" +
                "                        </HL7LocalNumber>\n" +
                "                        <HL7Extension>\n" +
                "                            <HL7Numeric>123</HL7Numeric>\n" +
                "                        </HL7Extension>\n" +
                "                    </PhoneNumber>\n" +
                "                </NextofKinAssociatedParties>\n" +
                "            </PATIENT>\n" +
                "            <ORDER_OBSERVATION>\n" +
                "                <CommonOrder>\n" +
                "                    <OrderControl>RE</OrderControl>\n" +
                "                    <FillerOrderNumber>\n" +
                "                        <HL7EntityIdentifier>20120601114</HL7EntityIdentifier>\n" +
                "                        <HL7NamespaceID>LABCORP</HL7NamespaceID>\n" +
                "                        <HL7UniversalID>20D0649525</HL7UniversalID>\n" +
                "                        <HL7UniversalIDType>CLIA</HL7UniversalIDType>\n" +
                "                    </FillerOrderNumber>\n" +
                "                    <OrderingFacilityName>\n" +
                "                        <HL7OrganizationName>COOSA VALLEY MEDICAL CENTER</HL7OrganizationName>\n" +
                "                        <HL7IDNumber/>\n" +
                "                        <HL7CheckDigit/>\n" +
                "                    </OrderingFacilityName>\n" +
                "                    <OrderingFacilityAddress>\n" +
                "                        <HL7StreetAddress>\n" +
                "                            <HL7StreetOrMailingAddress>315 WEST HICKORY ST.</HL7StreetOrMailingAddress>\n" +
                "                        </HL7StreetAddress>\n" +
                "                        <HL7OtherDesignation>SUITE 100</HL7OtherDesignation>\n" +
                "                        <HL7City>SYLACAUGA</HL7City>\n" +
                "                        <HL7StateOrProvince>AL</HL7StateOrProvince>\n" +
                "                        <HL7ZipOrPostalCode>35150</HL7ZipOrPostalCode>\n" +
                "                        <HL7Country>USA</HL7Country>\n" +
                "                        <HL7CountyParishCode>RICHLAND</HL7CountyParishCode>\n" +
                "                    </OrderingFacilityAddress>\n" +
                "                    <OrderingFacilityPhoneNumber>\n" +
                "                        <HL7CountryCode/>\n" +
                "                        <HL7AreaCityCode>\n" +
                "                            <HL7Numeric>256</HL7Numeric>\n" +
                "                        </HL7AreaCityCode>\n" +
                "                        <HL7LocalNumber>\n" +
                "                            <HL7Numeric>2495780</HL7Numeric>\n" +
                "                        </HL7LocalNumber>\n" +
                "                        <HL7Extension>\n" +
                "                            <HL7Numeric>123</HL7Numeric>\n" +
                "                        </HL7Extension>\n" +
                "                    </OrderingFacilityPhoneNumber>\n" +
                "                    <OrderingProviderAddress>\n" +
                "                        <HL7StreetAddress>\n" +
                "                            <HL7StreetOrMailingAddress>380 WEST HILL ST.</HL7StreetOrMailingAddress>\n" +
                "                        </HL7StreetAddress>\n" +
                "                        <HL7City>SYLACAUGA</HL7City>\n" +
                "                        <HL7StateOrProvince>AL</HL7StateOrProvince>\n" +
                "                        <HL7ZipOrPostalCode>35150</HL7ZipOrPostalCode>\n" +
                "                        <HL7Country>USA</HL7Country>\n" +
                "                        <HL7CountyParishCode>RICHLAND</HL7CountyParishCode>\n" +
                "                    </OrderingProviderAddress>\n" +
                "                </CommonOrder>\n" +
                "                <ObservationRequest>\n" +
                "                    <SetIDOBR>\n" +
                "                        <HL7SequenceID>1</HL7SequenceID>\n" +
                "                    </SetIDOBR>\n" +
                "                    <FillerOrderNumber>\n" +
                "                        <HL7EntityIdentifier>20120601114</HL7EntityIdentifier>\n" +
                "                        <HL7NamespaceID>LABCORP</HL7NamespaceID>\n" +
                "                        <HL7UniversalID>20D0649525</HL7UniversalID>\n" +
                "                        <HL7UniversalIDType>CLIA</HL7UniversalIDType>\n" +
                "                    </FillerOrderNumber>\n" +
                "                    <UniversalServiceIdentifier>\n" +
                "                        <HL7Identifier>525-9</HL7Identifier>\n" +
                "                        <HL7Text>ORGANISM COUNT</HL7Text>\n" +
                "                        <HL7NameofCodingSystem>LN</HL7NameofCodingSystem>\n" +
                "                        <HL7AlternateIdentifier>080186</HL7AlternateIdentifier>\n" +
                "                        <HL7AlternateText>CULTURE</HL7AlternateText>\n" +
                "                    </UniversalServiceIdentifier>\n" +
                "                    <ObservationDateTime>\n" +
                "                        <year>2006</year>\n" +
                "                        <month>3</month>\n" +
                "                        <day>24</day>\n" +
                "                        <hours>16</hours>\n" +
                "                        <minutes>55</minutes>\n" +
                "                        <gmtOffset></gmtOffset>\n" +
                "                    </ObservationDateTime>\n" +
                "                    <ObservationEndDateTime>\n" +
                "                        <year>2006</year>\n" +
                "                        <month>3</month>\n" +
                "                        <day>24</day>\n" +
                "                        <hours>16</hours>\n" +
                "                        <minutes>55</minutes>\n" +
                "                        <gmtOffset></gmtOffset>\n" +
                "                    </ObservationEndDateTime>\n" +
                "                    <CollectorIdentifier>\n" +
                "                        <HL7IDNumber>342384</HL7IDNumber>\n" +
                "                        <HL7FamilyName>\n" +
                "                            <HL7Surname>JONES</HL7Surname>\n" +
                "                        </HL7FamilyName>\n" +
                "                        <HL7GivenName>SUSAN</HL7GivenName>\n" +
                "                    </CollectorIdentifier>\n" +
                "                    <OrderingProvider>\n" +
                "                        <HL7IDNumber>46466</HL7IDNumber>\n" +
                "                        <HL7FamilyName>\n" +
                "                            <HL7Surname>BRENTNALL</HL7Surname>\n" +
                "                        </HL7FamilyName>\n" +
                "                        <HL7GivenName>GERRY</HL7GivenName>\n" +
                "                        <HL7SecondAndFurtherGivenNamesOrInitialsThereof>LEE</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "                        <HL7Suffix>SR</HL7Suffix>\n" +
                "                        <HL7Prefix>DR</HL7Prefix>\n" +
                "                        <HL7Degree>MD</HL7Degree>\n" +
                "                    </OrderingProvider>\n" +
                "                    <OrderCallbackPhoneNumber>\n" +
                "                        <HL7CountryCode/>\n" +
                "                        <HL7AreaCityCode>\n" +
                "                            <HL7Numeric>256</HL7Numeric>\n" +
                "                        </HL7AreaCityCode>\n" +
                "                        <HL7LocalNumber>\n" +
                "                            <HL7Numeric>2495780</HL7Numeric>\n" +
                "                        </HL7LocalNumber>\n" +
                "                        <HL7Extension/>\n" +
                "                    </OrderCallbackPhoneNumber>\n" +
                "                    <ResultsRptStatusChngDateTime>\n" +
                "                        <year>2006</year>\n" +
                "                        <month>4</month>\n" +
                "                        <day>4</day>\n" +
                "                        <hours>1</hours>\n" +
                "                        <minutes>39</minutes>\n" +
                "                        <seconds>0</seconds>\n" +
                "                        <gmtOffset></gmtOffset>\n" +
                "                    </ResultsRptStatusChngDateTime>\n" +
                "                    <ResultStatus>F</ResultStatus>\n" +
                "                    <ResultCopiesTo>\n" +
                "                        <HL7IDNumber>46214</HL7IDNumber>\n" +
                "                        <HL7FamilyName>\n" +
                "                            <HL7Surname>MATHIS</HL7Surname>\n" +
                "                        </HL7FamilyName>\n" +
                "                        <HL7GivenName>GERRY</HL7GivenName>\n" +
                "                        <HL7SecondAndFurtherGivenNamesOrInitialsThereof>LEE</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "                        <HL7Suffix>SR</HL7Suffix>\n" +
                "                        <HL7Prefix>DR</HL7Prefix>\n" +
                "                        <HL7Degree>MD</HL7Degree>\n" +
                "                    </ResultCopiesTo>\n" +
                "                    <ResultCopiesTo>\n" +
                "                        <HL7IDNumber>44582</HL7IDNumber>\n" +
                "                        <HL7FamilyName>\n" +
                "                            <HL7Surname>JONES</HL7Surname>\n" +
                "                        </HL7FamilyName>\n" +
                "                        <HL7GivenName>THOMAS</HL7GivenName>\n" +
                "                        <HL7SecondAndFurtherGivenNamesOrInitialsThereof>LEE</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "                        <HL7Suffix>III</HL7Suffix>\n" +
                "                        <HL7Prefix>DR</HL7Prefix>\n" +
                "                        <HL7Degree>MD</HL7Degree>\n" +
                "                    </ResultCopiesTo>\n" +
                "                    <ResultCopiesTo>\n" +
                "                        <HL7IDNumber>46111</HL7IDNumber>\n" +
                "                        <HL7FamilyName>\n" +
                "                            <HL7Surname>MARTIN</HL7Surname>\n" +
                "                        </HL7FamilyName>\n" +
                "                        <HL7GivenName>JERRY</HL7GivenName>\n" +
                "                        <HL7SecondAndFurtherGivenNamesOrInitialsThereof>L</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "                        <HL7Suffix>JR</HL7Suffix>\n" +
                "                        <HL7Prefix>DR</HL7Prefix>\n" +
                "                        <HL7Degree>MD</HL7Degree>\n" +
                "                    </ResultCopiesTo>\n" +
                "                    <ReasonforStudy>\n" +
                "                        <HL7Identifier>12365-4</HL7Identifier>\n" +
                "                        <HL7Text>TOTALLY CRAZY</HL7Text>\n" +
                "                        <HL7NameofCodingSystem>I9</HL7NameofCodingSystem>\n" +
                "                    </ReasonforStudy>\n" +
                "                    <PrincipalResultInterpreter>\n" +
                "                        <HL7Name>\n" +
                "                            <HL7IDNumber>22582</HL7IDNumber>\n" +
                "                            <HL7FamilyName>JONES</HL7FamilyName>\n" +
                "                            <HL7GivenName>TOM</HL7GivenName>\n" +
                "                            <HL7SecondAndFurtherGivenNamesOrInitialsThereof>L</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "                            <HL7Suffix>JR</HL7Suffix>\n" +
                "                            <HL7Prefix>DR</HL7Prefix>\n" +
                "                            <HL7Degree>MD</HL7Degree>\n" +
                "                        </HL7Name>\n" +
                "                    </PrincipalResultInterpreter>\n" +
                "                    <AssistantResultInterpreter>\n" +
                "                        <HL7Name>\n" +
                "                            <HL7IDNumber>22582</HL7IDNumber>\n" +
                "                            <HL7FamilyName>MOORE</HL7FamilyName>\n" +
                "                            <HL7GivenName>THOMAS</HL7GivenName>\n" +
                "                            <HL7SecondAndFurtherGivenNamesOrInitialsThereof>E</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "                            <HL7Suffix>III</HL7Suffix>\n" +
                "                            <HL7Prefix>DR</HL7Prefix>\n" +
                "                            <HL7Degree>MD</HL7Degree>\n" +
                "                        </HL7Name>\n" +
                "                    </AssistantResultInterpreter>\n" +
                "                    <Technician>\n" +
                "                        <HL7Name>\n" +
                "                            <HL7IDNumber>44</HL7IDNumber>\n" +
                "                            <HL7FamilyName>JONES</HL7FamilyName>\n" +
                "                            <HL7GivenName>SAM</HL7GivenName>\n" +
                "                            <HL7SecondAndFurtherGivenNamesOrInitialsThereof>A</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "                            <HL7Suffix>JR</HL7Suffix>\n" +
                "                            <HL7Prefix>MR</HL7Prefix>\n" +
                "                            <HL7Degree>MT</HL7Degree>\n" +
                "                        </HL7Name>\n" +
                "                    </Technician>\n" +
                "                    <Transcriptionist>\n" +
                "                        <HL7Name>\n" +
                "                            <HL7IDNumber>82</HL7IDNumber>\n" +
                "                            <HL7FamilyName>JONES</HL7FamilyName>\n" +
                "                            <HL7GivenName>THOMASINA</HL7GivenName>\n" +
                "                            <HL7SecondAndFurtherGivenNamesOrInitialsThereof>LEE ANN</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "                            <HL7Suffix>II</HL7Suffix>\n" +
                "                            <HL7Prefix>MS</HL7Prefix>\n" +
                "                            <HL7Degree>RA</HL7Degree>\n" +
                "                        </HL7Name>\n" +
                "                    </Transcriptionist>\n" +
                "                    <NumberofSampleContainers/>\n" +
                "                </ObservationRequest>\n" +
                "                <PatientResultOrderObservation>\n" +
                "                    <OBSERVATION>\n" +
                "                        <ObservationResult>\n" +
                "                            <SetIDOBX>\n" +
                "<HL7SequenceID>1</HL7SequenceID>\n" +
                "                            </SetIDOBX>\n" +
                "                            <ValueType>SN</ValueType>\n" +
                "                            <ObservationIdentifier>\n" +
                "<HL7Identifier>77190-7</HL7Identifier>\n" +
                "<HL7Text>TITER</HL7Text>\n" +
                "<HL7NameofCodingSystem>LN</HL7NameofCodingSystem>\n" +
                "<HL7AlternateIdentifier>080133</HL7AlternateIdentifier>\n" +
                "                            </ObservationIdentifier>\n" +
                "                            <ObservationSubID>1</ObservationSubID>\n" +
                "                            <ObservationValue>100^1^:^1</ObservationValue>\n" +
                "                            <Units>\n" +
                "<HL7Identifier>mL</HL7Identifier>\n" +
                "                            </Units>\n" +
                "                            <ReferencesRange>10-100</ReferencesRange>\n" +
                "                            <Probability/>\n" +
                "                            <ObservationResultStatus>F</ObservationResultStatus>\n" +
                "                            <ProducersReference>\n" +
                "<HL7Identifier>20D0649525</HL7Identifier>\n" +
                "<HL7Text>LABCORP BIRMINGHAM</HL7Text>\n" +
                "<HL7NameofCodingSystem>CLIA</HL7NameofCodingSystem>\n" +
                "                            </ProducersReference>\n" +
                "                            <DateTimeOftheAnalysis>\n" +
                "<year>2006</year>\n" +
                "<month>4</month>\n" +
                "<day>1</day>\n" +
                "<gmtOffset></gmtOffset>\n" +
                "                            </DateTimeOftheAnalysis>\n" +
                "                            <PerformingOrganizationName>\n" +
                "<HL7OrganizationName>Lab1</HL7OrganizationName>\n" +
                "<HL7OrganizationNameTypeCode>L</HL7OrganizationNameTypeCode>\n" +
                "<HL7IDNumber/>\n" +
                "<HL7CheckDigit/>\n" +
                "<HL7AssigningAuthority>\n" +
                "    <HL7NamespaceID>CLIA</HL7NamespaceID>\n" +
                "    <HL7UniversalID>2.16.840.1.114222.4.3.2.5.2.100</HL7UniversalID>\n" +
                "    <HL7UniversalIDType>ISO</HL7UniversalIDType>\n" +
                "</HL7AssigningAuthority>\n" +
                "<HL7OrganizationIdentifier>1234</HL7OrganizationIdentifier>\n" +
                "                            </PerformingOrganizationName>\n" +
                "                            <PerformingOrganizationAddress>\n" +
                "<HL7StreetAddress>\n" +
                "    <HL7StreetOrMailingAddress>1234 Cornell Park Dr</HL7StreetOrMailingAddress>\n" +
                "</HL7StreetAddress>\n" +
                "<HL7City>Blue Ash</HL7City>\n" +
                "<HL7StateOrProvince>OH</HL7StateOrProvince>\n" +
                "<HL7ZipOrPostalCode>45241</HL7ZipOrPostalCode>\n" +
                "                            </PerformingOrganizationAddress>\n" +
                "                            <PerformingOrganizationMedicalDirector>\n" +
                "<HL7IDNumber>9876543</HL7IDNumber>\n" +
                "<HL7FamilyName>\n" +
                "    <HL7Surname>JONES</HL7Surname>\n" +
                "</HL7FamilyName>\n" +
                "<HL7GivenName>BOB</HL7GivenName>\n" +
                "<HL7SecondAndFurtherGivenNamesOrInitialsThereof>F</HL7SecondAndFurtherGivenNamesOrInitialsThereof>\n" +
                "<HL7Degree>MD</HL7Degree>\n" +
                "                            </PerformingOrganizationMedicalDirector>\n" +
                "                        </ObservationResult>\n" +
                "                    </OBSERVATION>\n" +
                "                </PatientResultOrderObservation>\n" +
                "                <PatientResultOrderSPMObservation>\n" +
                "                    <SPECIMEN>\n" +
                "                        <SPECIMEN>\n" +
                "                            <SetIDSPM>\n" +
                "<HL7SequenceID>1</HL7SequenceID>\n" +
                "                            </SetIDSPM>\n" +
                "                            <SpecimenID>\n" +
                "<HL7FillerAssignedIdentifier>\n" +
                "    <HL7EntityIdentifier>56789</HL7EntityIdentifier>\n" +
                "    <HL7NamespaceID>LABCORP</HL7NamespaceID>\n" +
                "    <HL7UniversalID>20D0649525</HL7UniversalID>\n" +
                "    <HL7UniversalIDType>CLIA</HL7UniversalIDType>\n" +
                "</HL7FillerAssignedIdentifier>\n" +
                "                            </SpecimenID>\n" +
                "                            <SpecimenParentIDs>\n" +
                "<HL7FillerAssignedIdentifier>\n" +
                "    <HL7EntityIdentifier>20120601114</HL7EntityIdentifier>\n" +
                "    <HL7NamespaceID>LABCORP</HL7NamespaceID>\n" +
                "    <HL7UniversalID>20D0649525</HL7UniversalID>\n" +
                "    <HL7UniversalIDType>CLIA</HL7UniversalIDType>\n" +
                "</HL7FillerAssignedIdentifier>\n" +
                "                            </SpecimenParentIDs>\n" +
                "                            <SpecimenType>\n" +
                "<HL7AlternateIdentifier>BLD</HL7AlternateIdentifier>\n" +
                "<HL7AlternateText>BLOOD</HL7AlternateText>\n" +
                "<HL7NameofAlternateCodingSystem>L</HL7NameofAlternateCodingSystem>\n" +
                "                            </SpecimenType>\n" +
                "                            <SpecimenSourceSite>\n" +
                "<HL7Identifier>LA</HL7Identifier>\n" +
                "<HL7NameofCodingSystem>HL70070</HL7NameofCodingSystem>\n" +
                "                            </SpecimenSourceSite>\n" +
                "                            <GroupedSpecimenCount/>\n" +
                "                            <SpecimenDescription>SOURCE NOT SPECIFIED</SpecimenDescription>\n" +
                "                            <SpecimenCollectionDateTime>\n" +
                "<HL7RangeStartDateTime>\n" +
                "    <year>2006</year>\n" +
                "    <month>3</month>\n" +
                "    <day>24</day>\n" +
                "    <hours>16</hours>\n" +
                "    <minutes>55</minutes>\n" +
                "    <gmtOffset></gmtOffset>\n" +
                "</HL7RangeStartDateTime>\n" +
                "<HL7RangeEndDateTime>\n" +
                "    <year>2006</year>\n" +
                "    <month>3</month>\n" +
                "    <day>24</day>\n" +
                "    <hours>16</hours>\n" +
                "    <minutes>55</minutes>\n" +
                "    <gmtOffset></gmtOffset>\n" +
                "</HL7RangeEndDateTime>\n" +
                "                            </SpecimenCollectionDateTime>\n" +
                "                            <SpecimenReceivedDateTime>\n" +
                "<year>2006</year>\n" +
                "<month>3</month>\n" +
                "<day>25</day>\n" +
                "<hours>1</hours>\n" +
                "<minutes>37</minutes>\n" +
                "<gmtOffset></gmtOffset>\n" +
                "                            </SpecimenReceivedDateTime>\n" +
                "                            <NumberOfSpecimenContainers/>\n" +
                "                        </SPECIMEN>\n" +
                "                    </SPECIMEN>\n" +
                "                </PatientResultOrderSPMObservation>\n" +
                "            </ORDER_OBSERVATION>\n" +
                "        </HL7PATIENT_RESULT>\n" +
                "    </HL7LabReport>\n" +
                "</Container>\n" +
                "\n" +
                "<!-- raw_message_id = DF88C754-65FB-4D28-877B-4D79BA4583CA -->");
        edxDocumentDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        edxDocumentDto.setDocTypeCd("11648804");
        edxDocumentDto.setNbsDocumentMetadataUid(1005L);
        edxDocumentDto.setItNew(true);

        result.add(edxDocumentDto);

        return result;
    }

    private Collection<RoleDto> getRoles() {
        Collection<RoleDto> result = new ArrayList<>();

        RoleDto roleDto = new RoleDto();
        roleDto.setRoleSeq(1L);
        roleDto.setCd("NI");
        roleDto.setCdDescTxt("No Information Given");
        roleDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        roleDto.setStatusCd("A");
        roleDto.setScopingEntityUid(10092397L);
        roleDto.setScopingRoleSeq(1);
        roleDto.setSubjectEntityUid(10001017L);
        roleDto.setScopingClassCd("PATIENT");
        roleDto.setSubjectClassCd("MAT");
        roleDto.setItNew(true);

        result.add(roleDto);

        RoleDto roleDto1 = new RoleDto();
        roleDto1.setRoleSeq(1L);
        roleDto1.setAddReasonCd("because");
        roleDto1.setCd("PAT");
        roleDto1.setCdDescTxt("PATIENT");
        roleDto1.setRecordStatusCd(NEDSSConstant.ACTIVE);
        roleDto1.setStatusCd("A");
        roleDto1.setSubjectClassCd("PATIENT");
        roleDto1.setItNew(true);

        result.add(roleDto1);

        return result;
    }
    private Collection<MaterialContainer> getMaterialContainers() {
        Collection<MaterialContainer> result = new ArrayList<>();

        MaterialContainer materialContainer = new MaterialContainer();
        MaterialDto materialDto = new MaterialDto();
        materialDto.setAddUserId(123L);
        materialDto.setDescription("SOURCE NOT SPECIFIED");
        materialDto.setLastChgUserId(123L);
        materialDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        materialDto.setStatusCd("A");
        materialDto.setItNew(true);
        materialDto.setSuperClassType("Entity");

        materialContainer.setTheMaterialDto(materialDto);
        materialContainer.setItNew(true);

        EntityIdDto entityIdDto = new EntityIdDto();
        entityIdDto.setAssigningAuthorityCd("20D0649525");
        entityIdDto.setAssigningAuthorityDescTxt("LABCORP");
        entityIdDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        entityIdDto.setRootExtensionTxt("56789");
        entityIdDto.setTypeCd("SPC");
        entityIdDto.setTypeDescTxt("Specimen");
        entityIdDto.setAssigningAuthorityIdType("CLIA");
        entityIdDto.setItNew(true);

        Collection<EntityIdDto> entityIdDtos = new ArrayList<>();
        entityIdDtos.add(entityIdDto);

        materialContainer.setTheEntityIdDtoCollection(entityIdDtos);

        result.add(materialContainer);
        return result;
    }

    private EdxLabInformationDto getEdxLabInfo() {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setRole("CT");
        edxLabInformationDto.setRootObserbationUid(999999);
        edxLabInformationDto.setSendingFacilityClia("20D0649525");
        edxLabInformationDto.setSendingFacilityName("LABCORP");
        edxLabInformationDto.setPatientUid(-2);
        edxLabInformationDto.setUserId(123);
        edxLabInformationDto.setNextUid(-28);
        edxLabInformationDto.setFillerNumber("20120601114");
        edxLabInformationDto.setMessageControlID("20120509010020114_251.2");
        edxLabInformationDto.setOrderingProvider(true);
        edxLabInformationDto.setLocalId("OBS10002031GA01");

        EdxLabIdentiferDto id = new EdxLabIdentiferDto();
        id.setIdentifer("77190-7");
        id.setObservationUid(-27L);
        id.setSubMapID("1");
        ArrayList<String> obsValues = new ArrayList<>();
        obsValues.add("100^1^:^1");
        id.setObservationValues(obsValues);

        ArrayList<EdxLabIdentiferDto> labIdDtoList = new ArrayList<>();
        labIdDtoList.add(id);

        edxLabInformationDto.setEdxLabIdentiferDTColl(labIdDtoList);
        edxLabInformationDto.setEntityName("FIRSTMAX251 MaxGyver");
        edxLabInformationDto.setUserName("data-processing");
        edxLabInformationDto.setUniversalIdType("CLIA");
        edxLabInformationDto.setNbsInterfaceUid(10000023);
        edxLabInformationDto.setJurisdictionName("Fulton County");
        edxLabInformationDto.setProgramAreaName("HEP");
        edxLabInformationDto.setLabIsCreate(true);
        edxLabInformationDto.setLabIsCreateSuccess(true);
        edxLabInformationDto.setAddReasonCd("because");
        edxLabInformationDto.setEthnicityCodeTranslated(true);
        edxLabInformationDto.setObsMethodTranslated(true);
        edxLabInformationDto.setSexTranslated(true);
        edxLabInformationDto.setFillerNumberPresent(true);
        edxLabInformationDto.setObsStatusTranslated(true);
        edxLabInformationDto.setRelationship("FTH");
        edxLabInformationDto.setPersonParentUid(10092385);
        edxLabInformationDto.setCreateLabPermission(true);
        edxLabInformationDto.setUpdateLabPermission(true);
        edxLabInformationDto.setMarkAsReviewPermission(true);
        edxLabInformationDto.setCreateInvestigationPermission(true);
        edxLabInformationDto.setCreateNotificationPermission(true);
        edxLabInformationDto.setMatchingAlgorithm(true);
        edxLabInformationDto.setStatus(NbsInterfaceStatus.Success);
        edxLabInformationDto.setErrorText("2");

        return edxLabInformationDto;
    }

    private Collection<DsmAlgorithm> getAlgorithmsAnd() {
        Collection<DsmAlgorithm> result = new ArrayList<>();
        DsmAlgorithm alg = new DsmAlgorithm();
        alg.setAlgorithmNm("test name");
        alg.setAlgorithmPayload("<Algorithm xmlns=\"http://www.cdc.gov/NEDSS\">\n" +
                "  <AlgorithmName>testing algorithm name</AlgorithmName>\n" +
                "  <Event>\n" +
                "    <Code>11648804</Code>\n" +
                "    <CodeDescTxt>Laboratory Report</CodeDescTxt>\n" +
                "    <CodeSystemCode>2.16.840.1.113883.6.96</CodeSystemCode>\n" +
                "  </Event>\n" +
                "  <Frequency>\n" +
                "    <Code>1</Code>\n" +
                "    <CodeDescTxt>Real-Time</CodeDescTxt>\n" +
                "    <CodeSystemCode>L</CodeSystemCode>\n" +
                "  </Frequency>\n" +
                "  <AppliesToEntryMethods>\n" +
                "    <EntryMethod>\n" +
                "      <Code>1</Code>\n" +
                "      <CodeDescTxt>Electronic Document</CodeDescTxt>\n" +
                "      <CodeSystemCode>L</CodeSystemCode>\n" +
                "    </EntryMethod>\n" +
                "  </AppliesToEntryMethods>\n" +
                "  <InvestigationType/>\n" +
                "  <Comment/>\n" +
                "  <ElrAdvancedCriteria>\n" +
                "    <EventDateLogic>\n" +
                "      <ElrTimeLogic>\n" +
                "        <ElrTimeLogicInd>\n" +
                "          <Code>N</Code>\n" +
                "        </ElrTimeLogicInd>\n" +
                "      </ElrTimeLogic>\n" +
                "    </EventDateLogic>\n" +
                "    <AndOrLogic>AND</AndOrLogic>\n" +
                "    <ElrCriteria>\n" +
                "      <ResultedTest>\n" +
                "        <Code>77190-7</Code>\n" +
                "        <CodeDescTxt>Hepatitis B virus core and surface Ab and surface Ag panel - Serum (77190-7)</CodeDescTxt>\n" +
                "      </ResultedTest>\n" +
                "      <ElrNumericResultValue>\n" +
                "        <ComparatorCode>\n" +
                "          <Code>=</Code>\n" +
                "          <CodeDescTxt>=</CodeDescTxt>\n" +
                "        </ComparatorCode>\n" +
                "        <Value1>1</Value1>\n" +
                "        <Unit>\n" +
                "          <Code>mL</Code>\n" +
                "          <CodeDescTxt>mL</CodeDescTxt>\n" +
                "        </Unit>\n" +
                "      </ElrNumericResultValue>\n" +
                "    </ElrCriteria>\n" +
                "    <InvLogic>\n" +
                "      <InvLogicInd>\n" +
                "        <Code>N</Code>\n" +
                "      </InvLogicInd>\n" +
                "    </InvLogic>\n" +
                "  </ElrAdvancedCriteria>\n" +
                "  <Action>\n" +
                "    <MarkAsReviewed>\n" +
                "      <OnFailureToMarkAsReviewed>\n" +
                "        <Code>2</Code>\n" +
                "        <CodeDescTxt>Retain Event Record</CodeDescTxt>\n" +
                "        <CodeSystemCode>L</CodeSystemCode>\n" +
                "      </OnFailureToMarkAsReviewed>\n" +
                "      <AdditionalComment/>\n" +
                "    </MarkAsReviewed>\n" +
                "  </Action>\n" +
                "</Algorithm>");
        alg.setApplyTo("test apply");
        alg.setConditionList("hep, mumps");
        alg.setAdminComment("test admin comments");
        alg.setFrequency("test freq");
        alg.setEventAction("event action");
        alg.setEventType("event type");
        alg.setLastChgUserId(1L);
        alg.setReportingSystemList("reporting system list");
        alg.setResultedTestList("resulted test list");
        alg.setApplyTo("test apply to");
        alg.setStatusCd(NEDSSConstant.ACTIVE);
        alg.setSendingSystemList("sending system list");

        result.add(alg);
        return result;
    }

    private Collection<DsmAlgorithm> getAlgorithms() {
        Collection<DsmAlgorithm> result = new ArrayList<>();
        DsmAlgorithm alg = new DsmAlgorithm();
        alg.setAlgorithmNm("testing algorithm name");
        alg.setAlgorithmPayload("<Algorithm xmlns=\"http://www.cdc.gov/NEDSS\">\n" +
                "  <AlgorithmName>testing algorithm name</AlgorithmName>\n" +
                "  <Event>\n" +
                "    <Code>11648804</Code>\n" +
                "    <CodeDescTxt>Laboratory Report</CodeDescTxt>\n" +
                "    <CodeSystemCode>2.16.840.1.113883.6.96</CodeSystemCode>\n" +
                "  </Event>\n" +
                "  <Frequency>\n" +
                "    <Code>1</Code>\n" +
                "    <CodeDescTxt>Real-Time</CodeDescTxt>\n" +
                "    <CodeSystemCode>L</CodeSystemCode>\n" +
                "  </Frequency>\n" +
                "  <AppliesToEntryMethods>\n" +
                "    <EntryMethod>\n" +
                "      <Code>1</Code>\n" +
                "      <CodeDescTxt>Electronic Document</CodeDescTxt>\n" +
                "      <CodeSystemCode>L</CodeSystemCode>\n" +
                "    </EntryMethod>\n" +
                "  </AppliesToEntryMethods>\n" +
                "  <InvestigationType/>\n" +
                "  <Comment/>\n" +
                "  <ElrAdvancedCriteria>\n" +
                "    <EventDateLogic>\n" +
                "      <ElrTimeLogic>\n" +
                "        <ElrTimeLogicInd>\n" +
                "          <Code>N</Code>\n" +
                "        </ElrTimeLogicInd>\n" +
                "      </ElrTimeLogic>\n" +
                "    </EventDateLogic>\n" +
                "    <AndOrLogic>OR</AndOrLogic>\n" +
                "    <ElrCriteria>\n" +
                "      <ResultedTest>\n" +
                "        <Code>77190-7</Code>\n" +
                "        <CodeDescTxt>Hepatitis B virus core and surface Ab and surface Ag panel - Serum (77190-7)</CodeDescTxt>\n" +
                "      </ResultedTest>\n" +
                "      <ElrNumericResultValue>\n" +
                "        <ComparatorCode>\n" +
                "          <Code>=</Code>\n" +
                "          <CodeDescTxt>=</CodeDescTxt>\n" +
                "        </ComparatorCode>\n" +
                "        <Value1>1</Value1>\n" +
                "        <Unit>\n" +
                "          <Code>mL</Code>\n" +
                "          <CodeDescTxt>mL</CodeDescTxt>\n" +
                "        </Unit>\n" +
                "      </ElrNumericResultValue>\n" +
                "    </ElrCriteria>\n" +
                "    <InvLogic>\n" +
                "      <InvLogicInd>\n" +
                "        <Code>N</Code>\n" +
                "      </InvLogicInd>\n" +
                "    </InvLogic>\n" +
                "  </ElrAdvancedCriteria>\n" +
                "  <Action>\n" +
                "    <MarkAsReviewed>\n" +
                "      <OnFailureToMarkAsReviewed>\n" +
                "        <Code>2</Code>\n" +
                "        <CodeDescTxt>Retain Event Record</CodeDescTxt>\n" +
                "        <CodeSystemCode>L</CodeSystemCode>\n" +
                "      </OnFailureToMarkAsReviewed>\n" +
                "      <AdditionalComment/>\n" +
                "    </MarkAsReviewed>\n" +
                "  </Action>\n" +
                "</Algorithm>");
        alg.setApplyTo("1");
        alg.setConditionList("hep, mumps");
        alg.setAdminComment("test admin comments");
        alg.setFrequency("1");
        alg.setEventAction("3");
        alg.setEventType("event type");
        alg.setLastChgUserId(1L);
        alg.setReportingSystemList("reporting system list");
        alg.setApplyTo("test apply to");
        alg.setStatusCd("A");
        alg.setSendingSystemList("");
        alg.setResultedTestList("7790-7");

        result.add(alg);
        return result;
    }
}