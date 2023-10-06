package gov.cdc.dataingestion.nbs.ecr.service;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.*;
import gov.cdc.dataingestion.nbs.ecr.model.Patient.CdaPatientField;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedCase;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedInterview;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedTreatment;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.PhdcAnswerDao;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.QuestionIdentifierMapDao;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.GetStringsBeforeCaret;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.GetStringsBeforePipe;

@Service
public class CdaMapper implements ICdaMapper {
    
    private ICdaLookUpService ecrLookUpService;
    private final String rootId = "2.16.840.1.113883.19";
    private final String codeSystem = "2.16.840.1.113883.6.1";
    private final String codeSystemName = "LOINC";
    private final String xmlNameSpaceHolder = "urn:hl7-org:v3";
    private final String nameSpaceUrl = "http://www.w3.org/2001/XMLSchema-instance";
    private final String state = "STATE";
    private final String county = "COUNTY";
    private final String country = "COUNTRY";
    private final String useablePeriod = "useablePeriod";
    private final String code = "123-4567";
    private final String clinicalCodeSystem = "Local-codesystem-oid";
    private final String clinicalCodeSystemName = "LocalSystem";
    private final String codeDisplayName = "Interested Parties Section";
    private final String clinicalTitle = "INTERESTED PARTIES SECTION";
    private final String actCodeDisplayName = "Interested Party";
    private final String notFoundValue = "NOT_FOUND";
    private final String idRoot = "2.16.840.999999";
    private final String valueTest = "value";
    private final String change = "CHANGED";
    private final String idArrRoot = "2.16.840.1.113883.4.6";
    private final String extnStr = ";extn=";
    private final String notMappedValue = "NOT_MAPPED";
    private final String valueTag = "<value></value>";
    private final String codeNotMappedValue = "CODE NOT MAPPED";

    /** COL NAME */
    private final String patLocalId = "patLocalId";
    private final String colQuestionGroupSeqNbr = "questionGroupSeqNbr";
    private final String colAnswerGroupSeqNbr = "answerGroupSeqNbr";
    private final String colDataType = "dataType";
    private final String colSeqNbr = "seqNbr";
    private final String colAnswerTxt = "answerTxt";
    private final String colAnsCodeSystemCd = "ansCodeSystemCd";
    private final String colAnsCodeSystemDescTxt = "ansCodeSystemDescTxt";
    private final String colAnsDisplayTxt = "ansDisplayTxt";
    private final String colAnsToCode = "ansToCode";
    private final String colAnsToCodeSystemCd = "ansToCodeSystemCd";
    private final String colAnsToCodeSystemDescTxt = "ansToCodeSystemDescTxt";
    private final String colAnsToDisplayNm = "ansToDisplayNm";
    private final String colQuestionIdentifier = "questionIdentifier";
    private final String colQuesCodeSystemCd = "quesCodeSystemCd";
    private final String colQuesCodeSystemDescTxt = "quesCodeSystemDescTxt";
    private final String colQuesDisplayTxt = "quesDisplayTxt";
    
    
    /**DATA TYPE*/
    private final String dataTypeCode = "CODED";
    private final String dataTypeNumeric = "NUMERIC";


    @Autowired
    public CdaMapper(ICdaLookUpService ecrLookUpService) {
        this.ecrLookUpService = ecrLookUpService;
    }

    public String tranformSelectedEcrToCDAXml(EcrSelectedRecord input) throws EcrCdaXmlException {

        //region DOCUMENT INITIATION
        ClinicalDocumentDocument1 rootDocument = ClinicalDocumentDocument1.Factory.newInstance();
        POCDMT000040ClinicalDocument1 clinicalDocument = POCDMT000040ClinicalDocument1.Factory.newInstance();

        CS[] realmCodeArray = { CS.Factory.newInstance()};
        clinicalDocument.setRealmCodeArray(realmCodeArray);
        clinicalDocument.getRealmCodeArray(0).setCode("US");

        clinicalDocument.setTypeId(POCDMT000040InfrastructureRootTypeId.Factory.newInstance());
        clinicalDocument.getTypeId().setRoot("2.16.840.1.113883.1.3");
        clinicalDocument.getTypeId().setExtension("POCD_HD000040");
        //endregion

        String inv168 = "";
        Integer nbsInterfaceUid = null;
        String systemName = "";
        Integer versionCtrNbr = null;
        Integer dataMigrationStatus = null;

        //region CONTAINER COMPONENT CREATION
        if (input.getMsgContainer().getInvLocalId() != null && !input.getMsgContainer().getInvLocalId().isEmpty()) {
            clinicalDocument.setId(II.Factory.newInstance());
            clinicalDocument.getId().setRoot(rootId);
            clinicalDocument.getId().setExtension(input.getMsgContainer().getInvLocalId());
            clinicalDocument.getId().setAssigningAuthorityName("LR");
            inv168 = input.getMsgContainer().getInvLocalId();
        }

        if (input.getMsgContainer().getOngoingCase() != null &&
            !input.getMsgContainer().getOngoingCase().isEmpty()) {
            clinicalDocument.setSetId(II.Factory.newInstance());
            clinicalDocument.getSetId().setExtension("ONGOING_CASE");
            if (input.getMsgContainer().getOngoingCase().equalsIgnoreCase("yes")) {
                clinicalDocument.getSetId().setDisplayable(true);
            } else {
                clinicalDocument.getSetId().setDisplayable(false);
            }
        }

        if(input.getMsgContainer().getNbsInterfaceUid() != null) {
            nbsInterfaceUid = input.getMsgContainer().getNbsInterfaceUid();
        }

        if(input.getMsgContainer().getReceivingSystem() != null &&
            !input.getMsgContainer().getReceivingSystem().isEmpty()) {
            if(input.getMsgContainer().getReceivingSystem().length() > 0) {
                systemName = input.getMsgContainer().getReceivingSystem();
            } else {
                systemName = "NBS";
            }
        }

        if (input.getMsgContainer().getVersionCtrNbr() != null) {
            versionCtrNbr = input.getMsgContainer().getVersionCtrNbr();
        }

        if(input.getMsgContainer().getDataMigrationStatus() != null) {
            dataMigrationStatus = input.getMsgContainer().getDataMigrationStatus();
        }

        clinicalDocument.setCode(CE.Factory.newInstance());
        clinicalDocument.getCode().setCode("55751-2");
        clinicalDocument.getCode().setCodeSystem(codeSystem);
        clinicalDocument.getCode().setCodeSystemName(codeSystemName);
        clinicalDocument.getCode().setDisplayName("Public Health Case Report - PHRI");
        clinicalDocument.setTitle(ST.Factory.newInstance());

        // This need to be checked
        clinicalDocument.getTitle().set(mapToStringData("Public Health Case Report - Data from Legacy System to CDA"));

        clinicalDocument.setEffectiveTime(TS.Factory.newInstance());
        clinicalDocument.getEffectiveTime().setValue(getCurrentUtcDateTimeInCdaFormat());

        if(versionCtrNbr != null && versionCtrNbr > 0) {
            clinicalDocument.setVersionNumber(INT.Factory.newInstance());
            clinicalDocument.getVersionNumber().setValue(BigInteger.valueOf(versionCtrNbr));
        }

        clinicalDocument.setConfidentialityCode(CE.Factory.newInstance());
        clinicalDocument.getConfidentialityCode().setCode("N");
        clinicalDocument.getConfidentialityCode().setCodeSystem("2.16.840.1.113883.5.25");
        //endregion

        int componentCounter=-1;
        int componentCaseCounter=-1;
        int interviewCounter= 0;
        int treatmentCounter=0;
        int treatmentSectionCounter=0;
        int caseEntryCounter=0;
        int performerCounter=0;
        int patientComponentCounter=-1;
        int performerComponentCounter=0;
        int performerSectionCounter=0;
        int clinicalCounter= 0;
        int performerEntityCounter=0;
        int signsAndSymptomCounter=0;
        int medicalHistoryCounter=0;
        int GenericBatchEntryCounter=0;

        //region SUB COMPONENT CREATION

        // Set RecordTarget && patient Role
        clinicalDocument.addNewRecordTarget();
        clinicalDocument.getRecordTargetArray(0).addNewPatientRole();

        /**MAP TO PATIENT**/
        var pat =  mapToPatient(input, clinicalDocument, patientComponentCounter, inv168);
        clinicalDocument = pat.getClinicalDocument();
        patientComponentCounter = pat.getPatientComponentCounter();
        inv168 = pat.getInv168();

        /**MAP TO CASE**/
        var ecrCase = mapToCaseTop(input, clinicalDocument, componentCounter, clinicalCounter,
        componentCaseCounter, inv168);
        clinicalDocument = ecrCase.getClinicalDocument();
        componentCounter = ecrCase.getComponentCounter();
        clinicalCounter = ecrCase.getClinicalCounter();
        componentCaseCounter = ecrCase.getComponentCaseCounter();
        inv168 = ecrCase.getInv168();

        /**XML ANSWER**/
        var ecrXmlAnswer = mapToXmlAnswerTop(input,
                clinicalDocument, componentCounter);
        clinicalDocument = ecrXmlAnswer.getClinicalDocument();
        componentCounter = ecrXmlAnswer.getComponentCounter();


        /**INITIATE SECTION**/
        int c = 0;
        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
        }
        else {
            c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
        }

        var comp = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c);
        if (comp.getSection() == null) {
            comp.addNewSection();
        }

        /** set this section -- clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection(); **/
        /**
         * PROVIDER
         * **/
        var ecrProvider = mapToProviderTop(input, clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection(),
                inv168, performerComponentCounter, componentCounter,
                 performerSectionCounter);


        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).setSection(ecrProvider.getClinicalSection());
        inv168 = ecrProvider.getInv168();
        performerComponentCounter = ecrProvider.getPerformerComponentCounter();
        componentCounter = ecrProvider.getComponentCounter();
        performerSectionCounter = ecrProvider.getPerformerSectionCounter();


        /**
         * ORGANIZATION
         * **/
        var ecrOrganization = mapToOrganizationTop(input, clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection(),
                performerComponentCounter, componentCounter, performerSectionCounter);
        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).setSection(ecrProvider.getClinicalSection());
        performerComponentCounter = ecrOrganization.getPerformerComponentCounter();
        componentCounter = ecrOrganization.getComponentCounter();
        performerSectionCounter = ecrOrganization.getPerformerSectionCounter();

        /**
         * PLACE
         * */
        POCDMT000040Section interestedPartyComp = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection();
        var ecrPlace = mapToPlaceTop(input, performerComponentCounter,
                componentCounter, performerSectionCounter, interestedPartyComp);
        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).setSection(ecrPlace.getSection());
        performerComponentCounter = ecrPlace.getPerformerComponentCounter();
        componentCounter = ecrPlace.getComponentCounter();
        performerSectionCounter = ecrPlace.getPerformerSectionCounter();

        /**
         * INTERVIEW
         * */
        var ecrInterview = mapToInterviewTop(input, clinicalDocument, interviewCounter, componentCounter);
        clinicalDocument = ecrInterview.getClinicalDocument();
        interviewCounter = ecrInterview.getInterviewCounter();
        componentCounter = ecrInterview.getComponentCounter();

        /**
         * TREATMENT
         * */
        var ecrTreatment = mapToTreatmentTop(input, clinicalDocument,
                treatmentCounter, componentCounter, treatmentSectionCounter);
        clinicalDocument = ecrTreatment.getClinicalDocument();
        treatmentCounter = ecrTreatment.getTreatmentCounter();
        treatmentSectionCounter = ecrTreatment.getTreatmentSectionCounter();
        componentCounter = ecrTreatment.getComponentCounter();

        //endregion

        String value ="";
        int k =0;

        //region CONTAINER BOTTOM LAYER
        clinicalDocument.addNewCustodian().addNewAssignedCustodian().addNewRepresentedCustodianOrganization().addNewId();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().addNewAddr();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().addNewTelecom();


        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getIdArray(0).setExtension(mapToTranslatedValue("CUS101"));
        value = mapToTranslatedValue("CUS102");

        var element = clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization();
        element = mapToElementValue(value, element, "name");
        clinicalDocument.getCustodian().getAssignedCustodian().setRepresentedCustodianOrganization(element);

        value = mapToTranslatedValue("CUS103");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewStreetAddressLine();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(mapToCData(value));
        k = k+1;
        value = mapToTranslatedValue("CUS104");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewStreetAddressLine();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(mapToCData(value));
        k = k+1;

        k = 0;
        value = mapToTranslatedValue("CUS105");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewCity();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCityArray(k).set(mapToCData(value));
        k = k+1;

        k = 0;
        value = mapToTranslatedValue("CUS106");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewState();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStateArray(k).set(mapToCData(value));
        k = k+1;

        value = mapToTranslatedValue("CUS107");
        k = 0;
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewPostalCode();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getPostalCodeArray(k).set(mapToCData(value));
        k = k+1;

        value = mapToTranslatedValue("CUS108");
        k = 0;
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewCountry();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCountryArray(k).set(mapToCData(value));
        k = k+1;

        value = mapToTranslatedValue("CUS109");
        k = 0;
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getTelecom().setValue(value);
        k = k+1;

        clinicalDocument.addNewAuthor().addNewAssignedAuthor();
        clinicalDocument.getAuthorArray(0).addNewTime();
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().addNewId();

        value = mapToTranslatedValue("AUT101");
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getIdArray(0).setRoot(value);

        clinicalDocument.getAuthorArray(0).getAssignedAuthor().addNewAssignedPerson().addNewName();
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getAssignedPerson().getNameArray(0).addNewFamily();
        value = mapToTranslatedValue("AUT102");
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getAssignedPerson().getNameArray(0).getFamilyArray(0).set(mapToCData(value));

        OffsetDateTime now = OffsetDateTime.now();
        String formattedDateTime = formatDateTime(now);

        clinicalDocument.getAuthorArray(0).getTime().setValue(formattedDateTime);
        //endregion

        rootDocument.setClinicalDocument(clinicalDocument);

        //region XML CLEANUP
        XmlCursor cursor = rootDocument.newCursor();
        cursor.toFirstChild();
        cursor.setAttributeText(new QName("sdtcxmlnamespaceholder"), xmlNameSpaceHolder);
        cursor.setAttributeText(new QName("sdt"), "urn:hl7-org:sdtc");
        cursor.setAttributeText(new QName("xsi"), nameSpaceUrl);
        cursor.setAttributeText(new QName("schemaLocation"), xmlNameSpaceHolder + " CDA_SDTC.xsd");
        cursor.dispose();
        var result = convertXmlToString(rootDocument);
        //endregion

        return result;


    }

    private static String formatDateTime(OffsetDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssZ");
        return dateTime.format(formatter);
    }

    //region PATIENT
    private CdaPatientMapper mapToPatient(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument, int patientComponentCounter, String inv168)
            throws EcrCdaXmlException {

        try {
            CdaPatientMapper mapper = new CdaPatientMapper();
            for(var patient : input.getMsgPatients()) {

                //region VARIABLE
                String address1 ="";
                String address2 ="";
                String homeExtn="";
                String name1 = "Patient";
                String PAT_HOME_PHONE_NBR_TXT  ="";
                String PAT_WORK_PHONE_EXTENSION_TXT="";
                String wpNumber="";
                String cellNumber="";
                String PAT_NAME_FIRST_TXT="";
                String PAT_NAME_MIDDLE_TXT="";
                String PAT_NAME_PREFIX_CD="";
                String PAT_NAME_LAST_TXT="";
                String PAT_NAME_SUFFIX_CD="";

                int raceCodeCounter=0;
                int phoneCounter = 0;
                String PAT_RACE_DESC_TXT="";
                String PAT_ADDR_CENSUS_TRACT_TXT="";
                String PAT_EMAIL_ADDRESS_TXT="";
                String PAT_URL_ADDRESS_TXT="";
                String PAT_NAME_AS_OF_DT="";
                String PAT_ADDR_AS_OF_DT="";
                String PAT_PHONE_AS_OF_DT="";
                String PAT_PHONE_COUNTRY_CODE_TXT="";
                int patientIdentifier =0;
                int caseInvCounter= -1;
                int nameCounter = 1;
                //endregion
                CdaPatientField cdaPatientField = new CdaPatientField();

                if (input.getMsgPatients() != null && input.getMsgPatients().size() > 0) {
                    int k = 1;
                    Field[] fields = EcrMsgPatientDto.class.getDeclaredFields();

                    for (Field field : fields) {
                        if (!"numberOfField".equals(field.getName())) {
                            if (field.getName().equals("patPrimaryLanguageCd") &&
                                    patient.getPatPrimaryLanguageCd() != null && !patient.getPatPrimaryLanguageCd().isEmpty()) {
                                if(!clinicalDocument.isSetLanguageCode()){
                                    clinicalDocument.addNewLanguageCode();
                                }
                                clinicalDocument.getLanguageCode().setCode(patient.getPatPrimaryLanguageCd());
                            }
                            else if (field.getName().equals(patLocalId) &&
                                    patient.getPatLocalId() != null && !patient.getPatLocalId().isEmpty()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewId();
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setExtension(patient.getPatLocalId());
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setRoot("2.16.840.1.113883.4.1");
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setAssigningAuthorityName("LR");
                                patientIdentifier++;
                            }
                            else if (field.getName().equals("patIdMedicalRecordNbrTxt") &&
                                    patient.getPatIdMedicalRecordNbrTxt() != null && !patient.getPatIdMedicalRecordNbrTxt().isEmpty()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewId();
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setExtension(patient.getPatIdMedicalRecordNbrTxt());
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setRoot("2.16.840.1.113883.4.1");
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setAssigningAuthorityName("LR_MRN");
                                patientIdentifier++;
                            }
                            else if (field.getName().equals("patIdSsnTxt") &&
                                    patient.getPatIdSsnTxt() != null && !patient.getPatIdSsnTxt().isEmpty()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewId();
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setExtension(patient.getPatIdSsnTxt());
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setRoot("2.16.840.1.114222.4.5.1");
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setAssigningAuthorityName("SS");
                                patientIdentifier++;
                            }
                            else if (field.getName().equals("patAddrStreetAddr1Txt") && patient.getPatAddrStreetAddr1Txt() != null && !patient.getPatAddrStreetAddr1Txt().isEmpty()) {
                                address1 += patient.getPatAddrStreetAddr1Txt();
                            }
                            else if (field.getName().equals("patAddrStreetAddr2Txt") && patient.getPatAddrStreetAddr2Txt() != null && !patient.getPatAddrStreetAddr2Txt().isEmpty()) {
                                address2 += patient.getPatAddrStreetAddr2Txt();
                            }

                            else if(field.getName().equals("patAddrCityTxt") && patient.getPatAddrCityTxt() != null && !patient.getPatAddrCityTxt().isEmpty()) {
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                                }
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCityArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCity();
                                }
                                // original code start at index 1
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0)
                                        .getCityArray(0).set(mapToCData(patient.getPatAddrCityTxt()));
                                k++;
                            }
                            else if(field.getName().equals("patAddrStateCd") && patient.getPatAddrStateCd() != null && !patient.getPatAddrStateCd().isEmpty()) {
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                                }
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStateArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewState();
                                }
                                var nstate = mapToAddressType(patient.getPatAddrStateCd(), state);
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStateArray(0).set(mapToCData(nstate));
                                k++;
                            }
                            else if(field.getName().equals("patAddrZipCodeTxt") && patient.getPatAddrZipCodeTxt() != null && !patient.getPatAddrZipCodeTxt().isEmpty()) {
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                                }
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getPostalCodeArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewPostalCode();
                                }
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getPostalCodeArray(0).set(mapToCData(patient.getPatAddrZipCodeTxt()));
                                k++;
                            }
                            // PAT_ADDR_COUNTY_CD
                            else if(field.getName().equals("patAddrCountyCd") && patient.getPatAddrCountyCd() != null && !patient.getPatAddrCountyCd().isEmpty()) {
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                                }
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountyArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCounty();
                                }

                                var val = mapToAddressType(patient.getPatAddrCountyCd(), county);
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountyArray(0).set(mapToCData(val));
                                k++;
                            }
                            // PAT_ADDR_COUNTRY_CD
                            else if(field.getName().equals("patAddrCountryCd") && patient.getPatAddrCountryCd() != null && !patient.getPatAddrCountryCd().isEmpty()) {
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                                }
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountryArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCountry();
                                }
                                var val = mapToAddressType(patient.getPatAddrCountryCd(), country);
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountryArray(0).set(mapToCData(val));
                                k++;
                            }

                            else if (field.getName().equals("patWorkPhoneExtensionTxt") && patient.getPatWorkPhoneExtensionTxt() != null) {
                                PAT_WORK_PHONE_EXTENSION_TXT = patient.getPatWorkPhoneExtensionTxt().toString();
                            }
                            else if (field.getName().equals("patHomePhoneNbrTxt") && patient.getPatHomePhoneNbrTxt() != null) {
                                PAT_HOME_PHONE_NBR_TXT = patient.getPatHomePhoneNbrTxt();
                            }
                            else if (field.getName().equals("patWorkPhoneNbrTxt") && patient.getPatWorkPhoneNbrTxt() != null) {
                                wpNumber = patient.getPatWorkPhoneNbrTxt();
                            }
                            else if (field.getName().equals("patPhoneCountryCodeTxt") && patient.getPatPhoneCountryCodeTxt() != null) {
                                PAT_PHONE_COUNTRY_CODE_TXT = patient.getPatPhoneCountryCodeTxt().toString();
                            }
                            else if (field.getName().equals("patCellPhoneNbrTxt") && patient.getPatCellPhoneNbrTxt() != null) {
                                cellNumber = patient.getPatCellPhoneNbrTxt();
                            }
                            else if (field.getName().equals("patNamePrefixCd") && patient.getPatNamePrefixCd() != null && !patient.getPatNamePrefixCd().trim().isEmpty()) {
                                PAT_NAME_PREFIX_CD = patient.getPatNamePrefixCd();
                            }
                            else if (field.getName().equals("patNameFirstTxt") && patient.getPatNameFirstTxt() != null && !patient.getPatNameFirstTxt().trim().isEmpty()) {
                                PAT_NAME_FIRST_TXT = patient.getPatNameFirstTxt();
                            }
                            else if (field.getName().equals("patNameMiddleTxt") && patient.getPatNameMiddleTxt() != null && !patient.getPatNameMiddleTxt().trim().isEmpty()) {
                                PAT_NAME_MIDDLE_TXT = patient.getPatNameMiddleTxt();
                            }
                            else if (field.getName().equals("patNameLastTxt") && patient.getPatNameLastTxt() != null && !patient.getPatNameLastTxt().trim().isEmpty()) {
                                PAT_NAME_LAST_TXT = patient.getPatNameLastTxt();
                            }
                            else if (field.getName().equals("patNameSuffixCd") && patient.getPatNameSuffixCd() != null && !patient.getPatNameSuffixCd().trim().isEmpty()) {
                                PAT_NAME_SUFFIX_CD = patient.getPatNameSuffixCd();
                            }

                            // PAT_NAME_ALIAS_TXT
                            else if (field.getName().equals("patNameAliasTxt") && patient.getPatNameAliasTxt() != null && !patient.getPatNameAliasTxt().trim().isEmpty()) {
                                // CHECK ORIG: 211
                                if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                                }

                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                                }
                                else if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 1) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                                }

                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).getGivenArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).addNewGiven();
                                }

                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).setUse(new ArrayList<String> (Arrays.asList("P")));
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).getGivenArray(0).set(mapToCData(patient.getPatNameAliasTxt()));
                            }
                            // PAT_CURRENT_SEX_CD
                            else if(field.getName().equals("patCurrentSexCd") && patient.getPatCurrentSexCd() != null && !patient.getPatCurrentSexCd().isEmpty()) {
                                String questionCode = mapToQuestionId(patient.getPatCurrentSexCd());

                                if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                                }

                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().isSetAdministrativeGenderCode()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewAdministrativeGenderCode();
                                }
                                CE administrativeGender = mapToCEAnswerType(patient.getPatCurrentSexCd(), questionCode);
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setAdministrativeGenderCode(administrativeGender);
                            }
                            // PAT_BIRTH_DT
                            else if(field.getName().equals("patBirthDt") && patient.getPatBirthDt() != null) {
                                if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                                }

                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setBirthTime(mapToTsType(patient.getPatBirthDt().toString()));
                            }
                            // PAT_MARITAL_STATUS_CD
                            else if(field.getName().equals("patMaritalStatusCd") && patient.getPatMaritalStatusCd() != null  && !patient.getPatMaritalStatusCd().isEmpty()) {
                                String questionCode = mapToQuestionId(patient.getPatMaritalStatusCd());
                                CE ce = mapToCEAnswerType(patient.getPatMaritalStatusCd(), questionCode);
                                if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                                }
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setMaritalStatusCode(ce);
                            }
                            // PAT_RACE_CATEGORY_CD
                            else if(field.getName().equals("patRaceCategoryCd") && patient.getPatRaceCategoryCd() != null  && !patient.getPatRaceCategoryCd().isEmpty()) {
                                List<CE> raceCode2List = new ArrayList<>();
                                long counter = patient.getPatRaceCategoryCd().chars().filter(x -> x == '|').count();

                                List<String> raceCatList = new ArrayList<>();
                                if (counter > 0) {
                                    raceCatList = GetStringsBeforePipe(patient.getPatRaceCategoryCd());
                                } else {
                                    raceCatList.add(patient.getPatRaceCategoryCd());
                                }
                                for(int i = 0; i < raceCatList.size(); i++) {
                                    String val = raceCatList.get(i);
                                    String questionCode = mapToQuestionId("PAT_RACE_CATEGORY_CD");
                                    if (!questionCode.isEmpty()) {
                                        CE ce = mapToCEAnswerType(val, questionCode);
                                        raceCode2List.add(ce);
                                        raceCodeCounter = i;
                                    }
                                }
                            }
                            // PAT_RACE_DESC_TXT
                            else if(field.getName().equals("patRaceDescTxt") && patient.getPatRaceDescTxt() != null  && !patient.getPatRaceDescTxt().isEmpty()) {
                                if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                                }
                                var counter = 0;
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewRaceCode2();
                                }
                                else {
                                    counter = clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array().length + 1 - 1;
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewRaceCode2();
                                }
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array(counter).setCode("OTH");
                                ED originalText = ED.Factory.newInstance();
                                // CHECK LINE 246
                                XmlCursor cursor = originalText.newCursor();
                                cursor.toEndDoc();
                                cursor.beginElement("originalText");
                                cursor.insertChars("[CDATA]" + patient.getPatRaceDescTxt() + "[CDATA]");
                                cursor.dispose();


                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array(counter).setOriginalText(originalText);

                            }

                            // PAT_ETHNIC_GROUP_IND_CD
                            else if(field.getName().equals("patEthnicGroupIndCd") && patient.getPatEthnicGroupIndCd() != null  && !patient.getPatEthnicGroupIndCd().isEmpty()) {
                                if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                                }

                                String questionCode = mapToQuestionId(patient.getPatEthnicGroupIndCd());
                                CE ce = mapToCEAnswerType(patient.getPatEthnicGroupIndCd(), questionCode);

                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setEthnicGroupCode(ce);
                            }

                            // PAT_BIRTH_COUNTRY_CD
                            else if(field.getName().equals("patBirthCountryCd") && patient.getPatBirthCountryCd() != null  && !patient.getPatBirthCountryCd().isEmpty()) {
                                if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                                }

                                if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().isSetBirthplace()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewBirthplace();
                                }


                                String val = mapToAddressType(patient.getPatBirthCountryCd(), country);
                                POCDMT000040Place place = POCDMT000040Place.Factory.newInstance();
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getBirthplace().setPlace(place);

                                AD ad = AD.Factory.newInstance();
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getBirthplace().getPlace().setAddr(ad);

                                AdxpCounty county = AdxpCounty.Factory.newInstance();

                                XmlCursor cursor = county.newCursor();
                                cursor.setTextValue("[CDATA]" + val + "[CDATA]");
                                cursor.dispose();

                                AdxpCounty[] countyArr = {county};
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getBirthplace().getPlace().getAddr().setCountyArray(countyArr);
                            }

                            // PAT_ADDR_CENSUS_TRACT_TXT
                            else if(field.getName().equals("patAddrCensusTractTxt") && patient.getPatAddrCensusTractTxt() != null  && !patient.getPatAddrCensusTractTxt().isEmpty()) {

                                if ( clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                                }
                                AdxpCensusTract census = AdxpCensusTract.Factory.newInstance();
                                XmlCursor cursor = census.newCursor();
                                cursor.setTextValue( "[CDATA]" + patient.getPatAddrCensusTractTxt() + "[CDATA]");
                                cursor.dispose();

                                int c = 0;
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCensusTractArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCensusTract();
                                } else {
                                    c = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCensusTractArray().length;
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCensusTract();
                                }

                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).setCensusTractArray(c,census);
                                k++;
                            }

                            else if (field.getName().equals("patEmailAddressTxt") && patient.getPatEmailAddressTxt() != null && !patient.getPatEmailAddressTxt().trim().isEmpty()) {
                                PAT_EMAIL_ADDRESS_TXT = patient.getPatEmailAddressTxt();
                            }
                            else if (field.getName().equals("patUrlAddressTxt") && patient.getPatUrlAddressTxt() != null && !patient.getPatUrlAddressTxt().trim().isEmpty()) {
                                PAT_URL_ADDRESS_TXT = patient.getPatUrlAddressTxt();
                            }
                            // PAT_NAME_AS_OF_DT
                            else if(field.getName().equals("patNameAsOfDt") && patient.getPatNameAsOfDt() != null) {
                                if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                                }
                                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                                }

                                PN pn = PN.Factory.newInstance();
                                IVLTS time = IVLTS.Factory.newInstance();
                                var ts = mapToTsType(patient.getPatNameAsOfDt().toString());

                                XmlCursor cursor = time.newCursor();
                                cursor.toEndDoc();

                                cursor.beginElement("low");
                                cursor.insertAttributeWithValue("value",  ts.getValue());

                                cursor.toEndDoc();
                                cursor.removeXml();
                                cursor.dispose();


                                pn.setValidTime(time);
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setNameArray(0, pn);
                            }

                            else if (field.getName().equals("patPhoneAsOfDt") &&  patient.getPatPhoneAsOfDt() != null) {
                                PAT_PHONE_AS_OF_DT = patient.getPatPhoneAsOfDt().toString();
                            }

                            // PAT_INFO_AS_OF_DT
                            else if (
                                    (field.getName().equals("patInfoAsOfDt") && patient.getPatInfoAsOfDt() != null) ||
                                            (field.getName().equals("patAddrCommentTxt") && patient.getPatAddrCommentTxt() != null && !patient.getPatAddrCommentTxt().isEmpty()) ||
                                            (field.getName().equals("patAdditionalGenderTxt") && patient.getPatAdditionalGenderTxt() != null && !patient.getPatAdditionalGenderTxt().isEmpty()) ||
                                            (field.getName().equals("patSpeaksEnglishIndCd") && patient.getPatSpeaksEnglishIndCd() != null && !patient.getPatSpeaksEnglishIndCd().isEmpty()) ||
                                            (field.getName().equals("patIdStateHivCaseNbrTxt") && patient.getPatIdStateHivCaseNbrTxt() != null && !patient.getPatIdStateHivCaseNbrTxt().isEmpty()) ||
                                            (field.getName().equals("patEthnicityUnkReasonCd") && patient.getPatEthnicityUnkReasonCd() != null && !patient.getPatEthnicityUnkReasonCd().isEmpty()) ||
                                            (field.getName().equals("patSexUnkReasonCd") && patient.getPatSexUnkReasonCd() != null && !patient.getPatSexUnkReasonCd().isEmpty()) ||
                                            (field.getName().equals("patPhoneCommentTxt") && patient.getPatPhoneCommentTxt() != null && !patient.getPatPhoneCommentTxt().isEmpty()) ||
                                            (field.getName().equals("patDeceasedIndCd") && patient.getPatDeceasedIndCd() != null && !patient.getPatDeceasedIndCd().isEmpty()) ||
                                            (field.getName().equals("patDeceasedDt") && patient.getPatDeceasedDt() != null) ||
                                            (field.getName().equals("patPreferredGenderCd") && patient.getPatPreferredGenderCd() != null && !patient.getPatPreferredGenderCd().isEmpty()) ||
                                            (field.getName().equals("patReportedAge") && patient.getPatReportedAge() != null) ||
                                            (field.getName().equals("patReportedAgeUnitCd") && patient.getPatReportedAgeUnitCd() != null && !patient.getPatReportedAgeUnitCd().isEmpty()) ||
                                            (field.getName().equals("patCommentTxt") && patient.getPatCommentTxt() != null && !patient.getPatCommentTxt().isEmpty()) ||
                                            (field.getName().equals("patBirthSexCd") && patient.getPatBirthSexCd() != null && !patient.getPatBirthSexCd().isEmpty())
                            ) {
                                String colName = "";
                                String value = "";

                                if (field.getName().equals("patInfoAsOfDt") && isFieldValid(field.getName(), patient.getPatInfoAsOfDt())) {
                                    colName = "PAT_INFO_AS_OF_DT";
                                    value = patient.getPatInfoAsOfDt().toString();
                                } else if (field.getName().equals("patAddrCommentTxt") && isFieldValid(field.getName(), patient.getPatAddrCommentTxt())) {
                                    colName = "PAT_ADDR_COMMENT_TXT";
                                    value = patient.getPatAddrCommentTxt();
                                } else if (field.getName().equals("patAdditionalGenderTxt") && isFieldValid(field.getName(), patient.getPatAdditionalGenderTxt())) {
                                    colName = "PAT_ADDITIONAL_GENDER_TXT";
                                    value = patient.getPatAdditionalGenderTxt();
                                } else if (field.getName().equals("patSpeaksEnglishIndCd") && isFieldValid(field.getName(), patient.getPatSpeaksEnglishIndCd())) {
                                    colName = "PAT_SPEAKS_ENGLISH_IND_CD";
                                    value = patient.getPatSpeaksEnglishIndCd();
                                } else if (field.getName().equals("patIdStateHivCaseNbrTxt") && isFieldValid(field.getName(), patient.getPatIdStateHivCaseNbrTxt())) {
                                    colName = "PAT_ID_STATE_HIV_CASE_NBR_TXT";
                                    value = patient.getPatIdStateHivCaseNbrTxt();
                                } else if (field.getName().equals("patEthnicityUnkReasonCd") && isFieldValid(field.getName(), patient.getPatEthnicityUnkReasonCd())) {
                                    colName = "PAT_ETHNICITY_UNK_REASON_CD";
                                    value = patient.getPatEthnicityUnkReasonCd();
                                } else if (field.getName().equals("patSexUnkReasonCd") && isFieldValid(field.getName(), patient.getPatSexUnkReasonCd())) {
                                    colName = "PAT_SEX_UNK_REASON_CD";
                                    value = patient.getPatSexUnkReasonCd();
                                } else if (field.getName().equals("patPhoneCommentTxt") && isFieldValid(field.getName(), patient.getPatPhoneCommentTxt())) {
                                    colName = "PAT_PHONE_COMMENT_TXT";
                                    value = patient.getPatPhoneCommentTxt();
                                } else if (field.getName().equals("patDeceasedIndCd") && isFieldValid(field.getName(), patient.getPatDeceasedIndCd())) {
                                    colName = "PAT_DECEASED_IND_CD";
                                    value = patient.getPatDeceasedIndCd();
                                } else if (field.getName().equals("patDeceasedDt") && isFieldValid(field.getName(), patient.getPatDeceasedDt())) {
                                    colName = "PAT_DECEASED_DT";
                                    value = patient.getPatDeceasedDt().toString();
                                } else if (field.getName().equals("patPreferredGenderCd") && isFieldValid(field.getName(), patient.getPatPreferredGenderCd())) {
                                    colName = "PAT_PREFERRED_GENDER_CD";
                                    value = patient.getPatPreferredGenderCd();
                                } else if (field.getName().equals("patReportedAge") && patient.getPatReportedAge() != null) {
                                    colName = "PAT_REPORTED_AGE";
                                    value = String.valueOf(patient.getPatReportedAge());
                                } else if (field.getName().equals("patReportedAgeUnitCd") && isFieldValid(field.getName(), patient.getPatReportedAgeUnitCd())) {
                                    colName = "PAT_REPORTED_AGE_UNIT_CD";
                                    value = patient.getPatReportedAgeUnitCd();
                                } else if (field.getName().equals("patCommentTxt") && isFieldValid(field.getName(), patient.getPatCommentTxt())) {
                                    colName = "PAT_COMMENT_TXT";
                                    value = patient.getPatCommentTxt();
                                } else if (field.getName().equals("patBirthSexCd") && isFieldValid(field.getName(), patient.getPatBirthSexCd())) {
                                    colName = "PAT_BIRTH_SEX_CD";
                                    value = patient.getPatBirthSexCd();
                                }

                                if (patientComponentCounter < 0 ) {

                                    if (clinicalDocument.getComponent() == null) {
                                        clinicalDocument.addNewComponent();
                                    }

                                    if (!clinicalDocument.getComponent().isSetStructuredBody()) {
                                        clinicalDocument.getComponent().addNewStructuredBody();
                                    }

                                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                                        patientComponentCounter = 0;
                                    }
                                    else {
                                        patientComponentCounter = clinicalDocument.getComponent().addNewStructuredBody().getComponentArray().length+ 1;
                                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                                    }

                                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection() == null) {
                                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).addNewSection();
                                    }

                                    if (!clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().isSetId()) {
                                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().addNewId();
                                    }

                                    if (!clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().isSetCode()) {
                                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().addNewCode();
                                    }

                                    if (!clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().isSetTitle()) {
                                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().addNewTitle();
                                    }

                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setRoot(rootId);
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setExtension(inv168);
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setAssigningAuthorityName("LR");
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCode("297622");
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystem(codeSystem);
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystemName(codeSystemName);
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setDisplayName("Social History");
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getTitle().set(mapToCData("SOCIAL HISTORY INFORMATION"));

                                }

                                POCDMT000040Component3 comp = clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter);

                                int patEntityCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(0).getSection().getEntryArray().length;


                                var compPatient = mapToPatient(patEntityCounter, colName, value, comp);

                                clinicalDocument.getComponent().getStructuredBody().setComponentArray(patientComponentCounter, compPatient);
                            }


                        }
                        if (k > 1) {
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                            }
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).setUse(new ArrayList<String>(Arrays.asList("H")));
                        }
                        if (k> 1 && field.getName().equals("patAddrAsOfDt") && patient.getPatAddrAsOfDt() != null ) {
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                            }
                            AD element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0);
                            var ad = mapToUsableTSElement(patient.getPatAddrAsOfDt().toString(), element, useablePeriod);
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().setAddrArray(0, (AD) ad);
                        }

                    }
                }

                if(!PAT_NAME_PREFIX_CD.isEmpty()) {
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                    }
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getPrefixArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewPrefix();
                    }
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getPrefixArray(0).set(mapToCData(PAT_NAME_PREFIX_CD));
                    nameCounter++;
                }
                if(!PAT_NAME_FIRST_TXT.isEmpty()) {
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                    }
                    var count = 0;
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
                    } else {
                        count = clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length + 1 - 1;
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
                    }

                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).setUse(new ArrayList(Arrays.asList("L")));
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray(count).set(mapToCData(PAT_NAME_FIRST_TXT));
                    nameCounter++;
                }
                if(!PAT_NAME_MIDDLE_TXT.isEmpty()) {
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                    }
                    var count = 0;
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
                    } else {
                        count = clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length + 1 - 1;
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
                    }
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray(count).set(mapToCData(PAT_NAME_MIDDLE_TXT));
                    nameCounter++;
                }
                if(!PAT_NAME_LAST_TXT.isEmpty()) {
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                    }
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getFamilyArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewFamily();
                    }
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getFamilyArray(0).set(mapToCData(PAT_NAME_LAST_TXT));
                    nameCounter++;
                }
                if(!PAT_NAME_SUFFIX_CD.isEmpty()) {
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                    }
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getSuffixArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewSuffix();
                    }
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getSuffixArray(0).set(mapToCData(PAT_NAME_SUFFIX_CD));
                    nameCounter++;
                }
                if (!PAT_HOME_PHONE_NBR_TXT.isEmpty()) {
                    int pCount = 0;
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    } else {
                        pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    }

                    String phoneHome = "";
                    if(!PAT_PHONE_COUNTRY_CODE_TXT.isEmpty()) {
                        PAT_HOME_PHONE_NBR_TXT =  "+"+PAT_PHONE_COUNTRY_CODE_TXT+"-"+PAT_HOME_PHONE_NBR_TXT;
                    }
                    int homeExtnSize = homeExtn.length();
                    if(homeExtnSize>0){
                        phoneHome=PAT_HOME_PHONE_NBR_TXT+ ";ext="+ homeExtn;
                    }
                    else {
                        phoneHome=PAT_HOME_PHONE_NBR_TXT;
                    }
                    if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                        TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                        element.set(XmlObject.Factory.parse("<stud>stud</stud>"));
                        var out = mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, useablePeriod);
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                    }
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("HP")));
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(phoneHome);

                    phoneCounter =phoneCounter +1;
                }

                // wpNumber
                if (!wpNumber.isEmpty()) {
                    int pCount = 0;
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    } else {
                        pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    }

                    if(!PAT_WORK_PHONE_EXTENSION_TXT.isEmpty()){
                        wpNumber=wpNumber+ ";ext="+ PAT_WORK_PHONE_EXTENSION_TXT;
                    }
                    if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                        TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                        element.set(XmlObject.Factory.parse("<stud>stud</stud>"));
                        var out = mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, useablePeriod);
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                    }

                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("WP")));
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(wpNumber);

                    phoneCounter =phoneCounter +1;
                }

                // cellNumber
                if(!cellNumber.isEmpty()) {
                    int pCount = 0;
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    } else {
                        pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    }

                    if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                        TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                        // CHECK mapToUsableTSElement
                        element.set(XmlObject.Factory.parse("<stud>stud</stud>"));
                        var out = mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, useablePeriod);
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                    }

                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("MC")));
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(cellNumber);
                    phoneCounter =phoneCounter +1;
                }

                // PAT_EMAIL_ADDRESS_TXT
                if(!PAT_EMAIL_ADDRESS_TXT.isEmpty()) {
                    int pCount = 0;
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    } else {
                        pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    }


                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList(Arrays.asList("HP")));
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue("mailto:"+PAT_EMAIL_ADDRESS_TXT);
                    if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                        TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                        // CHECK mapToUsableTSElement
                        element.set(XmlObject.Factory.parse("<stud>stud</stud>"));
                        var out = mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, useablePeriod);
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                    }

                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("HP")));
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue("mailto:" + PAT_EMAIL_ADDRESS_TXT);
                    phoneCounter =phoneCounter +1;
                }

                // PAT_URL_ADDRESS_TXT
                if(!PAT_URL_ADDRESS_TXT.isEmpty()) {
                    int pCount = 0;
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    } else {
                        pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                    }
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList(Arrays.asList("HP")));
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(PAT_URL_ADDRESS_TXT);
                    if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                        TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                        // CHECK mapToUsableTSElement
                        element.set(XmlObject.Factory.parse("<stud>stud</stud>"));
                        var out = mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, useablePeriod);
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                    }

                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("HP")));
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(PAT_URL_ADDRESS_TXT);
                    phoneCounter =phoneCounter +1;
                }

                if(!address1.isEmpty()) {
                    int c1 = 0;
                    int c2 = 0;
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                    }
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                    }
                    else {
                        c2 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length;
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray();
                    }

                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray(c2).set(mapToCData(address1));
                }

                if(!address2.isEmpty()) {
                    int c1 = 0;
                    int c2 = 0;
                    if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                    }
                    else {
                        c1 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length - 1;

                        if ( clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length == 0) {
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                        } else {
                            c2 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length;
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                        }


                    }

                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray(c2).set(mapToCData(address2));
                }

            }
            mapper.setClinicalDocument(clinicalDocument);
            mapper.setPatientComponentCounter(patientComponentCounter);
            mapper.setInv168(inv168);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }
    //endregion

    //region CASE
    public CdaCaseMapper mapToCaseTop(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument,
                                      int componentCounter, int clinicalCounter, int componentCaseCounter,
                                      String inv168) throws EcrCdaXmlException {

        try {
            CdaCaseMapper mapper = new CdaCaseMapper();
            /**
             * CASE - 1st PHASE TESTED
             * **/
            if(!input.getMsgCases().isEmpty()) {

                if (clinicalDocument.getComponent() == null) {
                    clinicalDocument.addNewComponent().addNewStructuredBody().addNewComponent();

                }
                else {
                    if (!clinicalDocument.getComponent().isSetStructuredBody()) {
                        clinicalDocument.getComponent().addNewStructuredBody();
                    }
                    else {
                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                        }
                    }
                }

                // componentCounter should be zero initially
                for(int i = 0; i < input.getMsgCases().size(); i++) {
                    if (componentCounter < 0) {
                        componentCounter++;
                        var c = 0;

                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                        } else {
                            c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                        }


                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection().addNewId();
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
                        }
                        else {
                            if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getId() == null) {
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewId();
                            }
                            if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode() == null) {
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                            }
                            if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle() == null) {
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
                            }
                        }
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getId().setRoot(rootId);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getId().setExtension(inv168);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getId().setAssigningAuthorityName("LR");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("55752-0");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem(codeSystem);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName(codeSystemName);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Clinical Information");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle().set(mapToStringData("CLINICAL INFORMATION"));

                        componentCounter = c;

                    }
                    componentCaseCounter = componentCounter;
                    // MAP TO CASE code line 438
                    clinicalCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                    // CHECK mapToCase
                    POCDMT000040StructuredBody output = clinicalDocument.getComponent().getStructuredBody();

                    var mappedCase = mapToCase(clinicalCounter, input.getMsgCases().get(i), output);
                    clinicalDocument.getComponent().setStructuredBody(mappedCase);
                    componentCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length - 1;
                }

            }

            mapper.setClinicalDocument(clinicalDocument);
            mapper.setClinicalCounter(clinicalCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setComponentCaseCounter(componentCaseCounter);
            mapper.setInv168(inv168);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }
    //endregion

    //region XML Answer
    public CdaXmlAnswerMapper mapToXmlAnswerTop(EcrSelectedRecord input,
                                                POCDMT000040ClinicalDocument1 clinicalDocument,
                                                int componentCounter) throws EcrCdaXmlException {

        try {
            CdaXmlAnswerMapper mapper = new CdaXmlAnswerMapper();
            if(input.getMsgXmlAnswers() != null && !input.getMsgXmlAnswers().isEmpty()) {
                for(int i = 0; i < input.getMsgXmlAnswers().size(); i++) {
                    componentCounter++;
                    int c = 0;
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                    } else {
                        c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                    }
                    POCDMT000040Component3 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c);
                    var mappedData = mapToExtendedData(input.getMsgXmlAnswers().get(i), out);
                    clinicalDocument.getComponent().getStructuredBody().setComponentArray(c, mappedData);
                }
            }
            mapper.setClinicalDocument(clinicalDocument);
            mapper.setComponentCounter(componentCounter);
            return mapper;
        } catch ( Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }
    //endregion


    //region PROVIDER
    public CdaProviderMapper mapToProviderTop(EcrSelectedRecord input, POCDMT000040Section clinicalDocument,
                                              String inv168, int performerComponentCounter, int componentCounter,
                                              int performerSectionCounter) throws EcrCdaXmlException {

        try {
            CdaProviderMapper mapper = new CdaProviderMapper();

            if(input.getMsgProviders() != null && !input.getMsgProviders().isEmpty()) {
                // 449
                for(int i = 0; i < input.getMsgProviders().size(); i++) {
                    if (input.getMsgProviders().get(i).getPrvAuthorId() != null
                            && input.getMsgProviders().get(i).getPrvAuthorId().equalsIgnoreCase(inv168)) {
                        // ignore
                    }
                    else {
                        int c = 0;


                        if (clinicalDocument.getTitle() == null) {
                            clinicalDocument.addNewTitle();
                        }

                        if (clinicalDocument.getCode() == null) {
                            clinicalDocument.addNewCode();
                        }

                        if (performerComponentCounter < 1) {
                            componentCounter++;
                            performerComponentCounter = componentCounter;

                            var nestedCode = code;
                            if (nestedCode.contains("-")) {
                                nestedCode = nestedCode.replaceAll("-", "");
                            }
                            clinicalDocument.getCode().setCode(nestedCode);
                            clinicalDocument.getCode().setCodeSystem(clinicalCodeSystem);
                            clinicalDocument.getCode().setCodeSystemName(clinicalCodeSystemName);
                            clinicalDocument.getCode().setDisplayName(codeDisplayName);
                            clinicalDocument.getTitle().set(mapToStringData(clinicalTitle));
                        }

                        performerSectionCounter = clinicalDocument.getEntryArray().length;

                        if ( clinicalDocument.getEntryArray().length == 0) {
                            clinicalDocument.addNewEntry();
                            performerSectionCounter = 0;
                        }
                        else {
                            performerSectionCounter = clinicalDocument.getEntryArray().length;
                            clinicalDocument.addNewEntry();
                        }


                        if (clinicalDocument.getEntryArray(performerSectionCounter).getAct() == null) {
                            clinicalDocument.getEntryArray(performerSectionCounter).addNewAct();
                            clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                        } else {
                            clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                        }
                        // CHECK mapToPSN
                        POCDMT000040Participant2 out = clinicalDocument.getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
                        POCDMT000040Participant2 output = mapToPSN(
                                input.getMsgProviders().get(i),
                                out
                        );
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

                        clinicalDocument.getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);

                        if (clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode() == null){
                            clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewCode();
                        }
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCode("PSN");
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem(clinicalCodeSystem);
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName(clinicalCodeSystemName);
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName(actCodeDisplayName);

                    }
                }
            }
            mapper.setClinicalSection(clinicalDocument);
            mapper.setPerformerSectionCounter(performerSectionCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setPerformerComponentCounter(performerComponentCounter);
            mapper.setInv168(inv168);
            return mapper;
        } catch ( Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }
    //endregion

    //region ORGANIZATION
    private CdaOrganizationMapper mapToOrganizationTop(EcrSelectedRecord input, POCDMT000040Section clinicalDocument,
                                                       int performerComponentCounter, int componentCounter,
                                                       int performerSectionCounter) throws EcrCdaXmlException {

        try {
            CdaOrganizationMapper mapper = new CdaOrganizationMapper();
            if(input.getMsgOrganizations()!= null && !input.getMsgOrganizations().isEmpty()) {
                // 474
                for(int i = 0; i < input.getMsgOrganizations().size(); i++) {

                    int c = 0;

                    if (clinicalDocument.getCode() == null) {
                        clinicalDocument.addNewCode();
                    }

                    if (clinicalDocument.getTitle() == null) {
                        clinicalDocument.addNewTitle();
                    }

                    if (performerComponentCounter < 1) {
                        componentCounter++;
                        performerComponentCounter = componentCounter;
                        clinicalDocument.getCode().setCode(code);
                        clinicalDocument.getCode().setCodeSystem(clinicalCodeSystem);
                        clinicalDocument.getCode().setCodeSystemName(clinicalCodeSystemName);
                        clinicalDocument.getCode().setDisplayName(codeDisplayName);
                        clinicalDocument.getTitle().set(mapToStringData(clinicalTitle));


                    }
                    performerSectionCounter = clinicalDocument.getEntryArray().length;
                    if ( clinicalDocument.getEntryArray().length == 0) {
                        clinicalDocument.addNewEntry();
                        performerSectionCounter = 0;
                    }
                    else {
                        performerSectionCounter = clinicalDocument.getEntryArray().length;
                        clinicalDocument.addNewEntry();
                    }


                    if (clinicalDocument.getEntryArray(performerSectionCounter).getAct() == null) {
                        clinicalDocument.getEntryArray(performerSectionCounter).addNewAct();
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                    } else {
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                    }

                    // CHECK mapToORG
                    POCDMT000040Participant2 out = clinicalDocument.getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
                    POCDMT000040Participant2 output = mapToORG(input.getMsgOrganizations().get(i), out);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

                    clinicalDocument.getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);

                    if (clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode() == null){
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewCode();
                    }

                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCode("ORG");
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem(clinicalCodeSystem);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName(clinicalCodeSystemName);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName(actCodeDisplayName);

                }
            }
            mapper.setClinicalSection(clinicalDocument);
            mapper.setPerformerComponentCounter(performerComponentCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setPerformerSectionCounter(performerSectionCounter);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }
    //endregion

    //region PLACE
    private CdaPlaceMapper mapToPlaceTop(EcrSelectedRecord input,
                                         int performerComponentCounter, int componentCounter,
                                         int performerSectionCounter,
                                         POCDMT000040Section section) throws EcrCdaXmlException{
        try {
            CdaPlaceMapper mapper = new CdaPlaceMapper();
            if(input.getMsgPlaces() != null && !input.getMsgPlaces().isEmpty()) {
                // 498
                for(int i = 0; i < input.getMsgPlaces().size(); i++) {

                    if (section == null) {
                        section = POCDMT000040Section.Factory.newInstance();
                        section.addNewCode();
                        section.addNewTitle();
                    }


                    if (performerComponentCounter < 1) {
                        componentCounter++;
                        performerComponentCounter = componentCounter;

                        section.getCode().setCode(code);
                        section.getCode().setCodeSystem(clinicalCodeSystem);
                        section.getCode().setCodeSystemName(clinicalCodeSystemName);
                        section.getCode().setDisplayName(codeDisplayName);
                        section.getTitle().set(mapToStringData(clinicalTitle));
                    }

                    int c = 0;
                    if ( section.getEntryArray().length == 0) {
                        section.addNewEntry();
                        c = 0;
                    }
                    else {
                        c = section.getEntryArray().length;
                        section.addNewEntry();
                    }

                    performerSectionCounter = c;


                    if (section.getEntryArray(c).getAct() == null) {
                        section.getEntryArray(c).addNewAct();
                        section.getEntryArray(c).getAct().addNewParticipant();
                    } else {
                        section.getEntryArray(c).getAct().addNewParticipant();
                    }

                    // CHECK mapToPlace
                    POCDMT000040Participant2 out = section.getEntryArray(c).getAct().getParticipantArray(0);
                    POCDMT000040Participant2 output = mapToPlace(input.getMsgPlaces().get(i), out);
                    section.getEntryArray(c).getAct().setParticipantArray(0, output);

                    section.getEntryArray(c).setTypeCode(XActRelationshipEntry.COMP);
                    section.getEntryArray(c).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                    section.getEntryArray(c).getAct().setMoodCode(XDocumentActMood.EVN);

                    if (section.getEntryArray(c).getAct().getCode() == null){
                        section.getEntryArray(c).getAct().addNewCode();
                    }
                    section.getEntryArray(c).getAct().getCode().setCode("PLC");
                    section.getEntryArray(c).getAct().getCode().setCodeSystem(clinicalCodeSystem);
                    section.getEntryArray(c).getAct().getCode().setCodeSystemName(clinicalCodeSystemName);
                    section.getEntryArray(c).getAct().getCode().setDisplayName(actCodeDisplayName);

                }
            }

            mapper.setSection(section);
            mapper.setPerformerComponentCounter(performerComponentCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setPerformerComponentCounter(performerComponentCounter);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }
    //endregion

    //region INTERVIEW
    private CdaInterviewMapper mapToInterviewTop(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument,
                                                 int interviewCounter, int componentCounter) throws EcrCdaXmlException {
        try {
            CdaInterviewMapper mapper = new CdaInterviewMapper();
            if(input.getMsgInterviews() != null && !input.getMsgInterviews().isEmpty()) {
                // 523
                for(int i = 0; i < input.getMsgInterviews().size(); i++) {

                    int c = 0;
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                    } else {
                        c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                    }
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                    } else {
                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                        }
                    }


                    if (interviewCounter < 1) {
                        interviewCounter = componentCounter + 1;
                        componentCounter++;

                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("IXS");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem(clinicalCodeSystem);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName(clinicalCodeSystemName);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Interviews");

                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
                        }
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle().set(mapToStringData("INTERVIEW SECTION"));
                    }

                    POCDMT000040Component3 ot = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c);
                    // CHECK mapToInterview

                    POCDMT000040Component3 output = mapToInterview(input.getMsgInterviews().get(i), ot);
                    clinicalDocument.getComponent().getStructuredBody().setComponentArray(c, output);
                }
            }

            mapper.setClinicalDocument(clinicalDocument);
            mapper.setInterviewCounter(interviewCounter);
            mapper.setComponentCounter(componentCounter);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }
    //endregion

    //region TREATMENT
    private CdaTreatmentMapper mapToTreatmentTop(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument,
                                                 int treatmentCounter, int componentCounter,
                                                 int treatmentSectionCounter) throws EcrCdaXmlException {
        try {
            CdaTreatmentMapper mapper = new CdaTreatmentMapper();
            if(input.getMsgTreatments() != null && !input.getMsgTreatments().isEmpty()) {
                // 543
                for(int i = 0; i < input.getMsgTreatments().size(); i++) {

                    int c = 0;
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                    } else {
                        c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                    }
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                    } else {
                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                        }
                    }



                    if (treatmentCounter < 1) {
                        treatmentCounter++;
                        componentCounter++;
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("55753-8");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem(codeSystem);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName(codeSystemName);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Treatment Information");

                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
                        }
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle().set(mapToStringData("TREATMENT INFORMATION"));

                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getText() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewText();
                        }
                    }

                    int cTreatment = 0;
                    if ( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length == 0) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry().addNewSubstanceAdministration();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(0).getSubstanceAdministration().addNewStatusCode();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(0).getSubstanceAdministration().addNewEntryRelationship();
                    } else {
                        cTreatment = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length;
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry().addNewSubstanceAdministration();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().addNewStatusCode();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().addNewEntryRelationship();
                    }

                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().getStatusCode().setCode("active");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().getEntryRelationshipArray(0).setTypeCode(XActRelationshipEntryRelationship.COMP);
                    var outpp = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment)
                            .getSubstanceAdministration();
                    String treatmentvalue = "";
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().setClassCode("SBADM");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().setMoodCode(XDocumentSubstanceMood.EVN);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().setNegationInd(false);

                    var o1 = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration();
                    var o2 = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getText();
                    CdaTreatmentAdministrationMapper mappedVal = mapToTreatment(input.getMsgTreatments().get(0),
                            o1,
                            o2,
                            cTreatment);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).setSubstanceAdministration(mappedVal.getAdministration());
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().setText(mappedVal.getText());
                    treatmentSectionCounter= treatmentSectionCounter+1;
                }
            }

            mapper.setClinicalDocument(clinicalDocument);
            mapper.setTreatmentCounter(treatmentCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setTreatmentSectionCounter(treatmentSectionCounter);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }


    }
    //endregion


    private String convertXmlToString(ClinicalDocumentDocument1 clinicalDocument) throws EcrCdaXmlException {
        try {
            XmlOptions options = new XmlOptions();
            options.setSavePrettyPrint();
            options.setSavePrettyPrintIndent(4);  // Set indentation

            // Use a default namespace instead of a prefixed one (like urn:)
            options.setUseDefaultNamespace();

            // Set to always use full tags instead of self-closing tags
            options.setSaveNoXmlDecl();
            options.setSaveOuter();


            String xmlOutput = clinicalDocument.xmlText(options);

            xmlOutput = xmlOutput.replaceAll("<STRING[^>]*>([^<]+)</STRING>", "$1"); // remove string tag
            xmlOutput = xmlOutput.replaceAll("<CDATA[^>]*>(.*?)</CDATA>", "<![CDATA[$1]]>"); // replace CDATA with real CDATA
            xmlOutput = xmlOutput.replaceAll("<(\\w+)></\\1>", ""); // remove empty <tag></tag>
            xmlOutput = xmlOutput.replaceAll("<(\\w+)/>", ""); // remove empty <tag/>
            xmlOutput = xmlOutput.replaceAll("<stud xmlns=\"\">stud</stud>", ""); // remove stud tag
            xmlOutput = xmlOutput.replaceAll("(?m)^\\s*$[\n\r]{1,}", ""); // remove new line

            xmlOutput = xmlOutput.replaceAll("sdtcxmlnamespaceholder=\""+ xmlNameSpaceHolder +"\"", "xmlns:sdtcxmlnamespaceholder=\""+xmlNameSpaceHolder+"\"");
            xmlOutput = xmlOutput.replaceAll("sdt=\"urn:hl7-org:sdtc\"", "xmlns:sdt=\"urn:hl7-org:sdtc\"");
            xmlOutput = xmlOutput.replaceAll("xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
            xmlOutput = xmlOutput.replaceAll("schemaLocation=\""+ xmlNameSpaceHolder +" CDA_SDTC.xsd\"", "xsi:schemaLocation=\""+xmlNameSpaceHolder +" CDA_SDTC.xsd\"");


            xmlOutput = xmlOutput.replaceAll("\\^NOT_MAPPED", "");
            xmlOutput = xmlOutput.replaceAll("NOT_MAPPED","");

            xmlOutput = "<?xml version=\"1.0\"?>\n" + xmlOutput;
            return xmlOutput;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }

    private String mapToTranslatedValue(String input) {
        var res = ecrLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", input);
        if (res != null && !res.getSampleValue().isEmpty()) {
            return res.getSampleValue();
        }
        else {
            return notFoundValue;
        }
    }

    private CdaTreatmentAdministrationMapper mapToTreatment(
            EcrSelectedTreatment input, POCDMT000040SubstanceAdministration output,
            StrucDocText list,
            int counter) throws XmlException, ParseException, EcrCdaXmlException {
        String PROV="";
        String ORG="";
        String treatmentUid="";
        String TRT_TREATMENT_DT="";
        String TRT_FREQUENCY_AMT_CD="";
        String TRT_DOSAGE_UNIT_CD="";
        String TRT_DURATION_AMT="";
        String TRT_DURATION_UNIT_CD="";

        String treatmentName ="";
        String treatmentNameQuestion ="";

        String subjectAreaTRT ="TREATMENT";
        String customTreatment="";


        for (Map.Entry<String, Object> entry : input.getMsgTreatment().getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals("trtTreatmentDt")  && value != null && input.getMsgTreatment().getTrtTreatmentDt() != null) {
                TRT_TREATMENT_DT= input.getMsgTreatment().getTrtTreatmentDt().toString();
            }

            if(name.equals("trtFrequencyAmtCd")  && value != null && input.getMsgTreatment().getTrtFrequencyAmtCd() != null && !input.getMsgTreatment().getTrtFrequencyAmtCd().isEmpty()) {
                TRT_FREQUENCY_AMT_CD= input.getMsgTreatment().getTrtFrequencyAmtCd();
            }

            if(name.equals("trtDosageUnitCd") && value != null && input.getMsgTreatment().getTrtDosageUnitCd() != null && !input.getMsgTreatment().getTrtDosageUnitCd().isEmpty()) {
                TRT_DOSAGE_UNIT_CD= input.getMsgTreatment().getTrtDosageUnitCd();
                output.getDoseQuantity().setUnit(TRT_DOSAGE_UNIT_CD);
            }

            if(name.equals("trtDosageAmt") && value != null && input.getMsgTreatment().getTrtDosageAmt() != null) {
                String dosageSt = input.getMsgTreatment().getTrtDosageAmt().toString();
                if(!dosageSt.isEmpty()) {
                    String dosageStQty = "";
                    String dosageStUnit = "";
                    String dosageStCodeSystemName = "";
                    String dosageStDisplayName = "";
                    // CHECK mapToTreatment
                    if (output.getDoseQuantity() == null) {
                        output.addNewDoseQuantity();
                    }
                    output.getDoseQuantity().setValue(input.getMsgTreatment().getTrtDosageAmt());
                }
            }

            if(name.equals("trtDrugCd") && value != null && input.getMsgTreatment().getTrtDrugCd() != null && !input.getMsgTreatment().getTrtDrugCd().isEmpty()) {
                treatmentNameQuestion = mapToQuestionId("TRT_DRUG_CD");;
                treatmentName = input.getMsgTreatment().getTrtDrugCd();
            }


            if(name.equals("trtLocalId")  && value != null&& input.getMsgTreatment().getTrtLocalId() != null && !input.getMsgTreatment().getTrtLocalId().isEmpty()) {
                int c = 0;
                if (output.getIdArray().length == 0) {
                    output.addNewId();
                }else {
                    c = output.getIdArray().length;
                    output.addNewId();
                }
                output.getIdArray(c).setRoot(idRoot);
                output.getIdArray(c).setAssigningAuthorityName("LR");
                output.getIdArray(c).setExtension(input.getMsgTreatment().getTrtLocalId());
                treatmentUid=input.getMsgTreatment().getTrtLocalId();
            }

            if(name.equals("trtCustomTreatmentTxt")  && value != null && input.getMsgTreatment().getTrtCustomTreatmentTxt() != null && !input.getMsgTreatment().getTrtCustomTreatmentTxt().isEmpty()) {
                customTreatment= input.getMsgTreatment().getTrtCustomTreatmentTxt();
            }

            if(name.equals("trtCompositeCd")  && value != null && input.getMsgTreatment().getTrtCompositeCd() != null && !input.getMsgTreatment().getTrtCompositeCd().isEmpty()) {

            }

            if(name.equals("trtCommentTxt")  && value != null && input.getMsgTreatment().getTrtCommentTxt() != null && !input.getMsgTreatment().getTrtCommentTxt().isEmpty()) {

            }

            if(name.equals("trtDurationAmt") && value != null && input.getMsgTreatment().getTrtDurationAmt() != null) {
                TRT_DURATION_AMT = input.getMsgTreatment().getTrtDurationAmt().toString();
            }

            if(name.equals("trtDurationUnitCd") && value != null && input.getMsgTreatment().getTrtDurationUnitCd() != null && !input.getMsgTreatment().getTrtDurationUnitCd().isEmpty()) {
                TRT_DURATION_UNIT_CD = input.getMsgTreatment().getTrtDurationUnitCd();
            }
        }


        if(!customTreatment.isEmpty()){
            // CHECK mapToTreatment
            int c = 0;
            if (list == null) {
                list = StrucDocText.Factory.newInstance();
                list.addNewList();
            } else {
                if ( list.getListArray().length == 0){
                    list.addNewList();
                } else {
                    c = list.getListArray().length;
                    list.addNewList();
                }
            }
            list.getListArray(c).addNewItem();
            list.getListArray(c).addNewCaption();

            StrucDocItem item = StrucDocItem.Factory.newInstance();

            XmlCursor cursor = item.newCursor();
            cursor.setTextValue("[CDATA]" + customTreatment + "[CDATA]");
            cursor.dispose();

            list.getListArray(c).setItemArray(0, item);

            list.getListArray(c).getCaption().set(mapToCData("CDA Treatment Information Section"));

        }else{
            // OutXML::Element element1= (OutXML::Element)list.item[counter];
        }

        if (!treatmentName.isEmpty()) {
            if  (output.getConsumable() == null) {
                output.addNewConsumable().addNewManufacturedProduct().addNewManufacturedLabeledDrug().addNewCode();
            }
            var ot = output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getCode();
            var ce = mapToCEAnswerType(
                    treatmentName,
                    treatmentNameQuestion
            );
            ot = ce;
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().setCode(ot);

        } else {
            if  (output.getConsumable() == null) {
                output.addNewConsumable().addNewManufacturedProduct().addNewManufacturedLabeledDrug().addNewCode();
                output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().addNewName();
            }
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getCode().setNullFlavor("OTH");
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getName().set(mapToCData(customTreatment));
        }

        if(!TRT_TREATMENT_DT.isEmpty()){
            // CHECK mapToTreatment
            if (output.getEffectiveTimeArray().length == 0) {
                output.addNewEffectiveTime();
            }
            var lowElement = output.getEffectiveTimeArray(0);

            XmlObject xmlOb = XmlObject.Factory.newInstance();
            XmlCursor cursor = xmlOb.newCursor();
            cursor.toEndDoc();  // Move to the root element

            cursor.beginElement("low");
            cursor.insertAttributeWithValue("value",  mapToTsType(TRT_TREATMENT_DT).getValue());

            if (TRT_DURATION_AMT != null && !TRT_DURATION_AMT.isEmpty() && TRT_DURATION_UNIT_CD != null && !TRT_DURATION_UNIT_CD.isEmpty()) {
                cursor.toEndDoc();
                cursor.beginElement("width");
                if (!TRT_DURATION_AMT.isEmpty()) {
                    cursor.insertAttributeWithValue(valueTest, TRT_DURATION_AMT);
                }

                if (!TRT_DURATION_UNIT_CD.isEmpty()) {
                    cursor.insertAttributeWithValue("unit", TRT_DURATION_UNIT_CD);
                }

            }

            cursor.dispose();
            lowElement.set(xmlOb);

            XmlCursor parentCursor = lowElement.newCursor();
            parentCursor.toFirstChild();
            parentCursor.insertAttributeWithValue("type", "IVL_TS");
            parentCursor.dispose();

            output.setEffectiveTimeArray(0, lowElement);
        }

        if (!TRT_FREQUENCY_AMT_CD.isEmpty()) {
            // CHECK mapToTreatment
            int c = 0;
            if (output.getEffectiveTimeArray().length == 0) {
                output.addNewEffectiveTime();
            } else {
                c = output.getEffectiveTimeArray().length;
                output.addNewEffectiveTime();

            }

            var element = output.getEffectiveTimeArray(c);

            XmlObject xmlOb = XmlObject.Factory.newInstance();

            XmlCursor cursor = xmlOb.newCursor();
            cursor.toEndDoc();  // Move to the root element
            cursor.beginElement("period");

            String hertz = TRT_FREQUENCY_AMT_CD;
            AttributeMapper res = mapToAttributes(hertz);
            if (cursor.toFirstAttribute()) {
                cursor.insertAttributeWithValue("value", res.getAttribute1());
            }
            if (cursor.toNextAttribute()) {
                cursor.insertAttributeWithValue("unit", res.getAttribute2());
            }
            cursor.dispose();

            element.set(xmlOb);
            XmlCursor parentCursor = element.newCursor();
            parentCursor.toFirstChild();

            parentCursor.insertAttributeWithValue("type", "PIVL_TS");
            parentCursor.dispose();

            output.setEffectiveTimeArray(c, element);
        }

        int org = 0;
        int provider= 0;
        int performerCounter=0;
        if (input.getMsgTreatmentOrganizations().size() > 0 ||  input.getMsgTreatmentProviders().size() > 0) {
            for(int i = 0; i < input.getMsgTreatmentOrganizations().size(); i++) {
                int c = 0;
                if (output.getParticipantArray().length == 0) {
                    output.addNewParticipant().addNewParticipantRole().addNewId();
                } else {
                    c = output.getParticipantArray().length;
                    output.addNewParticipant().addNewParticipantRole().addNewId();
                }
                var ot = output.getParticipantArray(c);
                var mappedVal = mapToORG( input.getMsgTreatmentOrganizations().get(i), ot);
                output.setParticipantArray(c, mappedVal);
                output.getParticipantArray(c).getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR_ORG");
                performerCounter++;
                org = 1;
            }

            for(int i = 0; i < input.getMsgTreatmentProviders().size(); i++) {
                int c = 0;
                if (output.getParticipantArray().length == 0) {
                    output.addNewParticipant().addNewParticipantRole().addNewId();
                } else {
                    c = output.getParticipantArray().length;
                    output.addNewParticipant().addNewParticipantRole().addNewId();
                }

                var ot = output.getParticipantArray(c);
                var mappedVal = mapToPSN(input.getMsgTreatmentProviders().get(i), ot);
                output.getParticipantArray(c).getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR_ORG");
                performerCounter++;
                provider = 1;
            }
        }

        CdaTreatmentAdministrationMapper mapper = new CdaTreatmentAdministrationMapper();
        mapper.setAdministration(output);
        mapper.setText(list);
        return mapper;
    }

    private AttributeMapper mapToAttributes(String input) {
        AttributeMapper model = new AttributeMapper();
        if (!input.isEmpty()) {
            if (input.equals("BID")) {
                model.setAttribute1("12");
                model.setAttribute2("h");
            } else if (input.equals("5ID")) {
                model.setAttribute1("4.5");
                model.setAttribute2("h");
            } else if (input.equals("TID")) {
                model.setAttribute1("8");
                model.setAttribute2("h");
            } else if (input.equals("QW")) {
                model.setAttribute1("1");
                model.setAttribute2("wk");
            } else if (input.equals("QID")) {
                model.setAttribute1("6");
                model.setAttribute2("h");
            } else if (input.equals("QD")) {
                model.setAttribute1("1");
                model.setAttribute2("d");
            } else if (input.equals("Q8H")) {
                model.setAttribute1("8");
                model.setAttribute2("h");
            } else if (input.equals("Q6H")) {
                model.setAttribute1("6");
                model.setAttribute2("h");
            } else if (input.equals("Q5D")) {
                model.setAttribute1("1.4");
                model.setAttribute2("d");
            } else if (input.equals("Q4H")) {
                model.setAttribute1("4");
                model.setAttribute2("h");
            } else if (input.equals("Q3D")) {
                model.setAttribute1("3.5");
                model.setAttribute2("d");
            } else if (input.equals("Once")) {
                model.setAttribute1("24");
                model.setAttribute2("h");
            } else if (input.equals("Q12H")) {
                model.setAttribute1("12");
                model.setAttribute2("h");
            }

        }
        return model;
    }

    private POCDMT000040Component3 mapToInterview(EcrSelectedInterview in, POCDMT000040Component3 out) throws XmlException, ParseException, EcrCdaXmlException {

        int repeatCounter=0;
        int sectionEntryCounter= out.getSection().getEntryArray().length;

        if (out.getSection().getEntryArray().length == 0) {
            out.getSection().addNewEntry().addNewEncounter().addNewCode();
            out.getSection().getEntryArray(0).getEncounter().addNewId();
        }
        // check existing scenario

        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().setClassCode("ENC");
        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().setMoodCode(XDocumentEncounterMood.EVN);
        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getCode().setCode("54520-2");
        out.getSection().getEntryArray(0).getEncounter().getIdArray(0).setRoot("LR");

        int entryCounter= 0;
        int outerEntryCounter= 1;
        String IXS_INTERVIEWER_ID="";
        String interviewer = "";

        if (in.getMsgInterview().getDataMap() == null || in.getMsgInterview().getDataMap().size() == 0) {
            in.getMsgInterview().initDataMap();
        }
        for (Map.Entry<String, Object> entry : in.getMsgInterview().getDataMap().entrySet()) {

            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if((name.equals("msgContainerUid") && in.getMsgInterview().getMsgContainerUid() != null )
                    || (name.equals("ixsAuthorId")  && in.getMsgInterview().getIxsAuthorId() != null)
                    || (name.equals("ixsEffectiveTime")  && in.getMsgInterview().getIxsEffectiveTime() != null)){
                // CHECK mapToInterview
                int c = 0;
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray().length == 0) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewId();
                } else {
                    c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray().length ;
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewId();
                }

                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray(c).setExtension(in.getMsgInterview().getMsgContainerUid().toString());
            }
            else if (name.equals("ixsLocalId")  && in.getMsgInterview().getIxsLocalId() != null && !in.getMsgInterview().getIxsLocalId().isEmpty()){
                int c = 0;
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray().length == 0) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewId();
                } else {
                    c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray().length ;
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewId();
                }
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray(c).setExtension(in.getMsgInterview().getIxsLocalId());
            }

            else if (name.equals("ixsStatusCd")  && in.getMsgInterview().getIxsStatusCd() != null && !in.getMsgInterview().getIxsStatusCd().isEmpty()){
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getStatusCode() == null) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewStatusCode();
                }
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getStatusCode().setCode(in.getMsgInterview().getIxsStatusCd());
            }
            else if (name.equals("ixsInterviewDt")  && in.getMsgInterview().getIxsInterviewDt() != null){
                var ts = mapToTsType(in.getMsgInterview().getIxsInterviewDt().toString());
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEffectiveTime() == null) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEffectiveTime();
                }
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEffectiveTime().setValue(ts.getValue().toString());
            }

            else if (name.equals("ixsIntervieweeRoleCd")  && in.getMsgInterview().getIxsIntervieweeRoleCd() != null && !in.getMsgInterview().getIxsIntervieweeRoleCd().isEmpty()){
                String questionCode = mapToQuestionId("IXS_INTERVIEWEE_ROLE_CD");

                int c = 0;
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length == 0) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                } else {
                    c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length;
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                }

                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).getObservation();
                mapToObservation(questionCode, in.getMsgInterview().getIxsIntervieweeRoleCd(), obs);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setObservation(obs);
                entryCounter= entryCounter+ 1;
            }
            else if (name.equals("ixsInterviewTypeCd")  && in.getMsgInterview().getIxsInterviewTypeCd() != null && !in.getMsgInterview().getIxsInterviewTypeCd().isEmpty()){
                String questionCode = mapToQuestionId("IXS_INTERVIEW_TYPE_CD");

                int c = 0;
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length == 0) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                } else {
                    c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length;
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                }

                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).getObservation();
                mapToObservation(questionCode, in.getMsgInterview().getIxsInterviewTypeCd(), obs);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setObservation(obs);
                entryCounter= entryCounter+ 1;

            }
            else if (name.equals("ixsInterviewLocCd")  && in.getMsgInterview().getIxsInterviewLocCd() != null && !in.getMsgInterview().getIxsInterviewLocCd().isEmpty()){
                String questionCode = mapToQuestionId("IXS_INTERVIEW_LOC_CD");

                int c = 0;
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length == 0) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                } else {
                    c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length;
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                }
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).getObservation();
                mapToObservation(questionCode, in.getMsgInterview().getIxsInterviewLocCd(), obs);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setObservation(obs);
                entryCounter= entryCounter+ 1;
            }
        }

        int questionGroupCounter=0;
        int componentCounter=0;
        int answerGroupCounter=0;
        String OldQuestionId=change;
        String OldRepeatQuestionId=change;
        int sectionCounter = 0;
        int repeatComponentCounter=0;
        int providerRoleCounter=0;


        if (!in.getMsgInterviewProviders().isEmpty() || !in.getMsgInterviewAnswers().isEmpty() || !in.getMsgInterviewAnswerRepeats().isEmpty()) {

            for(int i = 0; i < in.getMsgInterviewProviders().size(); i++) {
                if ( out.getSection().getEntryArray(sectionCounter).getEncounter().getParticipantArray().length == 0) {
                    out.getSection().getEntryArray(sectionCounter).getEncounter().addNewParticipant().addNewParticipantRole().addNewCode();
                } else {
                    providerRoleCounter = out.getSection().getEntryArray(sectionCounter).getEncounter().getParticipantArray().length;
                    out.getSection().getEntryArray(sectionCounter).getEncounter().addNewParticipant().addNewParticipantRole().addNewCode();
                }

                var element = out.getSection().getEntryArray(sectionCounter).getEncounter().getParticipantArray(providerRoleCounter);

                POCDMT000040Participant2 ot = mapToPSN(in.getMsgInterviewProviders().get(i), element);

                out.getSection().getEntryArray(sectionCounter).getEncounter().setParticipantArray(providerRoleCounter, ot);
                var element2 = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getParticipantArray(providerRoleCounter)
                        .getParticipantRole().getCode();
                CE ce = mapToCEQuestionType("IXS102", element2);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getParticipantArray(providerRoleCounter)
                        .getParticipantRole().setCode(ce);
                providerRoleCounter=providerRoleCounter+1;
            }
            for(int i = 0; i < in.getMsgInterviewAnswers().size(); i++) {
                String newQuestionId="";
                var element = out.getSection().getEntryArray(sectionCounter).getEncounter();
                var ot = mapToInterviewObservation(in.getMsgInterviewAnswers().get(i), entryCounter, OldQuestionId,
                        element );

                entryCounter = ot.getCounter();
                OldQuestionId = ot.getQuestionSeq();
                out.getSection().getEntryArray(sectionCounter).setEncounter(ot.getComponent());

                if(newQuestionId.equals(OldQuestionId)){
                }
                else{
                    OldQuestionId=newQuestionId;
                }
            }
            for(int i = 0; i < in.getMsgInterviewAnswerRepeats().size(); i++) {
                var element = out.getSection().getEntryArray(sectionEntryCounter).getEncounter();
                var mapped = mapToInterviewMultiSelectObservation(in.getMsgInterviewAnswerRepeats().get(i),
                        answerGroupCounter,
                        questionGroupCounter,
                        sectionCounter,
                        OldRepeatQuestionId,
                        element);

                answerGroupCounter = mapped.getAnswerGroupCounter();
                questionGroupCounter = mapped.getQuestionGroupCounter();
                sectionCounter = mapped.getSectionCounter();
                OldRepeatQuestionId = mapped.getQuestionId();
                out.getSection().getEntryArray(sectionEntryCounter).setEncounter(mapped.getComponent());

            }
        }


        return out;
    }

    private InterviewAnswerMultiMapper mapToInterviewMultiSelectObservation(EcrMsgInterviewAnswerRepeatDto in,
                                                                            Integer answerGroupCounter,
                                                                            Integer questionGroupCounter,
                                                                            Integer sectionCounter,
                                                                            String questionId,
                                                                            POCDMT000040Encounter out) throws ParseException {
        int componentCounter = 0;
        String dataType="DATE";
        int seqNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;
        String questionIdentifier="";

        InterviewAnswerMultiMapper model = new InterviewAnswerMultiMapper();

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals(colQuestionGroupSeqNbr) && !in.getQuestionGroupSeqNbr().isEmpty()){
                questionGroupSeqNbr= Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if(name.equals(colAnswerGroupSeqNbr) && !in.getAnswerGroupSeqNbr().isEmpty()){
                answerGroupSeqNbr= Integer.valueOf(in.getAnswerGroupSeqNbr());
                if((answerGroupSeqNbr==answerGroupCounter) &&
                        (questionGroupSeqNbr ==questionGroupCounter))
                {
                    componentCounter = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray().length;
                }
                else
                {
                    sectionCounter = out.getEntryRelationshipArray().length - 1;
                    questionGroupCounter=questionGroupSeqNbr ;
                    answerGroupCounter=answerGroupSeqNbr;

                    if (out.getEntryRelationshipArray(sectionCounter).getOrganizer() == null) {
                        out.getEntryRelationshipArray(sectionCounter).addNewOrganizer();
                    }


                    if (out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode() == null) {
                        out.getEntryRelationshipArray(sectionCounter).getOrganizer().addNewCode();
                    }

                    if (out.getEntryRelationshipArray(sectionCounter).getOrganizer().getStatusCode() == null) {
                        out.getEntryRelationshipArray(sectionCounter).getOrganizer().addNewStatusCode();
                    }

                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCode(String.valueOf(questionGroupSeqNbr));
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCode("1234567RPT");
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCodeSystem(clinicalCodeSystem);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCodeSystemName(clinicalCodeSystemName);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setDisplayName("Generic Repeating Questions Section");

                    out.getEntryRelationshipArray(sectionCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().setMoodCode("EVN");
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getStatusCode().setCode("completed");
                    componentCounter=0;

                }
            }

            else if(name.equals(colDataType) && !in.getDataType().isEmpty() ){
                dataType= in.getDataType();
            }else if(name.equals(colSeqNbr) && !in.getSeqNbr().isEmpty()){
                seqNbr= Integer.valueOf(in.getSeqNbr()) ;
            }

            if(dataType.equalsIgnoreCase(dataTypeCode) || dataType.equalsIgnoreCase("CODED_COUNTY")){
                CE ce = CE.Factory.newInstance();
                if (name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals(colAnsCodeSystemCd) && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals(colAnsCodeSystemDescTxt) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals(colAnsDisplayTxt) && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals(colAnsToCode) && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals(colAnsToCodeSystemCd) && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals(colAnsToCodeSystemDescTxt) && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals(colAnsToDisplayNm) && !in.getAnsToDisplayNm().isEmpty()) {
                    ce.setDisplayName(in.getAnsToDisplayNm());

                }
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(seqNbr).set(ce);
            }
            else if ((dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase(dataTypeNumeric)) &&
                    name.equals(colAnswerTxt)){
                if(questionIdentifier.equalsIgnoreCase("NBS243") ||
                        questionIdentifier.equalsIgnoreCase("NBS290")) {

                    var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
                    var ot = mapToObservationPlace(value,
                            element);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation) ot);

                }
                else {
                    var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();

                    var ot = mapToSTValue(value,element);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation)ot);
                }
            }
            else if(dataType.equalsIgnoreCase("DATE")){
                if(name.equals(colAnswerTxt)){
                    if (out.getEntryRelationshipArray(sectionCounter).getOrganizer() == null) {
                        out.getEntryRelationshipArray(sectionCounter).addNewOrganizer().addNewComponent().addNewObservation().addNewValue();
                    }
                    var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(0);
                    XmlCursor cursor = element.newCursor();
                    cursor.toFirstChild();
                    cursor.setAttributeText(new QName(nameSpaceUrl, "type"), "TS");
                    cursor.setAttributeText(new QName("", valueTest), null);
                    if (name.equals(colAnswerTxt)) {
                        String newValue = mapToTsType(in.getAnswerTxt()).toString();
                        cursor.setAttributeText(new QName("", valueTest), newValue);
                    }
                    cursor.dispose();
                }
            }


            if(name.equals(colQuestionIdentifier)){
                questionIdentifier= value;
                if(value.equals(questionId)){
                    //Test
                }else{
                    if(questionId.equals(change)){

                    }else{
                        sectionCounter =  sectionCounter+1;
                    }

                    questionId =value;
                }

                if (out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode() == null ) {
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
                }

                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setClassCode("OBS");
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCode(value);
            }
            else if(name.equals(colQuesCodeSystemCd)){
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCodeSystem(value);
            }
            else if(name.equals(colQuesCodeSystemDescTxt)){
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCodeSystemName(value);
            }
            else if(name.equals(colQuesDisplayTxt)){
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setDisplayName(value);
            }
        }

        model.setAnswerGroupCounter(answerGroupCounter);
        model.setQuestionGroupCounter(questionGroupCounter);
        model.setSectionCounter(sectionCounter);
        model.setQuestionId(questionId);
        model.setComponent(out);

        return model;
    }

    private InterviewAnswerMapper mapToInterviewObservation(EcrMsgInterviewAnswerDto in, int counter,
                                                            String questionSeq,
                                                            POCDMT000040Encounter out) throws XmlException, ParseException {
        String dataType="";
        int sequenceNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;

        InterviewAnswerMapper model = new InterviewAnswerMapper();

        var sizeArr = out.getEntryRelationshipArray().length;

        if (sizeArr > 0 && sizeArr == counter) {
            out.addNewEntryRelationship().addNewObservation();
            out.getEntryRelationshipArray(counter).getObservation().addNewCode();
            out.getEntryRelationshipArray(counter).getObservation().addNewStatusCode();
        }

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals(colQuestionGroupSeqNbr) && !in.getQuestionGroupSeqNbr().isEmpty()){
                questionGroupSeqNbr= Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if(name.equals(colAnswerGroupSeqNbr) && !in.getAnswerGroupSeqNbr().isEmpty() ){
                answerGroupSeqNbr= Integer.valueOf(in.getAnswerGroupSeqNbr());
            }
            else if(name.equals(colDataType) && !in.getDataType().isEmpty()){
                dataType=in.getDataType();
            }
            else if(name.equals(colSeqNbr) && !in.getSeqNbr().isEmpty()){
                sequenceNbr= Integer.valueOf(in.getSeqNbr());
                if(sequenceNbr>0) {
                    sequenceNbr =sequenceNbr-1;
                }
            }

            if(dataType.equalsIgnoreCase(dataTypeCode) || dataType.equalsIgnoreCase(county)){
                CE ce = CE.Factory.newInstance();
                if (name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals(colAnsCodeSystemCd) && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals(colAnsCodeSystemDescTxt) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals(colAnsDisplayTxt) && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals(colAnsToCode) && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals(colAnsToCodeSystemCd) && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals(colAnsToCodeSystemDescTxt) && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals(colAnsToDisplayNm) && !in.getAnsToDisplayNm().isEmpty()) {
                    ce.setDisplayName(in.getAnsToDisplayNm());

                }
                out.getEntryRelationshipArray(counter).getObservation().getValueArray(sequenceNbr).set(ce);

            }
            else if(
                    dataType.equals("TEXT") ||
                    dataType.equals(dataTypeNumeric)){
                if(name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()){
                    var element = out.getEntryRelationshipArray(counter).getObservation();
                    var ot = mapToSTValue(value, element);
                    out.getEntryRelationshipArray(counter).setObservation((POCDMT000040Observation) ot);
                }

            }
            else if(dataType.equalsIgnoreCase(  "DATE")){
                if(name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()){
                    var element = out.getEntryRelationshipArray(counter).getObservation().getValueArray(0);
                    XmlCursor cursor = element.newCursor();
                    cursor.setAttributeText(new QName(nameSpaceUrl, "type"), "TS");
                    cursor.setAttributeText(new QName("name"), valueTest);  // This is an assumption based on the original code

                    if(name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()){
                        String newValue = mapToTsType(in.getAnswerTxt()).toString();
                        cursor.setAttributeText(new QName("name"), valueTest);
                        cursor.setTextValue(newValue);
                    }
                    else {
                        element = out.getEntryRelationshipArray(counter).getObservation().getValueArray(0);
                        cursor = element.newCursor();
                        cursor.setAttributeText(new QName(nameSpaceUrl, "type"), "ST");

                        if(name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()) {
                            cursor.setTextValue("[CDATA]" + in.getAnswerTxt() + "[CDATA]");
                        }
                    }

                    out.getEntryRelationshipArray(counter).getObservation().setValueArray(0, element);
                    cursor.dispose();
                }
            }
            if(name.equals(colQuestionIdentifier) && !in.getQuestionIdentifier().isEmpty()){
                if(in.getQuestionIdentifier().equals(value)){
                    //Test
                }else{
                    if(questionSeq.equals(change)){

                    }else{
                        counter =  counter+1;
                    }

                    questionSeq =value;

                    out.getEntryRelationshipArray(counter).getObservation().setClassCode("OBS");
                    out.getEntryRelationshipArray(counter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
                    out.getEntryRelationshipArray(counter).getObservation().getCode().setCode(in.getQuestionIdentifier());
                }
            }
            else if(name.equals(colQuesCodeSystemCd) && !in.getQuesCodeSystemCd().isEmpty()){
                out.getEntryRelationshipArray(counter).getObservation().getCode().setCodeSystem(in.getQuesCodeSystemCd());
            }
            else if(name.equals(colQuesCodeSystemDescTxt) && !in.getQuesCodeSystemDescTxt().isEmpty()){
                out.getEntryRelationshipArray(counter).getObservation().getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());
            }
            else if(name.equals(colQuesDisplayTxt) && !in.getQuesDisplayTxt().isEmpty()){
                out.getEntryRelationshipArray(counter).getObservation().getCode().setDisplayName(in.getQuesDisplayTxt());
            }

        }

        model.setComponent(out);
        model.setCounter(counter);
        model.setQuestionSeq(questionSeq);
        return model;
    }



    private CE mapToCEQuestionType(String questionCode, CE output) {
        var ot = mapToCodedQuestionType(questionCode);
        output.setCodeSystem(ot.getQuesCodeSystemCd());
        output.setCodeSystemName(ot.getQuesCodeSystemDescTxt());
        output.setDisplayName(ot.getQuesDisplayName());
        output.setCode(questionCode);

        return output;
    }

    private POCDMT000040Participant2 mapToPlace(EcrMsgPlaceDto in, POCDMT000040Participant2 out) throws XmlException, EcrCdaXmlException {
        String state="";
        String streetAddress1="";
        String streetAddress2="";
        String city = "";
        String county = "";
        String country = "";
        String zip = "";
        String workPhone= "";
        String workExtn = "";
        String workURL = "";
        String workEmail = "";
        String workCountryCode="";
        String placeComments="";
        String placeAddressComments="";
        int teleCounter=0;
        String teleAsOfDate="";
        String postalAsOfDate="";
        String censusTract="";

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals("plaLocalId") && value != null && !in.getPlaLocalId().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }

                out.setTypeCode("PRF");
                out.getParticipantRole().getIdArray(0).setRoot(idArrRoot);
                out.getParticipantRole().getIdArray(0).setExtension(in.getPlaLocalId());
                out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
            }
            if (name.equals("plaNameTxt") && value != null && !in.getPlaNameTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewPlayingEntity().addNewName();
                } else {
                    out.getParticipantRole().addNewPlayingEntity().addNewName();
                }

                PN val = PN.Factory.newInstance();

                XmlCursor cursor = val.newCursor();
                cursor.setTextValue("[CDATA]" + in.getPlaNameTxt() + "[CDATA]");
                cursor.dispose();

                out.getParticipantRole().getPlayingEntity().addNewName();
                out.getParticipantRole().getPlayingEntity().setNameArray(0, val);
            }
            if (name.equals("plaAddrStreetAddr1Txt")&& value != null && !in.getPlaAddrStreetAddr1Txt().isEmpty()){
                streetAddress1= in.getPlaAddrStreetAddr1Txt();
            }
            if (name.equals("plaAddrStreetAddr2Txt")&& value != null && !in.getPlaAddrStreetAddr2Txt().isEmpty()){
                streetAddress2 =in.getPlaAddrStreetAddr2Txt();
            }
            if (name.equals("plaAddrCityTxt")&& value != null && !in.getPlaAddrCityTxt().isEmpty()){
                city= in.getPlaAddrCityTxt();
            }
            if (name.equals("plaAddrCountyCd")&& value != null && !in.getPlaAddrCountyCd().isEmpty()){
                county= in.getPlaAddrCountyCd();
            }
            if (name.equals("plaAddrStateCd")&& value != null && !in.getPlaAddrStateCd().isEmpty()){
                state= in.getPlaAddrStateCd();
            }
            if (name.equals("plaAddrZipCodeTxt")&& value != null && !in.getPlaAddrZipCodeTxt().isEmpty()){
                zip = in.getPlaAddrZipCodeTxt();
            }
            if (name.equals("plaAddrCountryCd")&& value != null && !in.getPlaAddrCountryCd().isEmpty()){
                country=in.getPlaAddrCountryCd();
            }
            if (name.equals("plaPhoneNbrTxt") && value != null&& !in.getPlaPhoneNbrTxt().isEmpty()){
                workPhone=in.getPlaPhoneNbrTxt();
            }
            if (name.equals("plaAddrAsOfDt") && value != null&& in.getPlaAddrAsOfDt() != null){
                postalAsOfDate=in.getPlaAddrAsOfDt().toString();
            }
            if (name.equals("plaCensusTractTxt") && value != null&& !in.getPlaCensusTractTxt().isEmpty()){
                censusTract=in.getPlaCensusTractTxt();
            }
            if (name.equals("plaPhoneAsOfDt") && value != null&& in.getPlaPhoneAsOfDt() != null ){
                teleAsOfDate=in.getPlaPhoneAsOfDt().toString();
            }
            if (name.equals("plaPhoneExtensionTxt") && value != null&& !in.getPlaPhoneExtensionTxt().isEmpty()){
                workExtn= in.getPlaPhoneExtensionTxt();
            }
            if (name.equals("plaCommentTxt") && value != null&& !in.getPlaCommentTxt().isEmpty()){
                placeAddressComments= in.getPlaCommentTxt();
            }
            if (name.equals("plaPhoneCountryCodeTxt") && value != null&& !in.getPlaPhoneCountryCodeTxt().isEmpty()){
                workCountryCode= in.getPlaPhoneCountryCodeTxt();
            }
            if (name.equals("plaEmailAddressTxt") && value != null&& !in.getPlaEmailAddressTxt().isEmpty()){
                workEmail= in.getPlaEmailAddressTxt();
            }
            if (name.equals("plaUrlAddressTxt") && value != null&& !in.getPlaUrlAddressTxt().isEmpty()){
                workURL= in.getPlaUrlAddressTxt();
            }
            if (name.equals("plaPhoneCommentTxt") && value != null&& !in.getPlaPhoneCommentTxt().isEmpty()){
                placeComments= in.getPlaPhoneCommentTxt();
            }
            if (name.equals("plaTypeCd") && value != null&& !in.getPlaTypeCd().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewCode();
                } else {
                    out.getParticipantRole().addNewCode();
                }

                String questionCode= mapToQuestionId("PLA_TYPE_CD");
                out.getParticipantRole().addNewCode();
                out.getParticipantRole().setCode(mapToCEAnswerType(in.getPlaTypeCd(), questionCode));
            }
            if (name.equals("plaCommentTxt") && value != null&& !in.getPlaCommentTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewPlayingEntity().addNewDesc();
                } else {
                    out.getParticipantRole().addNewPlayingEntity().addNewDesc();
                }

                out.getParticipantRole().getPlayingEntity().getDesc().set(mapToCData(in.getPlaCommentTxt()));
            }

            if (name.equals("plaIdQuickCode") && value != null&& !in.getPlaIdQuickCode().isEmpty()){

                int c = 0;
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    if (out.getParticipantRole().getIdArray().length > 0) {
                        c = out.getParticipantRole().getIdArray().length;
                    }
                    out.addNewParticipantRole().addNewId();
                }


                // Index is 1 in original code
                out.getParticipantRole().getIdArray(c).setRoot(idArrRoot);
                out.getParticipantRole().getIdArray(c).setExtension(in.getPlaIdQuickCode());
                out.getParticipantRole().getIdArray(c).setAssigningAuthorityName("LR_QEC");
            }

        }



        int isAddressPopulated= 0;
        if(!streetAddress1.isEmpty() ){
            AdxpStreetAddressLine val = AdxpStreetAddressLine.Factory.newInstance();
            XmlCursor cursor = val.newCursor();
            cursor.setTextValue("[CDATA]" + streetAddress1 + "[CDATA]");
            cursor.dispose();

            int c = 0;
            if (out.getParticipantRole().getAddrArray().length == 0) {
                out.getParticipantRole().addNewAddr().addNewStreetAddressLine();
            } else {
                c = out.getParticipantRole().getAddrArray().length;
                out.getParticipantRole().addNewAddr().addNewStreetAddressLine();
            }

            out.getParticipantRole().getAddrArray(c).setStreetAddressLineArray(0, val);
            isAddressPopulated=1;
        }
        if(!streetAddress2.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            if (out.getParticipantRole().getAddrArray().length > 1) {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1,  AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(1).set(mapToCData(streetAddress2));
            }
            else {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0,  AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(mapToCData(streetAddress2));
            }
            isAddressPopulated=1;
        }
        if(!city.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewCity();
            out.getParticipantRole().getAddrArray(0).setCityArray(0,  AdxpCity.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCityArray(0).set(mapToCData(city));

            isAddressPopulated=1;
        }
        if(!state.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewState();
            out.getParticipantRole().getAddrArray(0).setStateArray(0,  AdxpState.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(mapToCData(state  ));
            isAddressPopulated=1;
        }
        if(!county.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewCounty();

            out.getParticipantRole().getAddrArray(0).setCountyArray(0,  AdxpCounty.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(mapToCData(county));
            isAddressPopulated=1;
        }
        if(!zip.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewPostalCode();

            out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0,  AdxpPostalCode.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(mapToCData(zip   ));
            isAddressPopulated=1;
        }
        if(!country.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            AdxpCountry val = AdxpCountry.Factory.newInstance();
            val.set(mapToCData(country));
            out.getParticipantRole().getAddrArray(0).addNewCountry();

            out.getParticipantRole().getAddrArray(0).setCountryArray(0,  AdxpCountry.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(mapToCData(country));
            isAddressPopulated=1;
        }
        if(!censusTract.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewCensusTract();

            out.getParticipantRole().getAddrArray(0).setCensusTractArray(0,  AdxpCensusTract.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCensusTractArray(0).set(mapToCData(censusTract));
        }
        if(isAddressPopulated>0){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray()[0].setUse(Arrays.asList("WP"));
            if(!postalAsOfDate.isEmpty()){
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().addr[0];
                // mapToUsableTSElement(postalAsOfDate, element, useablePeriod);
                // CHECK mapToUsableTSElement
            }
        }
        if(!placeAddressComments.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewAdditionalLocator();

            out.getParticipantRole().getAddrArray(0).setAdditionalLocatorArray(0,  AdxpAdditionalLocator.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getAdditionalLocatorArray(0).set(mapToCData(placeAddressComments));
        }

        if(!workPhone.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(Arrays.asList("WP"));
            //workCountryCode
            int countryphoneCodeSize= workCountryCode.length();
            if(countryphoneCodeSize>0){
                workPhone = workCountryCode+"-"+ workPhone;
            }

            int phoneExtnSize = workExtn.length();
            if(phoneExtnSize>0){
                workPhone=workPhone+ extnStr+ workExtn;
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(workPhone);

            if(!teleAsOfDate.isEmpty()){
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().telecom[teleCounter];
                // mapToUsableTSElement(teleAsOfDate, element, useablePeriod);
                // CHECK mapToUsableTSElement
            }
            teleCounter = teleCounter+1;
        }
        if(!workEmail.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(Arrays.asList("WP"));
            out.getParticipantRole().getTelecomArray(teleCounter).setValue("mailto:"+workEmail);
            if(!teleAsOfDate.isEmpty()){
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().telecom[teleCounter];
                // mapToUsableTSElement(teleAsOfDate, element, useablePeriod);
                // CHECK mapToUsableTSElement
            }
            teleCounter = teleCounter +1;
        }
        if(!workURL.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(Arrays.asList("WP"));
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(workURL);
            if(!teleAsOfDate.isEmpty()){
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().telecom[teleCounter];
                // mapToUsableTSElement(teleAsOfDate, element, useablePeriod);
                // CHECK mapToUsableTSElement
            }
            teleCounter=teleCounter+1;
        }
        return out;
    }

    private POCDMT000040Participant2 mapToORG(EcrMsgOrganizationDto in, POCDMT000040Participant2 out) throws XmlException, EcrCdaXmlException {
        String state="";
        String streetAddress1="";
        String streetAddress2="";
        String city = "";
        String county = "";
        String country = "";
        String zip = "";
        String phone= "";
        String extn = "";

        out.setTypeCode("PRF");

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals("orgLocalId") && in.getOrgLocalId()!=null && !in.getOrgLocalId().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }
                out.getParticipantRole().getIdArray(0).setRoot(idArrRoot);
                out.getParticipantRole().getIdArray(0).setExtension(in.getOrgLocalId());
                out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
            }
            else if(name.equals("orgNameTxt") && in.getOrgNameTxt() != null && !in.getOrgNameTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewPlayingEntity();
                } else {
                    out.getParticipantRole().addNewPlayingEntity();
                }
                var val = mapToCData(in.getOrgNameTxt());
                out.getParticipantRole().getPlayingEntity().addNewName();
                out.getParticipantRole().getPlayingEntity().setNameArray(0,  PN.Factory.newInstance());
                out.getParticipantRole().getPlayingEntity().getNameArray(0).set(val);
            }
            else if(name.equals("orgAddrStreetAddr1Txt") && in.getOrgAddrStreetAddr1Txt() != null && !in.getOrgAddrStreetAddr1Txt().isEmpty()){
                streetAddress1= in.getOrgAddrStreetAddr1Txt();
            }
            else if(name.equals("orgAddrStreetAddr2Txt") && in.getOrgAddrStreetAddr2Txt() != null && !in.getOrgAddrStreetAddr2Txt().isEmpty()){
                streetAddress2 =in.getOrgAddrStreetAddr2Txt();
            }
            else if(name.equals("orgAddrCityTxt") && in.getOrgAddrCityTxt() !=null && !in.getOrgAddrCityTxt().isEmpty()){
                city= in.getOrgAddrCityTxt();
            }
            else if(name.equals("orgAddrCountyCd") && in.getOrgAddrCountyCd() != null && !in.getOrgAddrCountyCd().isEmpty()){
                county = mapToAddressType( in.getOrgAddrCountyCd(), county);
            }
            else if (name.equals("orgAddrStateCd") && in.getOrgAddrStateCd() != null &&  !in.getOrgAddrStateCd().isEmpty()){
                state= mapToAddressType( in.getOrgAddrStateCd(), state);
            }
            else if(name.equals("orgAddrZipCodeTxt") && in.getOrgAddrZipCodeTxt() != null && !in.getOrgAddrZipCodeTxt().isEmpty()){
                zip = in.getOrgAddrZipCodeTxt();
            }
            else if(name.equals("orgAddrCountryCd") && in.getOrgAddrCountryCd() != null && !in.getOrgAddrCountryCd().isEmpty()){
                country = mapToAddressType( in.getOrgAddrCountryCd(), country);
            }
            else if(name.equals("orgPhoneNbrTxt") && in.getOrgPhoneNbrTxt() != null && !in.getOrgPhoneNbrTxt().isEmpty()){
                phone=in.getOrgPhoneNbrTxt();
            }
            else if (name.equals("orgPhoneExtensionTxt") && in.getOrgPhoneExtensionTxt() != null)
            {
                extn= in.getOrgPhoneExtensionTxt().toString();
            }
            else if(name.equals("orgIdCliaNbrTxt") && in.getOrgIdCliaNbrTxt() != null && !in.getOrgIdCliaNbrTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }

                out.getParticipantRole().getIdArray(1).setRoot(idArrRoot);
                out.getParticipantRole().getIdArray(1).setExtension(in.getOrgIdCliaNbrTxt());
                out.getParticipantRole().getIdArray(1).setAssigningAuthorityName("LR_CLIA");
            }
        }



        int isAddressPopulated= 0;
        if(!streetAddress1.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, AdxpStreetAddressLine.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(mapToCData(streetAddress1));

            isAddressPopulated=1;
        }
        if(!streetAddress2.isEmpty() ){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            if (out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray().length > 1) {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1, AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(1).set(mapToCData(streetAddress2));
            }
            else {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(mapToCData(streetAddress2));
            }

            isAddressPopulated=1;
        }
        if(!city.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewCity();
            out.getParticipantRole().getAddrArray(0).setCityArray(0, AdxpCity.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCityArray(0).set(mapToCData(city));

            isAddressPopulated=1;
        }
        if(!state.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewState();

            out.getParticipantRole().getAddrArray(0).setStateArray(0, AdxpState.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStateArray(0).set(mapToCData(state));

            isAddressPopulated=1;
        }
        if(!county.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewCounty();

            out.getParticipantRole().getAddrArray(0).setCountyArray(0, AdxpCounty.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(mapToCData(county));

            isAddressPopulated=1;
        }
        if(!zip.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewPostalCode();

            out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0, AdxpPostalCode.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(mapToCData(zip));
            isAddressPopulated=1;
        }
        if(!country.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewCountry();

            out.getParticipantRole().getAddrArray(0).setCountryArray(0, AdxpCountry.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(mapToCData(zip));

            isAddressPopulated=1;
        }
        if(isAddressPopulated>0) {
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).setUse(new ArrayList(Arrays.asList("WP")));
        }



        if(!phone.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(0).setUse(new ArrayList(Arrays.asList("WP")));
            int phoneExtnSize = extn.length();
            if(phoneExtnSize>0){
                phone=phone+ extnStr+ extn;
            }
            out.getParticipantRole().getTelecomArray(0).setValue(phone);

        }
        return out;
    }

    private POCDMT000040Participant2 mapToPSN(EcrMsgProviderDto in, POCDMT000040Participant2 out) throws XmlException, EcrCdaXmlException {
        String firstName="";
        String lastName="";
        String suffix="";
        String degree="";
        String address1="";
        String address2="";
        String city="";
        String county="";
        String state="";
        String zip="";
        String country="";
        String telephone="";
        String extn="";
        String qec="";
        String email="";
        String prefix="";
        int teleCounter=0;

        out.setTypeCode("PRF");

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if (name.equals("prvLocalId") && in.getPrvLocalId() != null && !in.getPrvLocalId().isEmpty()) {
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }
                out.getParticipantRole().getIdArray(0).setExtension(in.getPrvLocalId());
                out.getParticipantRole().getIdArray(0).setRoot("2.16.840.1.113883.11.19745");
                out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
            }
            else if (name.equals("prvNameFirstTxt") && in.getPrvNameFirstTxt() !=null && !in.getPrvNameFirstTxt().isEmpty()) {
                firstName = in.getPrvNameFirstTxt();
            }
            else if (name.equals("prvNamePrefixCd") && in.getPrvNamePrefixCd() != null && !in.getPrvNamePrefixCd().isEmpty()) {
                prefix = in.getPrvNamePrefixCd();
            }
            else if (name.equals("prvNameLastTxt") && in.getPrvNameLastTxt() != null && !in.getPrvNameLastTxt().isEmpty()) {
                lastName = in.getPrvNameLastTxt();
            }
            else if(name.equals("prvNameSuffixCd") && in.getPrvNameSuffixCd() != null && !in.getPrvNameSuffixCd().isEmpty()) {
                suffix = in.getPrvNameSuffixCd();
            }
            else if(name.equals("prvNameDegreeCd") && in.getPrvNameDegreeCd()!=null && !in.getPrvNameDegreeCd().isEmpty()) {
                degree = in.getPrvNameDegreeCd();
            }
            else if(name.equals("prvAddrStreetAddr1Txt") && in.getPrvAddrStreetAddr1Txt() !=null && !in.getPrvAddrStreetAddr1Txt().isEmpty()) {
                address1 = in.getPrvAddrStreetAddr1Txt();
            }
            else if(name.equals("prvAddrStreetAddr2Txt") && in.getPrvAddrStreetAddr2Txt() != null && !in.getPrvAddrStreetAddr2Txt().isEmpty()) {
                address2 = in.getPrvAddrStreetAddr2Txt();
            }
            else if(name.equals("prvAddrCityTxt") && in.getPrvAddrCityTxt() != null && !in.getPrvAddrCityTxt().isEmpty()) {
                city = in.getPrvAddrCityTxt();
            }
            if(name.equals("prvAddrCountyCd") && in.getPrvAddrCountyCd() != null && !in.getPrvAddrCountyCd().isEmpty()) {
                county = mapToAddressType(in.getPrvAddrCountyCd(), county);
            }
            else if(name.equals("prvAddrStateCd") && in.getPrvAddrStateCd() != null  && !in.getPrvAddrStateCd().isEmpty()) {
                state = mapToAddressType(in.getPrvAddrStateCd(), state);
            }
            else if(name.equals("prvAddrZipCodeTxt") && in.getPrvAddrZipCodeTxt() != null && !in.getPrvAddrZipCodeTxt().isEmpty()) {
                zip = in.getPrvAddrZipCodeTxt();
            }
            else if(name.equals("prvAddrCountryCd") && in.getPrvAddrCountryCd() != null && !in.getPrvAddrCountryCd().isEmpty()) {
                country = mapToAddressType(in.getPrvAddrCountryCd(), country);
            }
            else if(name.equals("prvPhoneNbrTxt") && in.getPrvPhoneNbrTxt() != null && !in.getPrvPhoneNbrTxt().isEmpty()) {
                telephone = in.getPrvPhoneNbrTxt();
            }
            else  if(name.equals("prvPhoneExtensionTxt") && in.getPrvPhoneExtensionTxt() != null) {
                extn = in.getPrvPhoneExtensionTxt().toString();
            }
            else if(name.equals("prvIdQuickCodeTxt") && in.getPrvIdQuickCodeTxt() != null && !in.getPrvIdQuickCodeTxt().isEmpty()) {
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }


                int c = 0;
                if (out.getParticipantRole().getIdArray().length == 0) {
                    out.getParticipantRole().addNewId();
                } else {
                    c = out.getParticipantRole().getIdArray().length;
                    out.getParticipantRole().addNewId();
                }

                out.getParticipantRole().getIdArray(c).setExtension(in.getPrvIdQuickCodeTxt());
                out.getParticipantRole().getIdArray(c).setRoot("2.16.840.1.113883.11.19745");
                out.getParticipantRole().getIdArray(c).setAssigningAuthorityName("LR_QEC");
            }
            else if(name.equals("prvEmailAddressTxt") && in.getPrvEmailAddressTxt() != null && !in.getPrvEmailAddressTxt().isEmpty()) {
                email = in.getPrvEmailAddressTxt();
            }

        }



        if(!firstName.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewName();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewName();
            }
            var mapVal = mapToCData(firstName);
            EnGiven enG = EnGiven.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewGiven();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setGivenArray(0,  EnGiven.Factory.newInstance());
            out.getParticipantRole().getPlayingEntity().getNameArray(0).getGivenArray(0).set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setUse(new ArrayList(Arrays.asList("L")));
        }
        if(!lastName.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewName();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewName();
            }
            var mapVal = mapToCData(lastName);
            EnFamily enG = EnFamily.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewFamily();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setFamilyArray(0,  EnFamily.Factory.newInstance());
            out.getParticipantRole().getPlayingEntity().getNameArray(0).getFamilyArray(0).set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setUse(new ArrayList(Arrays.asList("L")));
        }
        if(!prefix.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewName();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewName();
            }
            var mapVal = mapToCData(prefix);
            EnPrefix enG = EnPrefix.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewPrefix();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setPrefixArray(0,  EnPrefix.Factory.newInstance());
            out.getParticipantRole().getPlayingEntity().getNameArray(0).getPrefixArray(0).set(mapVal);
        }
        if(!suffix.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewName();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewName();
            }
            var mapVal = mapToCData(suffix);
            EnSuffix enG = EnSuffix.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewSuffix();

            out.getParticipantRole().getPlayingEntity().getNameArray(0).setSuffixArray(0,  EnSuffix.Factory.newInstance());
            out.getParticipantRole().getPlayingEntity().getNameArray(0).getSuffixArray(0).set(mapVal);
        }
        if(!address1.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = mapToCData(address1);
            AdxpStreetAddressLine enG = AdxpStreetAddressLine.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0,  AdxpStreetAddressLine.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(mapVal);
        }
        if(!address2.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = mapToCData(address2);
            AdxpStreetAddressLine enG = AdxpStreetAddressLine.Factory.newInstance();
            enG.set(mapVal);

            if (out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray().length > 1) {
                out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1,  AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(1).set(mapVal);
            } else {
                out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0,  AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(mapVal);
            }

        }
        if(!city.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = mapToCData(city);
            AdxpCity enG = AdxpCity.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewCity();

            out.getParticipantRole().getAddrArray(0).setCityArray(0,  AdxpCity.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCityArray(0).set(mapVal);
        }
        if(!county.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = mapToCData(county);
            AdxpCounty enG = AdxpCounty.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewCounty();

            out.getParticipantRole().getAddrArray(0).setCountyArray(0,  AdxpCounty.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(mapVal);
        }
        if(!zip.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = mapToCData(zip);
            AdxpPostalCode enG = AdxpPostalCode.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewPostalCode();

            out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0,  AdxpPostalCode.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(mapVal);
        }

        if(!state.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = mapToCData(state);
            AdxpState enG = AdxpState.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewState();

            out.getParticipantRole().getAddrArray(0).setStateArray(0,  AdxpState.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStateArray(0).set(mapVal);
        }
        if(!country.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = mapToCData(country);
            AdxpCountry enG = AdxpCountry.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewCountry();

            out.getParticipantRole().getAddrArray(0).setCountryArray(0,  AdxpCountry.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(mapVal);
        }
        if(!telephone.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(new ArrayList(Arrays.asList("WP")));
            int phoneExtnSize= extn.length();
            if(phoneExtnSize>0){
                telephone=telephone+ extnStr+ extn;
            }

            out.getParticipantRole().getTelecomArray(teleCounter).setValue(telephone);
            teleCounter = teleCounter+1;
        } if(!email.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(new ArrayList(Arrays.asList("WP")));
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(email);
            teleCounter= teleCounter + 1;
        }



        return out;
    }

    private POCDMT000040Component3 mapToExtendedData(EcrMsgXmlAnswerDto in, POCDMT000040Component3 out) throws XmlException {
        // CHECK mapToExtendedData
        String dataType="";
        if (!in.getDataType().isEmpty()) {
            dataType = in.getDataType();
        }



        if (!in.getAnswerXmlTxt().isEmpty()) {
//            XmlCursor cursor = out.newCursor();
//            cursor.toEndToken(); // Move to the end token of the current element
//            cursor.beginElement("childElementName"); // Replace "childElementName" with the name of your new child element
//            cursor.insertChars(in.getAnswerXmlTxt());
//            cursor.dispose();

            ANY any = ANY.Factory.parse(in.getAnswerXmlTxt());

            out.set(any);
        }
        return out;
    }



    private String mapToAddressType(String data, String questionCode) {
        String output = "";
        // Mapping To Code Anwser goes here, may have to call out to RhapsodyAnswer table
        // mapToCodedAnswer
        var answer = mapToCodedAnswer(data, questionCode);

        if (!answer.getCode().isEmpty()) {
            output = answer.getCode();
        }

        if (!answer.getDisplayName().isEmpty()) {
            output = output + "^" + answer.getDisplayName();
        }

        if (!answer.getCodeSystemName().isEmpty()) {
            output = output + "^" + answer.getCodeSystemName();
        }


        return output;

    }

    private PhdcAnswerDao mapToCodedAnswer(String data, String questionCode) {
        PhdcAnswerDao model = new PhdcAnswerDao();
        String translation="";
        String isTranslationReq= "YES";
        String code = "";
        String transCode = data;
        String transCodeSystem = "";
        String transCodeSystemName = "";
        String transDisplayName = "";
        String codeSystem = "";
        String codeSystemName = "";
        String displayName = "";

        // RhapsodyTableLookup(output, tableName, resultColumnName, defaultValue, queryColumn1, queryValue1, queryColumn2, queryValue2, ...)
        // DI - output = RhapsodyTableLookup(resultColumnName, defaultValue, queryColumn1, queryValue1, queryColumn2, queryValue2, ...)
        var phdcAnswer = ecrLookUpService.fetchPhdcAnswerByCriteriaForTranslationCode(questionCode, data);
        if (phdcAnswer != null) {
            isTranslationReq = phdcAnswer.getCodeTranslationRequired();
            code = phdcAnswer.getAnsToCode();
            transCodeSystem = phdcAnswer.getAnsFromCodeSystemCd();
            transCodeSystemName = phdcAnswer.getAnsFromCodeSystemCd();
            transDisplayName = phdcAnswer.getAnsFromDisplayNm();
            codeSystem = phdcAnswer.getAnsToCodeSystemCd();
            codeSystemName = phdcAnswer.getAnsToCodeSystemDescTxt();
            displayName = phdcAnswer.getAnsToDisplayNm();
        }
        else {
            transCodeSystem = idRoot;
            codeSystem = idRoot;
            isTranslationReq = notMappedValue;
            code = notMappedValue;
            transCodeSystemName = notMappedValue;
            transDisplayName = notMappedValue;
            codeSystemName =notMappedValue;
            displayName = notMappedValue;
        }

        if (code.equalsIgnoreCase(notMappedValue)) {
            code = data;
        }

        if (code.equalsIgnoreCase("NULL") || code.isEmpty()) {
            code = data ;
            codeSystem = transCodeSystem;
            codeSystemName = transCodeSystemName;
            displayName = transDisplayName;
        }

        model.setCode(code);
        model.setCodeSystem(codeSystem);
        model.setCodeSystemName(codeSystemName);
        model.setDisplayName(displayName);
        model.setTransCode(transCode);
        model.setTransCodeSystem(transCodeSystem);
        model.setTransCodeSystemName(transCodeSystemName);
        model.setTransDisplayName(transDisplayName);

        return model;
    }

    private String mapToQuestionId(String data) {
        String output = "";
        QuestionIdentifierMapDao model = new QuestionIdentifierMapDao();
        var qIdentifier = ecrLookUpService.fetchQuestionIdentifierMapByCriteriaByCriteria("COLUMN_NM", data);
        if(qIdentifier != null) {
            if (qIdentifier.getDynamicQuestionIdentifier().equalsIgnoreCase("STANDARD")) {
                model.setQuestionIdentifier(qIdentifier.getQuestionIdentifier());
                output = model.getQuestionIdentifier();
            } else {
                model.setDynamicQuestionIdentifier(qIdentifier.getDynamicQuestionIdentifier());
                output = model.getDynamicQuestionIdentifier();
            }
        }
        return output;
    }

    private POCDMT000040CustodianOrganization mapToElementValue(String data, POCDMT000040CustodianOrganization output, String name) {
        // CHECK mapToElementValue

        XmlCursor cursor = output.newCursor();
        cursor.toFirstChild();
        cursor.beginElement(name);
        cursor.insertAttributeWithValue("xmlns", xmlNameSpaceHolder);
        cursor.insertProcInst("CDATA", data);
        cursor.dispose();

        return output;
    }

    private CE mapToCEAnswerType(String data, String questionCode) {
        CE ce = CE.Factory.newInstance();
        var answer = mapToCodedAnswer(data, questionCode);

        ce.setCode(answer.getCode());
        ce.setCodeSystem(answer.getCodeSystem());
        ce.setCodeSystemName(answer.getCodeSystemName());
        ce.setDisplayName(answer.getDisplayName());

        CD cd = CD.Factory.newInstance();
        cd.setCode(answer.getTransCode());
        cd.setCodeSystem(answer.getTransCodeSystem());
        cd.setCodeSystemName(answer.getTransCodeSystemName());
        cd.setDisplayName(answer.getTransDisplayName());
        CD[] cdArr = {cd};
        ce.setTranslationArray(cdArr);
        return ce;
    }

    private TS mapToTsType(String data) throws ParseException {
        TS ts = TS.Factory.newInstance();
        String result = "";
        boolean checkerCode = data.contains("/");
        boolean checkerCodeDash = data.contains("-");
        if (!checkerCode && !checkerCodeDash) {
            result = data;
        }
        else if (checkerCodeDash && !data.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.S");
            Date date = inputFormat.parse(data);
            result = outputFormat.format(date);
        }
        else if (checkerCode && !data.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.S");
            Date date = inputFormat.parse(data);
            result = outputFormat.format(date);
        }

        ts.setValue(result);
        return ts;
    }


    private XmlObject mapToCData(String data) throws EcrCdaXmlException {
        try {
//        String xmlTemplate = "<to-be-remove><![CDATA[REPLACE_STRING]]></to-be-remove>";
//        String updatedXML = xmlTemplate.replace("REPLACE_STRING", data);
//
            XmlObject xmlObject = XmlObject.Factory.parse("<CDATA>"+data+"</CDATA>");
//        XmlCursor cursor = xmlObject.newCursor();
//        cursor.toFirstChild();
//        cursor.insertChars(data);
//        cursor.dispose();
            return xmlObject;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }


    private XmlObject mapToStringData(String data) throws EcrCdaXmlException {
        try {
            //        String xmlTemplate = "<to-be-remove><![CDATA[REPLACE_STRING]]></to-be-remove>";
//        String updatedXML = xmlTemplate.replace("REPLACE_STRING", data);
//
            XmlObject xmlObject = XmlObject.Factory.parse("<STRING>"+data+"</STRING>");
//        XmlCursor cursor = xmlObject.newCursor();
//        cursor.toFirstChild();
//        cursor.insertChars(data);
//        cursor.dispose();
            return xmlObject;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }



    private String getCurrentUtcDateTimeInCdaFormat() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX");
        String formattedDate = utcNow.format(formatter);
        return formattedDate;
    }

    private POCDMT000040Component3 mapToPatient(int counter, String colName, String data, POCDMT000040Component3 component3) throws XmlException, ParseException {
       var questionCode = mapToQuestionId(colName);
       var count = 0;
       if (component3.getSection() == null) {
           component3.addNewSection();
       }

       if (component3.getSection().getEntryArray().length == 0){
           component3.getSection().addNewEntry();
       } else {
           count = component3.getSection().getEntryArray().length + 1 - 1;
           component3.getSection().addNewEntry();
       }

       if (!component3.getSection().getEntryArray(count).isSetObservation()) {
           component3.getSection().getEntryArray(count).addNewObservation();
       }

       POCDMT000040Observation observation = component3.getSection().getEntryArray(count).getObservation();
       observation = mapToObservation(questionCode, data, observation);

        component3.getSection().getEntryArray(counter).setObservation(observation);
        return component3;
    }

    /*
    * TEST NEEDED
    * */
    private POCDMT000040Observation mapToObservation(String questionCode, String data, POCDMT000040Observation observation) throws XmlException, ParseException {
        observation.setClassCode("OBS");
        observation.setMoodCode(XActMoodDocumentObservation.EVN);
        String dataType="DATE";
        String defaultQuestionIdentifier = "";

        PhdcQuestionLookUpDto questionLup = new PhdcQuestionLookUpDto();
        questionLup.setQuestionIdentifier(notFoundValue);
        questionLup.setQuesCodeSystemCd(notFoundValue);
        questionLup.setQuesCodeSystemDescTxt(notFoundValue);
        questionLup.setQuesDisplayName(notFoundValue);
        questionLup.setDataType(notFoundValue);
        var result = ecrLookUpService.fetchPhdcQuestionByCriteria(questionCode);
        if (result != null) {

            //region DB LOOKUP
            if (!result.getQuestionIdentifier().isEmpty()) {
                questionLup.setQuestionIdentifier(result.getQuestionIdentifier());
            }
            if (!result.getQuesCodeSystemCd().isEmpty()) {
                questionLup.setQuesCodeSystemCd(result.getQuesCodeSystemCd());
            }
            if (!result.getQuesCodeSystemDescTxt().isEmpty()) {
                questionLup.setQuesCodeSystemDescTxt(result.getQuesCodeSystemDescTxt());
            }
            if (!result.getQuesDisplayName().isEmpty()) {
                questionLup.setQuesDisplayName(result.getQuesDisplayName());
            }
            if (!result.getDataType().isEmpty()) {
                questionLup.setDataType(result.getDataType());
            }

            QuestionIdentifierMapDto map = new QuestionIdentifierMapDto();
            map.setDynamicQuestionIdentifier(notFoundValue);
            QuestionIdentifierMapDto identifierMap = ecrLookUpService.fetchQuestionIdentifierMapByCriteriaByCriteria("Question_Identifier", questionCode);
            if(identifierMap != null && !identifierMap.getDynamicQuestionIdentifier().isEmpty()) {
                map.setDynamicQuestionIdentifier(identifierMap.getDynamicQuestionIdentifier());
            }

            if(map.getDynamicQuestionIdentifier().equalsIgnoreCase("STANDARD")
                    || map.getDynamicQuestionIdentifier().equalsIgnoreCase(notFoundValue)) {
                defaultQuestionIdentifier = questionCode;
            }

            //endregion

            if (!result.getDataType().isEmpty()) {
                if (result.getDataType().equalsIgnoreCase(dataTypeCode)) {
                    var dataList = GetStringsBeforePipe(data);
                    String dataStr = "";
                    for(int i = 0; i < dataList.size(); i++) {
//                        int c = 0;
//                        if (observation.getValueArray().length == 0) {
//                            observation.addNewValue();
//                        }
//                        else {
//                            c = observation.getValueArray().length;
//                            observation.addNewValue();
//                        }
//                        CE ce = mapToCEAnswerTypeNoTranslation(
//                                dataList.get(i),
//                                defaultQuestionIdentifier);
//                        observation.setValueArray(c, ce);

                        dataStr = dataStr + " " +  dataList.get(i);

                    }
                    dataStr  = dataStr.trim();
                    observation.addNewCode();
                    observation.getCode().setCode(dataStr);
                    observation.getCode().setCodeSystem(result.getQuesCodeSystemCd());
                    observation.getCode().setCodeSystemName(result.getQuesCodeSystemDescTxt());
                    observation.getCode().setDisplayName(result.getQuesDisplayName());
                }
                else {
                    if (result.getDataType().equalsIgnoreCase("TEXT")) {
                        // CHECK mapToSTValue from ori code
                        observation.addNewCode();
                        observation.getCode().setCode(data);
                        observation.getCode().setCodeSystem(result.getQuesCodeSystemCd());
                        observation.getCode().setCodeSystemName(result.getQuesCodeSystemDescTxt());
                        observation.getCode().setDisplayName(result.getQuesDisplayName());
                    }
                    else if (result.getDataType().equalsIgnoreCase("PART")) {
                        // CHECK mapToObservation from ori 47
                        if (observation.getValueArray().length == 0) {
                            observation.addNewValue();
                        }

                        if (observation.getCode() == null) {
                            observation.addNewCode();
                        }

                        ANY any = ANY.Factory.parse(valueTag);
                        var element = any;
                        XmlCursor cursor = element.newCursor();
                        cursor.toFirstAttribute();
                        cursor.toNextToken();
                        cursor.insertAttributeWithValue(new QName(nameSpaceUrl, "type"), "II");
                        var val = ecrLookUpService.fetchPhdcQuestionByCriteriaWithColumn("Question_Identifier", defaultQuestionIdentifier);
                        cursor.insertAttributeWithValue("root",  val.getQuesCodeSystemCd());

                        cursor.insertAttributeWithValue("extension", data);
                        cursor.dispose();

                        observation.getCode().setCodeSystem(result.getQuesCodeSystemCd());
                        observation.getCode().setCodeSystemName(result.getQuesCodeSystemDescTxt());
                        observation.getCode().setDisplayName(result.getQuesDisplayName());

                        observation.getCode().setCode(data);

                        observation.setValueArray(0, element); // THIS




                    }
                    else if (result.getDataType().equalsIgnoreCase("DATE")) {
                        var ts = mapToTsType(data).getValue().toString();
                        observation.addNewCode();
                        observation.getCode().setCode(ts);
                        observation.getCode().setCodeSystem(result.getQuesCodeSystemCd());
                        observation.getCode().setCodeSystemName(result.getQuesCodeSystemDescTxt());
                        observation.getCode().setDisplayName(result.getQuesDisplayName());

//                        ANY any = ANY.Factory.parse(valueTag);
//                        var element = any;
//                        XmlCursor cursor = element.newCursor();
//                        if (cursor.toFirstAttribute() || !cursor.toEndToken().isStart()) { // Added check here
//                            cursor.setAttributeText(new QName(nameSpaceUrl, "type"), "TS");
//                            if (cursor.getAttributeText(new QName(valueTest)) != null) {
//                                cursor.setAttributeText(new QName(valueTest), mapToTsType(data).toString());
//                            } else {
//                                cursor.toStartDoc();
//                                cursor.toNextToken(); // Moves to the start of the element
//                                cursor.insertAttributeWithValue(valueTest, mapToTsType(data).toString());
//                            }
//                            cursor.dispose();
//
//                            observation.setValueArray(0, element);
//
//
//
//
//                        } else {
//                            cursor.dispose();
//                            // Handle the case where the element didn't have attributes, if necessary
//                        }
                    }
                    else {
                        // CHECK mapToObservation from ori 77
//                        if (observation.getValueArray().length == 0) {
//                            observation.addNewValue();
//                        }
//
//                        ANY any = ANY.Factory.parse(valueTag);
//
//                        var element = any;
//                        XmlCursor cursor = element.newCursor();
//                        cursor.toFirstAttribute();
//                        cursor.setAttributeText(new QName(nameSpaceUrl, "type"), "ST");
//                        cursor.toParent();
//                        cursor.setTextValue(data);
//                        cursor.dispose();
//
//                        observation.setValueArray(0,  any);

                        observation.addNewCode();
                        observation.getCode().setCode(data);
                        observation.getCode().setCodeSystem(result.getQuesCodeSystemCd());
                        observation.getCode().setCodeSystemName(result.getQuesCodeSystemDescTxt());
                        observation.getCode().setDisplayName(result.getQuesDisplayName());
                    }
                }
            }
        } else {

            if (observation.getCode() == null) {
                observation.addNewCode();
            }
            observation.getCode().setCode(data + questionCode);
            observation.getCode().setCodeSystem(codeNotMappedValue);
            observation.getCode().setCodeSystemName(codeNotMappedValue);
            observation.getCode().setDisplayName(codeNotMappedValue);
        }
        return observation;
    }

    private XmlObject mapToSTValue(String input, XmlObject output) {
        XmlCursor cursor = output.newCursor();

        // Navigate to the 'value' element
        if (cursor.toChild(new QName(valueTest))) {
            // Set the attributes of the 'value' element
            cursor.setAttributeText(new QName(nameSpaceUrl, "type"), "ST");

            // Add child CDATA to 'value' element
            cursor.toNextToken(); // Move to the end of the current element (value element)
            cursor.insertChars(input);
        }

        cursor.dispose();
        return output;
    }

    private XmlObject mapToUsableTSElement(String data, XmlObject output, String name) throws ParseException {
        XmlCursor cursor = output.newCursor();
        cursor.toFirstChild();  // Move to the root element

        cursor.beginElement(name);
        cursor.insertAttributeWithValue("type", "IVL_TS");
        cursor.toFirstChild();  // Move inside childName
        cursor.beginElement("low");
        cursor.insertNamespace("", xmlNameSpaceHolder);
        cursor.insertAttributeWithValue(valueTest, mapToTsType(data).getValue().toString());
        cursor.dispose();
        return output;
    }

    private POCDMT000040StructuredBody mapToCase(int entryCounter, EcrSelectedCase caseDto, POCDMT000040StructuredBody output) throws XmlException, ParseException, EcrCdaXmlException {
        int componentCaseCounter=output.getComponentArray().length -1;
        int repeats = 0;

        // case index in case list
        int counter= entryCounter;

        for (Map.Entry<String, Object> entry : caseDto.getMsgCase().getDataMap().entrySet()) {
            String name = entry.getKey();

            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            boolean patLocalIdFailedCheck = (name.equalsIgnoreCase(patLocalId) && caseDto.getMsgCase().getPatLocalId() == null)
                    || (name.equalsIgnoreCase(patLocalId)  && caseDto.getMsgCase().getPatLocalId() != null && caseDto.getMsgCase().getPatLocalId().isEmpty());
            boolean patInvEffTimeFailedCheck = name.equalsIgnoreCase("invEffectiveTime")  && caseDto.getMsgCase().getInvEffectiveTime() == null;
            boolean patInvAuthorIdFailedCheck = (name.equalsIgnoreCase("invAuthorId") && caseDto.getMsgCase().getInvAuthorId() == null)
                    || (name.equalsIgnoreCase("invAuthorId") && caseDto.getMsgCase().getInvAuthorId() != null && caseDto.getMsgCase().getInvAuthorId().isEmpty());
            if (patLocalIdFailedCheck || patInvEffTimeFailedCheck || patInvAuthorIdFailedCheck) {
                // do nothing
            }
            else if (value != null && !value.isEmpty()) {
                String questionId= "";

                questionId = mapToQuestionId(name);


                if (name.equalsIgnoreCase("invConditionCd")) {
                    repeats = (int) caseDto.getMsgCase().getInvConditionCd().chars().filter(x -> x == '^').count();
                }


                if (repeats > 1) {
                    int c = 0;
                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length == 0) {
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    } else {
                        c = output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    }

                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation() == null) {
                        output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).addNewObservation();
                    }


                    var element = output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation();
                    var obs = mapTripletToObservation(
                            value,
                            questionId,
                            element
                    );
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).setObservation(obs);
                    repeats = 0;
                }
                else {
                    int c = 0;
                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length == 0) {
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    } else {
                        c = output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    }

                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation() == null) {
                        output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).addNewObservation();
                    }
                    var element = output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation();
                    POCDMT000040Observation obs = mapToObservation(
                            questionId,
                            value,
                            element
                    );
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).setObservation(obs);
                }
                counter++;
            }
        }


        int questionGroupCounter=0;
        int componentCounter=0;
        int answerGroupCounter=0;
        String OldQuestionId=change;
        int sectionCounter = 0;
        int repeatComponentCounter=0;


        if (caseDto.getMsgCaseParticipants() == null) {
            caseDto.setMsgCaseParticipants(new ArrayList<>());
        }

        if (caseDto.getMsgCaseAnswers() == null) {
            caseDto.setMsgCaseAnswers(new ArrayList<>());
        }

        if (caseDto.getMsgCaseAnswerRepeats() == null) {
            caseDto.setMsgCaseAnswerRepeats(new ArrayList<>());
        }

        if (caseDto.getMsgCaseParticipants().size() > 0
                || caseDto.getMsgCaseAnswers().size() > 0 || caseDto.getMsgCaseAnswerRepeats().size() > 0) {

            /**
             * CASE PARTICIPANT
             * */
            if (caseDto.getMsgCaseParticipants() != null) {
                for(int i = 0; i < caseDto.getMsgCaseParticipants().size(); i++) {
                    int c = 0;
                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length == 0) {
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    }
                    else {
                        c = output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    }

                    if (!output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).isSetObservation()) {
                        output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).addNewObservation();
                    }

                    var element = output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation();

                    POCDMT000040Observation out = mapToObsFromParticipant(
                            caseDto.getMsgCaseParticipants().get(i),
                            element
                    );
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).setObservation(out);
                    counter++;
                }
            }


            /**
             * CASE ANSWER
             * */
            if (caseDto.getMsgCaseAnswers() != null) {
                for(int i = 0; i < caseDto.getMsgCaseAnswers().size(); i++) {
                    var out = output.getComponentArray(componentCaseCounter);
                    var res = mapToMessageAnswer(
                            caseDto.getMsgCaseAnswers().get(i),
                            OldQuestionId,
                            counter,
                            out );

                    OldQuestionId = res.getQuestionSeq();
                    counter = res.getCounter();
                    output.setComponentArray(componentCaseCounter, res.getComponent());
                }
            }

            if (caseDto.getMsgCaseAnswerRepeats() != null) {
                for(int i = 0; i < caseDto.getMsgCaseAnswerRepeats().size(); i++) {
                    if (repeatComponentCounter == 0) {
                        componentCaseCounter++;
                        repeatComponentCounter = 1;
                        output.addNewComponent().addNewSection();
                    }

                    var out = output.getComponentArray(componentCaseCounter).getSection();

                    var ot = mapToMultiSelect(caseDto.getMsgCaseAnswerRepeats().get(i),
                            answerGroupCounter, questionGroupCounter, sectionCounter, out);

                    answerGroupCounter = ot.getAnswerGroupCounter();
                    questionGroupCounter = ot.getQuestionGroupCounter();
                    sectionCounter = ot.getSectionCounter();

                    output.getComponentArray(componentCaseCounter).setSection(ot.getComponent());
                }
            }

        }


        // CHECK mapToCase
        return output;


    }



    private MultiSelect mapToMultiSelect(EcrMsgCaseAnswerRepeatDto in,
                                         int answerGroupCounter,
                                         int questionGroupCounter,
                                         int sectionCounter, POCDMT000040Section out) throws XmlException, ParseException, EcrCdaXmlException {

        if (out.getCode() == null) {
            out.addNewCode();
        }
        if (out.getTitle() == null) {
            out.addNewTitle();
        }

        out.getCode().setCode("1234567-RPT");
        out.getCode().setCodeSystem(clinicalCodeSystem);
        out.getCode().setCodeSystemName(clinicalCodeSystemName);
        out.getCode().setDisplayName("Generic Repeating Questions Section");
        out.getTitle().set(mapToStringData("REPEATING QUESTIONS"));
        int componentCounter = 0;
        String dataType="DATE";
        int seqNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;
        String questionIdentifier="";

        MultiSelect model = new MultiSelect();

        if (out.getEntryArray().length == 0) {
            out.addNewEntry().addNewOrganizer();

        } else {
            sectionCounter = out.getEntryArray().length;
            out.addNewEntry().addNewOrganizer();
        }



        // CHECK MapToMutliSelect
        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name= entry.getKey();
            String value= entry.getValue().toString();


            if (name.equalsIgnoreCase(colQuestionGroupSeqNbr)) {
                questionGroupSeqNbr = Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if (name.equalsIgnoreCase(colAnswerGroupSeqNbr)) {

                if (out.getEntryArray(sectionCounter).getOrganizer() == null) {
                    out.getEntryArray(sectionCounter).addNewOrganizer();
                }

                answerGroupSeqNbr = Integer.valueOf(in.getAnswerGroupSeqNbr());
                if((answerGroupSeqNbr==answerGroupCounter) && (questionGroupSeqNbr ==questionGroupCounter)){
                    componentCounter = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray().length;
                }
                else {
                    if (out.getEntryArray(sectionCounter).getOrganizer().getCode() == null) {
                        out.getEntryArray(sectionCounter).getOrganizer().addNewCode();
                    }

                    if (out.getEntryArray(sectionCounter).getOrganizer().getStatusCode() == null) {
                        out.getEntryArray(sectionCounter).getOrganizer().addNewStatusCode();
                    }

                   // sectionCounter = out.getEntryArray().length;
                    questionGroupCounter=questionGroupSeqNbr ;
                    answerGroupCounter=answerGroupSeqNbr;
                    out.getEntryArray(sectionCounter).getOrganizer().getCode().setCode(String.valueOf(questionGroupSeqNbr));
                    out.getEntryArray(sectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                    out.getEntryArray(sectionCounter).getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);;
                    out.getEntryArray(sectionCounter).getOrganizer().setMoodCode("EVN");
                    out.getEntryArray(sectionCounter).getOrganizer().getStatusCode().setCode("completed");;
                    componentCounter=0;
                }

                if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray().length == 0) {
                    out.getEntryArray(sectionCounter).getOrganizer().addNewComponent().addNewObservation();
                } else {
                    componentCounter = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray().length;
                    out.getEntryArray(sectionCounter).getOrganizer().addNewComponent().addNewObservation();
                }

                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setClassCode("OBS");
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);

            }
            else if (name.equalsIgnoreCase(colDataType)) {
                dataType = in.getDataType();
            }
            else if (name.equalsIgnoreCase(colSeqNbr)) {
                seqNbr = Integer.valueOf(in.getSeqNbr());
            }

            if(dataType.equalsIgnoreCase(dataTypeCode) || dataType.equalsIgnoreCase("CODED_COUNTY")){
                CE ce = CE.Factory.newInstance();
                if (name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals(colAnsCodeSystemCd) && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals(colAnsCodeSystemDescTxt) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals(colAnsDisplayTxt) && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals(colAnsToCode) && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals(colAnsToCodeSystemCd) && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals(colAnsToCodeSystemDescTxt) && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals(colAnsToDisplayNm) && !in.getAnsToDisplayNm().isEmpty()) {
                    ce.setDisplayName(in.getAnsToDisplayNm());

                }




                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(seqNbr).set(ce);
            }
            else if ((dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase(dataTypeNumeric)) &&
                    name.equals(colAnswerTxt)) {
                if(questionIdentifier.equalsIgnoreCase("NBS243") ||
                        questionIdentifier.equalsIgnoreCase("NBS290")) {
                    var element = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
                    var ot = mapToObservationPlace(
                            in.getAnswerTxt(),
                            element);
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation) ot);
                }
                else {

                    var element = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
                    var ot = mapToSTValue(
                            in.getAnswerTxt(),
                            element);
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation)ot);
                }



            }
            else if(dataType.equalsIgnoreCase("DATE")){
                if(name.equals(colAnswerTxt)){
                    int c = 0;

                    var size = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray().length;

                    if (size == 0) {
                        out.getEntryArray(sectionCounter).getOrganizer().addNewComponent();
                    } else {
                        componentCounter = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray().length;
                        out.getEntryArray(sectionCounter).getOrganizer().addNewComponent();
                    }

                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation();


                    if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray().length == 0) {
                        out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewValue();
                    } else {
                        c = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray().length;
                        out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewValue();
                    }
                    var element = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(c);
                    XmlCursor cursor = element.newCursor();
                    cursor.toFirstChild();
                    cursor.setAttributeText(new QName(nameSpaceUrl, "type"), "TS");
                    cursor.setAttributeText(new QName("", valueTest), null);
                    if (name.equals(colAnswerTxt)) {
                        String newValue = mapToTsType(in.getAnswerTxt()).toString();
                        cursor.setAttributeText(new QName("", valueTest), newValue);
                    }
                    cursor.dispose();
                }
            }

            if(name.equals(colQuestionIdentifier)){

                if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation() == null) {
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation().addNewCode();
                } else {
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
                }
                questionIdentifier= value;
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                        .getCode().setCode(in.getQuestionIdentifier());
            }
            else if(name.equals(colQuesCodeSystemCd)){
                if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation() == null) {
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation().addNewCode();
                } else {
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
                }
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                        .getCode().setCodeSystem(in.getQuesCodeSystemCd());
            }
            else if(name.equals(colQuesCodeSystemDescTxt)){
                if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation() == null) {
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation().addNewCode();
                } else {
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
                }
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                        .getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());;
            }
            else if(name.equals(colQuesDisplayTxt)){
                if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation() == null) {
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation().addNewCode();
                } else {
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
                }
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                        .getCode().setDisplayName(in.getQuesDisplayTxt());
            }
        }

        model.setAnswerGroupCounter(answerGroupCounter);
        model.setQuestionGroupCounter(questionGroupCounter);
        model.setSectionCounter(sectionCounter);
        model.setComponent(out);
        return model;
    }

    private XmlObject mapToObservationPlace(String in, XmlObject out) {
        XmlCursor cursor = out.newCursor();
        cursor.toFirstChild();
        cursor.setAttributeText(new QName(nameSpaceUrl, "type"), "II");
        cursor.setAttributeText(new QName("", "root"), "2.3.3.3.322.23.34");
        cursor.setAttributeText(new QName("", "extension"), in);
        cursor.dispose();

        return out;
    }

    private MessageAnswer mapToMessageAnswer(EcrMsgCaseAnswerDto in, String questionSeq, int counter, POCDMT000040Component3 out) throws ParseException {
        String dataType="";
        int sequenceNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;

        MessageAnswer model = new MessageAnswer();
        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if (name.equals(colQuestionGroupSeqNbr) &&  !in.getQuestionGroupSeqNbr().isEmpty()) {
                var test = in.getQuestionGroupSeqNbr();
                questionGroupSeqNbr = Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if (name.equals(colAnswerGroupSeqNbr) && !in.getAnswerGroupSeqNbr().isEmpty()) {
                answerGroupSeqNbr = Integer.valueOf(in.getAnswerGroupSeqNbr());
            }
            else if (name.equals(colDataType) && !in.getDataType().isEmpty()) {
                dataType = in.getDataType();
            }
            else if (name.equals(colSeqNbr) && !in.getSeqNbr().isEmpty()) {
                sequenceNbr = out.getSection().getEntryArray(counter).getObservation().getValueArray().length;
            }
            else if (dataType.equalsIgnoreCase(dataTypeCode) || dataType.equalsIgnoreCase(county)) {
                CE ce = CE.Factory.newInstance();
                if (name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals(colAnsCodeSystemCd) && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals(colAnsCodeSystemDescTxt) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals(colAnsDisplayTxt) && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals(colAnsToCode) && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals(colAnsToCodeSystemCd) && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals(colAnsToCodeSystemDescTxt) && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals(colAnsToDisplayNm) && !in.getAnsToDisplayNm().isEmpty()) {
                    if(ce.getTranslationArray(0).getDisplayName().equals("OTH^")) {
                        ce.setDisplayName(ce.getTranslationArray(0).getDisplayName());
                    }
                    else {
                        ce.setDisplayName(in.getAnsToDisplayNm());
                    }
                }
                out.getSection().getEntryArray(counter).getObservation().getValueArray(sequenceNbr).set(ce);
            }

            else if (dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase(dataTypeNumeric)) {
                if (name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()) {
                    // CHECK mapToSTValue
                    var element = out.getSection().getEntryArray(counter).getObservation();
                    var ot = mapToSTValue(in.getAnswerTxt(), element);
                    out.getSection().getEntryArray(counter).setObservation((POCDMT000040Observation) ot);
                }
            }
            else if (dataType.equalsIgnoreCase("DATE")) {
                if (name.equals(colAnswerTxt) && !in.getAnswerTxt().isEmpty()) {
                    // CHECK mapToMessageAnswer
                    var element = out.getSection().getEntryArray(counter).getObservation().getValueArray(0);
                    XmlCursor cursor = element.newCursor();
                    cursor.toFirstAttribute();
                    cursor.insertAttributeWithValue(new QName(nameSpaceUrl, "type"), "TS");
                    cursor.insertAttributeWithValue(valueTest, ""); // As per your code, it's empty

                    var ot = mapToTsType(in.getAnswerTxt()).toString();
                    cursor.setAttributeText(new QName("", valueTest), ot);
                    cursor.dispose();
                }
            }

            if (!in.getQuestionIdentifier().isEmpty()) {
                if (in.getQuestionIdentifier().equalsIgnoreCase(questionSeq)) {
                    // ignore
                }
                else {
                    if (questionSeq.equalsIgnoreCase(change)) {
                        // ignore
                    }
                    else {
                        counter++;
                        sequenceNbr = 0;
                    }
                    questionSeq = in.getQuestionIdentifier();

                    var size = out.getSection().getEntryArray().length;
                    if (out.getSection().getEntryArray().length - 1 < counter) {
                        out.getSection().addNewEntry().addNewObservation().addNewCode();
                    }
                    out.getSection().getEntryArray(counter).getObservation().setClassCode("OBS");
                    out.getSection().getEntryArray(counter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
                    out.getSection().getEntryArray(counter).getObservation().getCode().setCode(in.getQuestionIdentifier());
                }
            }
            else if (!in.getQuesCodeSystemCd().isEmpty()) {
                out.getSection().getEntryArray(counter).getObservation().getCode().setCodeSystem(in.getQuesCodeSystemCd());
            }
            else if (!in.getQuesCodeSystemDescTxt().isEmpty()) {
                out.getSection().getEntryArray(counter).getObservation().getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());
            }
            else if (!in.getQuesDisplayTxt().isEmpty()) {
                out.getSection().getEntryArray(counter).getObservation().getCode().setDisplayName(in.getQuesDisplayTxt());
            }

        }

        model.setQuestionSeq(questionSeq);
        model.setCounter(counter);
        model.setComponent(out);
        return model;
    }

    /**
     * TEST NEEDED
     * */
    private POCDMT000040Observation mapToObsFromParticipant(EcrMsgCaseParticipantDto in, POCDMT000040Observation out) throws XmlException, ParseException {
        String localId = "";
        String questionCode ="";

        if (in.getAnswerTxt() != null && !in.getAnswerTxt().isEmpty()) {
            localId = in.getAnswerTxt();
        }

        if (in.getQuestionIdentifier() != null && !in.getQuestionIdentifier().isEmpty()) {
            questionCode = in.getQuestionIdentifier();
        }

        return mapToObservation(questionCode, localId, out);
    }

    private POCDMT000040Observation mapTripletToObservation(String invConditionCd, String questionId, POCDMT000040Observation output) {
        output.setClassCode("OBS");
        output.setMoodCode(XActMoodDocumentObservation.EVN);
        List<String> repeats = GetStringsBeforePipe(invConditionCd);

        String tripletCodedValue =  "";
        PhdcQuestionLookUpDto questionLookUpDto = mapToCodedQuestionType(questionId);
        output.getCode().setCode(questionLookUpDto.getQuesCodeSystemCd());
        output.getCode().setCodeSystem(questionLookUpDto.getQuesCodeSystemDescTxt());
        output.getCode().setDisplayName(questionLookUpDto.getQuesDisplayName());
        //output.getCode().setCode(questionId);

        for(int i = 0; i < repeats.size(); i++) {
            if (repeats.size() == 1) {
                tripletCodedValue = invConditionCd;
            } else {
                tripletCodedValue = repeats.get(i);
            }
            var caretStringList = GetStringsBeforeCaret(repeats.get(i));

            if (tripletCodedValue.length() > 0 && caretStringList.size() == 4) {
                // CHECK mapTripletToObservation
                String code = caretStringList.get(0);
                String displayName = caretStringList.get(1);
                String codeSystemName = caretStringList.get(2);
                String codeSystem = caretStringList.get(3);
                int c = 0;
                if (output.getValueArray().length == 0) {
                    output.addNewValue();
                }
                else {
                    c = output.getValueArray().length;
                    output.addNewValue();
                }

                CE ce = CE.Factory.newInstance();
                ce.setCode(code);
                ce.setCodeSystem(codeSystem);
                ce.setCodeSystemName(codeSystemName);
                ce.setDisplayName(displayName);
                output.getValueArray(c).set(ce);
            }
        }

        return output;

    }

    private PhdcQuestionLookUpDto mapToCodedQuestionType(String questionIdentifier) {
        PhdcQuestionLookUpDto dto = new PhdcQuestionLookUpDto();
        dto.setQuesCodeSystemCd(notFoundValue);
        dto.setQuesCodeSystemDescTxt(notFoundValue);
        dto.setQuesDisplayName(notFoundValue);
        if (!questionIdentifier.isEmpty()) {
            var result = ecrLookUpService.fetchPhdcQuestionByCriteriaWithColumn("QUESTION_IDENTIFIER", questionIdentifier);
            if (result != null) {
                if (result.getQuesCodeSystemCd() != null && !result.getQuesCodeSystemCd().isEmpty()) {
                    dto.setQuesCodeSystemCd(result.getQuesCodeSystemCd());
                }
                else if (result.getQuesCodeSystemDescTxt() != null && !result.getQuesCodeSystemDescTxt().isEmpty()) {
                    dto.setQuesCodeSystemDescTxt(result.getQuesCodeSystemDescTxt());
                }
                else if (result.getQuesDisplayName() != null && !result.getQuesDisplayName().isEmpty()) {
                    dto.setQuesDisplayName(result.getQuesDisplayName());
                }
            }
        }
        return dto;
    }

    private boolean isFieldValid(String fieldName, String fieldValue) {
        return fieldValue != null && !fieldValue.isEmpty();
    }

    private boolean isFieldValid(String fieldName, Date fieldValue) {
        return fieldValue != null;
    }
}
