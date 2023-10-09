package gov.cdc.dataingestion.nbs.ecr.service;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.*;
import gov.cdc.dataingestion.nbs.ecr.model.cases.CdaCaseComponent;
import gov.cdc.dataingestion.nbs.ecr.model.patient.CdaPatientTelecom;
import gov.cdc.dataingestion.nbs.ecr.service.helper.CdaCaseMappingHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.CdaPatientMappingHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaCaseMappingHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaPatientMappingHelper;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedCase;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedInterview;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedTreatment;
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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.GetStringsBeforeCaret;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.GetStringsBeforePipe;

@Service
public class CdaMapper implements ICdaMapper {
    
    private final ICdaLookUpService ecrLookUpService;


    private final ICdaMapHelper cdaMapHelper;
    private final ICdaPatientMappingHelper patientMappingHelper;

    private final ICdaCaseMappingHelper caseMappingHelper;

    @Autowired
    public CdaMapper(ICdaLookUpService ecrLookUpService) {
        this.ecrLookUpService = ecrLookUpService;
        this.cdaMapHelper = new CdaMapHelper(this.ecrLookUpService);
        this.patientMappingHelper = new CdaPatientMappingHelper(this.cdaMapHelper);
        this.caseMappingHelper = new CdaCaseMappingHelper(this.cdaMapHelper);
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
        Integer versionCtrNbr = null;

        //region CONTAINER COMPONENT CREATION
        if (input.getMsgContainer().getInvLocalId() != null && !input.getMsgContainer().getInvLocalId().isEmpty()) {
            clinicalDocument.setId(II.Factory.newInstance());
            clinicalDocument.getId().setRoot(ROOT_ID);
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

        if (input.getMsgContainer().getVersionCtrNbr() != null) {
            versionCtrNbr = input.getMsgContainer().getVersionCtrNbr();
        }

        clinicalDocument.setCode(CE.Factory.newInstance());
        clinicalDocument.getCode().setCode("55751-2");
        clinicalDocument.getCode().setCodeSystem(CODE_SYSTEM);
        clinicalDocument.getCode().setCodeSystemName(CODE_SYSTEM_NAME);
        clinicalDocument.getCode().setDisplayName("Public Health Case Report - PHRI");
        clinicalDocument.setTitle(ST.Factory.newInstance());

        clinicalDocument.getTitle().set(cdaMapHelper.mapToStringData("Public Health Case Report - Data from Legacy System to CDA"));

        clinicalDocument.setEffectiveTime(TS.Factory.newInstance());
        clinicalDocument.getEffectiveTime().setValue(this.cdaMapHelper.getCurrentUtcDateTimeInCdaFormat());

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
        int patientComponentCounter=-1;
        int performerComponentCounter=0;
        int performerSectionCounter=0;
        int clinicalCounter= 0;

        //region SUB COMPONENT CREATION

        // Set RecordTarget && patient Role
        clinicalDocument.addNewRecordTarget();
        clinicalDocument.getRecordTargetArray(0).addNewPatientRole();

        /**MAP TO PATIENT**/
        var pat =  this.patientMappingHelper.mapToPatient(input, clinicalDocument, patientComponentCounter, inv168);
        clinicalDocument = pat.getClinicalDocument();
        inv168 = pat.getInv168();

        /**MAP TO CASE**/
        var ecrCase = caseMappingHelper.mapToCaseTop(input, clinicalDocument, componentCounter, clinicalCounter,
        componentCaseCounter, inv168);
        clinicalDocument = ecrCase.getClinicalDocument();
        componentCounter = ecrCase.getComponentCounter();
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
        componentCounter = ecrPlace.getComponentCounter();

        /**
         * INTERVIEW
         * */
        var ecrInterview = mapToInterviewTop(input, clinicalDocument, interviewCounter, componentCounter);
        clinicalDocument = ecrInterview.getClinicalDocument();
        componentCounter = ecrInterview.getComponentCounter();

        /**
         * TREATMENT
         * */
        var ecrTreatment = mapToTreatmentTop(input, clinicalDocument,
                treatmentCounter, componentCounter, treatmentSectionCounter);
        clinicalDocument = ecrTreatment.getClinicalDocument();

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
        element = this.cdaMapHelper.mapToElementValue(value, element, "name");
        clinicalDocument.getCustodian().getAssignedCustodian().setRepresentedCustodianOrganization(element);

        value = mapToTranslatedValue("CUS103");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewStreetAddressLine();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(cdaMapHelper.mapToCData(value));
        k = k+1;
        value = mapToTranslatedValue("CUS104");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewStreetAddressLine();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(cdaMapHelper.mapToCData(value));
        k = k+1;

        k = 0;
        value = mapToTranslatedValue("CUS105");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewCity();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCityArray(k).set(cdaMapHelper.mapToCData(value));
        k = k+1;

        k = 0;
        value = mapToTranslatedValue("CUS106");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewState();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStateArray(k).set(cdaMapHelper.mapToCData(value));
        k = k+1;

        value = mapToTranslatedValue("CUS107");
        k = 0;
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewPostalCode();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getPostalCodeArray(k).set(cdaMapHelper.mapToCData(value));
        k = k+1;

        value = mapToTranslatedValue("CUS108");
        k = 0;
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewCountry();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCountryArray(k).set(cdaMapHelper.mapToCData(value));
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
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getAssignedPerson().getNameArray(0).getFamilyArray(0).set(cdaMapHelper.mapToCData(value));

        OffsetDateTime now = OffsetDateTime.now();
        String formattedDateTime = formatDateTime(now);

        clinicalDocument.getAuthorArray(0).getTime().setValue(formattedDateTime);
        //endregion

        rootDocument.setClinicalDocument(clinicalDocument);

        //region XML CLEANUP
        XmlCursor cursor = rootDocument.newCursor();
        cursor.toFirstChild();
        cursor.setAttributeText(new QName("sdtcxmlnamespaceholder"), XML_NAME_SPACE_HOLDER);
        cursor.setAttributeText(new QName("sdt"), "urn:hl7-org:sdtc");
        cursor.setAttributeText(new QName("xsi"), NAME_SPACE_URL);
        cursor.setAttributeText(new QName("schemaLocation"), XML_NAME_SPACE_HOLDER + " CDA_SDTC.xsd");
        cursor.dispose();
        var result = convertXmlToString(rootDocument);
        //endregion

        return result;


    }

    private static String formatDateTime(OffsetDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssZ");
        return dateTime.format(formatter);
    }


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

                            var nestedCode = CODE;
                            if (nestedCode.contains("-")) {
                                nestedCode = nestedCode.replaceAll("-", ""); // NOSONAR
                            }
                            clinicalDocument.getCode().setCode(nestedCode);
                            clinicalDocument.getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                            clinicalDocument.getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                            clinicalDocument.getCode().setDisplayName(CODE_DISPLAY_NAME);
                            clinicalDocument.getTitle().set(cdaMapHelper.mapToStringData(CLINICAL_TITLE));
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
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName(ACT_CODE_DISPLAY_NAME);

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
                for(int i = 0; i < input.getMsgOrganizations().size(); i++) {
                    if (clinicalDocument.getCode() == null) {
                        clinicalDocument.addNewCode();
                    }

                    if (clinicalDocument.getTitle() == null) {
                        clinicalDocument.addNewTitle();
                    }

                    if (performerComponentCounter < 1) {
                        componentCounter++;
                        performerComponentCounter = componentCounter;
                        clinicalDocument.getCode().setCode(CODE);
                        clinicalDocument.getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                        clinicalDocument.getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                        clinicalDocument.getCode().setDisplayName(CODE_DISPLAY_NAME);
                        clinicalDocument.getTitle().set(cdaMapHelper.mapToStringData(CLINICAL_TITLE));


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
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName(ACT_CODE_DISPLAY_NAME);

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
                for(int i = 0; i < input.getMsgPlaces().size(); i++) {

                    if (section == null) {
                        section = POCDMT000040Section.Factory.newInstance();
                        section.addNewCode();
                        section.addNewTitle();
                    }


                    if (performerComponentCounter < 1) {
                        componentCounter++;
                        performerComponentCounter = componentCounter;

                        section.getCode().setCode(CODE);
                        section.getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                        section.getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                        section.getCode().setDisplayName(CODE_DISPLAY_NAME);
                        section.getTitle().set(cdaMapHelper.mapToStringData(CLINICAL_TITLE));
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

                    performerSectionCounter = c; // NOSONAR


                    if (section.getEntryArray(c).getAct() == null) {
                        section.getEntryArray(c).addNewAct();
                        section.getEntryArray(c).getAct().addNewParticipant();
                    } else {
                        section.getEntryArray(c).getAct().addNewParticipant();
                    }

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
                    section.getEntryArray(c).getAct().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                    section.getEntryArray(c).getAct().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                    section.getEntryArray(c).getAct().getCode().setDisplayName(ACT_CODE_DISPLAY_NAME);

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
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Interviews");

                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
                        }
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle().set(cdaMapHelper.mapToStringData("INTERVIEW SECTION"));
                    }

                    POCDMT000040Component3 ot = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c);

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
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem(CODE_SYSTEM);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName(CODE_SYSTEM_NAME);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Treatment Information");

                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
                        }
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle().set(cdaMapHelper.mapToStringData("TREATMENT INFORMATION"));

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

            xmlOutput = xmlOutput.replaceAll("<STRING[^>]*>([^<]+)</STRING>", "$1");// NOSONAR // remove string tag
            xmlOutput = xmlOutput.replaceAll("<CDATA[^>]*>(.*?)</CDATA>", "<![CDATA[$1]]>");// NOSONAR // replace CDATA with real CDATA
            xmlOutput = xmlOutput.replaceAll("<(\\w+)></\\1>", "");// NOSONAR // remove empty <tag></tag>
            xmlOutput = xmlOutput.replaceAll("<(\\w+)/>", "");// NOSONAR // remove empty <tag/>
            xmlOutput = xmlOutput.replaceAll("<STUD xmlns=\"\">STUD</STUD>", "");// NOSONAR // remove STUD tag
            xmlOutput = xmlOutput.replaceAll("<stud xmlns=\"\">stud</stud>", "");// NOSONAR // remove STUD tag
            xmlOutput = xmlOutput.replaceAll("(?m)^\\s*$[\n\r]{1,}", "");// NOSONAR // remove new line

            xmlOutput = xmlOutput.replaceAll("sdtcxmlnamespaceholder=\""+ XML_NAME_SPACE_HOLDER +"\"", "xmlns:sdtcxmlnamespaceholder=\""+XML_NAME_SPACE_HOLDER+"\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("sdt=\"urn:hl7-org:sdtc\"", "xmlns:sdt=\"urn:hl7-org:sdtc\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("schemaLocation=\""+ XML_NAME_SPACE_HOLDER +" CDA_SDTC.xsd\"", "xsi:schemaLocation=\""+XML_NAME_SPACE_HOLDER +" CDA_SDTC.xsd\"");// NOSONAR


            xmlOutput = xmlOutput.replaceAll("\\^NOT_MAPPED", "");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("NOT_MAPPED","");// NOSONAR

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
            return NOT_FOUND_VALUE;
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
                    if (output.getDoseQuantity() == null) {
                        output.addNewDoseQuantity();
                    }
                    output.getDoseQuantity().setValue(input.getMsgTreatment().getTrtDosageAmt());
                }
            }

            if(name.equals("trtDrugCd") && value != null && input.getMsgTreatment().getTrtDrugCd() != null && !input.getMsgTreatment().getTrtDrugCd().isEmpty()) {
                treatmentNameQuestion = this.cdaMapHelper.mapToQuestionId("TRT_DRUG_CD");;
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
                output.getIdArray(c).setRoot(ID_ROOT);
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
            cursor.setTextValue(CDATA + customTreatment + CDATA);
            cursor.dispose();

            list.getListArray(c).setItemArray(0, item);

            list.getListArray(c).getCaption().set(cdaMapHelper.mapToCData("CDA Treatment Information Section"));

        }else{
            // TODO: OutXML::Element element1= (OutXML::Element)list.item[counter];
        }

        if (!treatmentName.isEmpty()) {
            if  (output.getConsumable() == null) {
                output.addNewConsumable().addNewManufacturedProduct().addNewManufacturedLabeledDrug().addNewCode();
            }
            var ot = output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getCode();
            var ce = this.cdaMapHelper.mapToCEAnswerType(
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
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getName().set(cdaMapHelper.mapToCData(customTreatment));
        }

        if(!TRT_TREATMENT_DT.isEmpty()){
            if (output.getEffectiveTimeArray().length == 0) {
                output.addNewEffectiveTime();
            }
            var lowElement = output.getEffectiveTimeArray(0);

            XmlObject xmlOb = XmlObject.Factory.newInstance();
            XmlCursor cursor = xmlOb.newCursor();
            cursor.toEndDoc();  // Move to the root element
            cursor.beginElement("low");
            cursor.insertAttributeWithValue(VALUE_NAME,  cdaMapHelper.mapToTsType(TRT_TREATMENT_DT).getValue());

            if (TRT_DURATION_AMT != null && !TRT_DURATION_AMT.isEmpty() && TRT_DURATION_UNIT_CD != null && !TRT_DURATION_UNIT_CD.isEmpty()) {
                cursor.toEndDoc();
                cursor.beginElement("width");
                if (!TRT_DURATION_AMT.isEmpty()) {
                    cursor.insertAttributeWithValue(VALUE_NAME, TRT_DURATION_AMT);
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
                cursor.insertAttributeWithValue(VALUE_NAME, res.getAttribute1());
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

            if((name.equals("msgContainerUid") && in.getMsgInterview().getMsgContainerUid() != null )
                    || (name.equals("ixsAuthorId")  && in.getMsgInterview().getIxsAuthorId() != null)
                    || (name.equals("ixsEffectiveTime")  && in.getMsgInterview().getIxsEffectiveTime() != null)){
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
                var ts = cdaMapHelper.mapToTsType(in.getMsgInterview().getIxsInterviewDt().toString());
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEffectiveTime() == null) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEffectiveTime();
                }
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEffectiveTime().setValue(ts.getValue().toString());
            }

            else if (name.equals("ixsIntervieweeRoleCd")  && in.getMsgInterview().getIxsIntervieweeRoleCd() != null && !in.getMsgInterview().getIxsIntervieweeRoleCd().isEmpty()){
                String questionCode = this.cdaMapHelper.mapToQuestionId("IXS_INTERVIEWEE_ROLE_CD");

                int c = 0;
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length == 0) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                } else {
                    c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length;
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                }

                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).getObservation();
                this.cdaMapHelper.mapToObservation(questionCode, in.getMsgInterview().getIxsIntervieweeRoleCd(), obs);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setObservation(obs);
                entryCounter= entryCounter+ 1;
            }
            else if (name.equals("ixsInterviewTypeCd")  && in.getMsgInterview().getIxsInterviewTypeCd() != null && !in.getMsgInterview().getIxsInterviewTypeCd().isEmpty()){
                String questionCode = this.cdaMapHelper.mapToQuestionId("IXS_INTERVIEW_TYPE_CD");

                int c = 0;
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length == 0) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                } else {
                    c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length;
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                }

                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).getObservation();
                this.cdaMapHelper.mapToObservation(questionCode, in.getMsgInterview().getIxsInterviewTypeCd(), obs);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setObservation(obs);
                entryCounter= entryCounter+ 1;

            }
            else if (name.equals("ixsInterviewLocCd")  && in.getMsgInterview().getIxsInterviewLocCd() != null && !in.getMsgInterview().getIxsInterviewLocCd().isEmpty()){
                String questionCode = this.cdaMapHelper.mapToQuestionId("IXS_INTERVIEW_LOC_CD");

                int c = 0;
                if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length == 0) {
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                } else {
                    c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length;
                    out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
                }
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).getObservation();
                this.cdaMapHelper.mapToObservation(questionCode, in.getMsgInterview().getIxsInterviewLocCd(), obs);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setObservation(obs);
                entryCounter= entryCounter+ 1;
            }
        }

        int questionGroupCounter=0;
        int componentCounter=0;
        int answerGroupCounter=0;
        String OldQuestionId=CHANGE;
        String OldRepeatQuestionId=CHANGE;
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
                CE ce = cdaMapHelper.mapToCEQuestionType("IXS102", element2);
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
                                                                            POCDMT000040Encounter out) throws ParseException, EcrCdaXmlException {
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

            if(name.equals(COL_QUES_GROUP_SEQ_NBR) && !in.getQuestionGroupSeqNbr().isEmpty()){
                questionGroupSeqNbr= Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if(name.equals(COL_ANS_GROUP_SEQ_NBR) && !in.getAnswerGroupSeqNbr().isEmpty()){
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
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setDisplayName("Generic Repeating Questions Section");

                    out.getEntryRelationshipArray(sectionCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().setMoodCode("EVN");
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getStatusCode().setCode("completed");
                    componentCounter=0;

                }
            }

            else if(name.equals(COL_DATA_TYPE) && !in.getDataType().isEmpty() ){
                dataType= in.getDataType();
            }else if(name.equals(COL_SEQ_NBR) && !in.getSeqNbr().isEmpty()){
                seqNbr= Integer.valueOf(in.getSeqNbr()) ;
            }

            if(dataType.equalsIgnoreCase(DATA_TYPE_CODE) || dataType.equalsIgnoreCase("CODED_COUNTY")){
                CE ce = CE.Factory.newInstance();
                if (name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals(COL_ANS_CODE_SYSTEM_CD) && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals(COL_ANS_CODE_SYSTEM_DESC_TXT) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals(COL_ANS_DISPLAY_TXT) && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals(COL_ANS_TO_CODE) && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals(COL_ANS_TO_CODE_SYSTEM_CD) && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals(COL_ANS_TO_CODE_SYSTEM_DESC_TXT) && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals(COL_ANS_TO_DISPLAY_NM) && !in.getAnsToDisplayNm().isEmpty()) {
                    ce.setDisplayName(in.getAnsToDisplayNm());

                }
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(seqNbr).set(ce);
            }
            else if ((dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase(DATA_TYPE_NUMERIC)) &&
                    name.equals(COL_ANS_TXT)){
                if(questionIdentifier.equalsIgnoreCase("NBS243") ||
                        questionIdentifier.equalsIgnoreCase("NBS290")) {

                    var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
                    var ot = cdaMapHelper.mapToObservationPlace(value,
                            element);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation) ot);

                }
                else {
                    var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();

                    var ot = cdaMapHelper.mapToSTValue(value,element);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation)ot);
                }
            }
            else if(dataType.equalsIgnoreCase("DATE")){
                if(name.equals(COL_ANS_TXT)){
                    if (out.getEntryRelationshipArray(sectionCounter).getOrganizer() == null) {
                        out.getEntryRelationshipArray(sectionCounter).addNewOrganizer().addNewComponent().addNewObservation().addNewValue();
                    }
                    var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(0);
                    XmlCursor cursor = element.newCursor();
                    cursor.toFirstChild();
                    cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "TS");
                    cursor.setAttributeText(new QName("", value), null);
                    if (name.equals(COL_ANS_TXT)) {
                        String newValue = cdaMapHelper.mapToTsType(in.getAnswerTxt()).toString();
                        cursor.setAttributeText(new QName("", value), newValue);
                    }
                    cursor.dispose();
                }
            }


            if(name.equals(COL_QUES_IDENTIFIER)){
                questionIdentifier= value;
                if(value.equals(questionId)){
                    // IGNORE
                }else{
                    if(questionId.equals(CHANGE)){

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
            else if(name.equals(COL_QUES_CODE_SYSTEM_CD)){
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCodeSystem(value);
            }
            else if(name.equals(COL_QUES_CODE_SYSTEM_DESC_TXT)){
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCodeSystemName(value);
            }
            else if(name.equals(COL_QUES_DISPLAY_TXT)){
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
                                                            POCDMT000040Encounter out) throws XmlException, ParseException, EcrCdaXmlException {
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

            if(name.equals(COL_QUES_GROUP_SEQ_NBR) && !in.getQuestionGroupSeqNbr().isEmpty()){
                questionGroupSeqNbr= Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if(name.equals(COL_ANS_GROUP_SEQ_NBR) && !in.getAnswerGroupSeqNbr().isEmpty() ){
                answerGroupSeqNbr= Integer.valueOf(in.getAnswerGroupSeqNbr());
            }
            else if(name.equals(COL_DATA_TYPE) && !in.getDataType().isEmpty()){
                dataType=in.getDataType();
            }
            else if(name.equals(COL_SEQ_NBR) && !in.getSeqNbr().isEmpty()){
                sequenceNbr= Integer.valueOf(in.getSeqNbr());
                if(sequenceNbr>0) {
                    sequenceNbr =sequenceNbr-1;
                }
            }

            if(dataType.equalsIgnoreCase(DATA_TYPE_CODE) || dataType.equalsIgnoreCase(COUNTY)){
                CE ce = CE.Factory.newInstance();
                if (name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals(COL_ANS_CODE_SYSTEM_CD) && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals(COL_ANS_CODE_SYSTEM_DESC_TXT) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals(COL_ANS_DISPLAY_TXT) && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals(COL_ANS_TO_CODE) && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals(COL_ANS_TO_CODE_SYSTEM_CD) && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals(COL_ANS_TO_CODE_SYSTEM_DESC_TXT) && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals(COL_ANS_TO_DISPLAY_NM) && !in.getAnsToDisplayNm().isEmpty()) {
                    ce.setDisplayName(in.getAnsToDisplayNm());

                }
                out.getEntryRelationshipArray(counter).getObservation().getValueArray(sequenceNbr).set(ce);

            }
            else if(
                    dataType.equals("TEXT") ||
                    dataType.equals(DATA_TYPE_NUMERIC)){
                if(name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()){
                    var element = out.getEntryRelationshipArray(counter).getObservation();
                    var ot = cdaMapHelper.mapToSTValue(value, element);
                    out.getEntryRelationshipArray(counter).setObservation((POCDMT000040Observation) ot);
                }

            }
            else if(dataType.equalsIgnoreCase(  "DATE")){
                if(name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()){
                    var element = out.getEntryRelationshipArray(counter).getObservation().getValueArray(0);
                    XmlCursor cursor = element.newCursor();
                    cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "TS");
                    cursor.setAttributeText(new QName("name"), value);  // This is an assumption based on the original code

                    if(name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()){
                        String newValue = cdaMapHelper.mapToTsType(in.getAnswerTxt()).toString();
                        cursor.setAttributeText(new QName("name"), value);
                        cursor.setTextValue(newValue);
                    }
                    else {
                        element = out.getEntryRelationshipArray(counter).getObservation().getValueArray(0);
                        cursor = element.newCursor();
                        cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "ST");

                        if(name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()) {
                            cursor.setTextValue(CDATA + in.getAnswerTxt() + CDATA);
                        }
                    }

                    out.getEntryRelationshipArray(counter).getObservation().setValueArray(0, element);
                    cursor.dispose();
                }
            }
            if(name.equals(COL_QUES_IDENTIFIER) && !in.getQuestionIdentifier().isEmpty()){
                if(in.getQuestionIdentifier().equals(value)){
                    //IGNORE
                }else{
                    if(questionSeq.equals(CHANGE)){

                    }else{
                        counter =  counter+1;
                    }

                    questionSeq =value;

                    out.getEntryRelationshipArray(counter).getObservation().setClassCode("OBS");
                    out.getEntryRelationshipArray(counter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
                    out.getEntryRelationshipArray(counter).getObservation().getCode().setCode(in.getQuestionIdentifier());
                }
            }
            else if(name.equals(COL_QUES_CODE_SYSTEM_CD) && !in.getQuesCodeSystemCd().isEmpty()){
                out.getEntryRelationshipArray(counter).getObservation().getCode().setCodeSystem(in.getQuesCodeSystemCd());
            }
            else if(name.equals(COL_QUES_CODE_SYSTEM_DESC_TXT) && !in.getQuesCodeSystemDescTxt().isEmpty()){
                out.getEntryRelationshipArray(counter).getObservation().getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());
            }
            else if(name.equals(COL_QUES_DISPLAY_TXT) && !in.getQuesDisplayTxt().isEmpty()){
                out.getEntryRelationshipArray(counter).getObservation().getCode().setDisplayName(in.getQuesDisplayTxt());
            }

        }

        model.setComponent(out);
        model.setCounter(counter);
        model.setQuestionSeq(questionSeq);
        return model;
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
                out.getParticipantRole().getIdArray(0).setRoot(ID_ARR_ROOT);
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
                cursor.setTextValue(CDATA + in.getPlaNameTxt() + CDATA);
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

                String questionCode= this.cdaMapHelper.mapToQuestionId("PLA_TYPE_CD");
                out.getParticipantRole().addNewCode();
                out.getParticipantRole().setCode(this.cdaMapHelper.mapToCEAnswerType(in.getPlaTypeCd(), questionCode));
            }
            if (name.equals("plaCommentTxt") && value != null&& !in.getPlaCommentTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewPlayingEntity().addNewDesc();
                } else {
                    out.getParticipantRole().addNewPlayingEntity().addNewDesc();
                }

                out.getParticipantRole().getPlayingEntity().getDesc().set(cdaMapHelper.mapToCData(in.getPlaCommentTxt()));
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

                out.getParticipantRole().getIdArray(c).setRoot(ID_ARR_ROOT);
                out.getParticipantRole().getIdArray(c).setExtension(in.getPlaIdQuickCode());
                out.getParticipantRole().getIdArray(c).setAssigningAuthorityName("LR_QEC");
            }

        }



        int isAddressPopulated= 0;
        if(!streetAddress1.isEmpty() ){
            AdxpStreetAddressLine val = AdxpStreetAddressLine.Factory.newInstance();
            XmlCursor cursor = val.newCursor();
            cursor.setTextValue(CDATA + streetAddress1 + CDATA);
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
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(1).set(cdaMapHelper.mapToCData(streetAddress2));
            }
            else {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0,  AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(cdaMapHelper.mapToCData(streetAddress2));
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
            out.getParticipantRole().getAddrArray(0).getCityArray(0).set(cdaMapHelper.mapToCData(city));

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
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(cdaMapHelper.mapToCData(state  ));
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
            out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(cdaMapHelper.mapToCData(county));
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
            out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(cdaMapHelper.mapToCData(zip   ));
            isAddressPopulated=1;
        }
        if(!country.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            AdxpCountry val = AdxpCountry.Factory.newInstance();
            val.set(cdaMapHelper.mapToCData(country));
            out.getParticipantRole().getAddrArray(0).addNewCountry();

            out.getParticipantRole().getAddrArray(0).setCountryArray(0,  AdxpCountry.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(cdaMapHelper.mapToCData(country));
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
            out.getParticipantRole().getAddrArray(0).getCensusTractArray(0).set(cdaMapHelper.mapToCData(censusTract));
        }
        if(isAddressPopulated>0){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray()[0].setUse(Arrays.asList("WP"));
            if(!postalAsOfDate.isEmpty()){
                // TODO:
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().addr[0];
                // mapToUsableTSElement(postalAsOfDate, element, USESABLE_PERIOD);
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
            out.getParticipantRole().getAddrArray(0).getAdditionalLocatorArray(0).set(cdaMapHelper.mapToCData(placeAddressComments));
        }

        if(!workPhone.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(Arrays.asList("WP"));
            int countryphoneCodeSize= workCountryCode.length();
            if(countryphoneCodeSize>0){
                workPhone = workCountryCode+"-"+ workPhone;
            }

            int phoneExtnSize = workExtn.length();
            if(phoneExtnSize>0){
                workPhone=workPhone+ EXTN_STR+ workExtn;
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(workPhone);

            if(!teleAsOfDate.isEmpty()){
                // TODO:
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().telecom[teleCounter];
                // mapToUsableTSElement(teleAsOfDate, element, USESABLE_PERIOD);
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
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(MAIL_TO+workEmail);
            if(!teleAsOfDate.isEmpty()){
                // TODO:
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().telecom[teleCounter];
                // mapToUsableTSElement(teleAsOfDate, element, USESABLE_PERIOD);
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
                // TODO:
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().telecom[teleCounter];
                // mapToUsableTSElement(teleAsOfDate, element, USESABLE_PERIOD);
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
                out.getParticipantRole().getIdArray(0).setRoot(ID_ARR_ROOT);
                out.getParticipantRole().getIdArray(0).setExtension(in.getOrgLocalId());
                out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
            }
            else if(name.equals("orgNameTxt") && in.getOrgNameTxt() != null && !in.getOrgNameTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewPlayingEntity();
                } else {
                    out.getParticipantRole().addNewPlayingEntity();
                }
                var val = cdaMapHelper.mapToCData(in.getOrgNameTxt());
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
                county = this.cdaMapHelper.mapToAddressType( in.getOrgAddrCountyCd(), county);
            }
            else if (name.equals("orgAddrStateCd") && in.getOrgAddrStateCd() != null &&  !in.getOrgAddrStateCd().isEmpty()){
                state= this.cdaMapHelper.mapToAddressType( in.getOrgAddrStateCd(), state);
            }
            else if(name.equals("orgAddrZipCodeTxt") && in.getOrgAddrZipCodeTxt() != null && !in.getOrgAddrZipCodeTxt().isEmpty()){
                zip = in.getOrgAddrZipCodeTxt();
            }
            else if(name.equals("orgAddrCountryCd") && in.getOrgAddrCountryCd() != null && !in.getOrgAddrCountryCd().isEmpty()){
                country = this.cdaMapHelper.mapToAddressType( in.getOrgAddrCountryCd(), country);
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

                out.getParticipantRole().getIdArray(1).setRoot(ID_ARR_ROOT);
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
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(cdaMapHelper.mapToCData(streetAddress1));

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
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(1).set(cdaMapHelper.mapToCData(streetAddress2));
            }
            else {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(cdaMapHelper.mapToCData(streetAddress2));
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
            out.getParticipantRole().getAddrArray(0).getCityArray(0).set(cdaMapHelper.mapToCData(city));

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
            out.getParticipantRole().getAddrArray(0).getStateArray(0).set(cdaMapHelper.mapToCData(state));

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
            out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(cdaMapHelper.mapToCData(county));

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
            out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(cdaMapHelper.mapToCData(zip));
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
            out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(cdaMapHelper.mapToCData(zip));

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
                phone=phone+ EXTN_STR+ extn;
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
                county = this.cdaMapHelper.mapToAddressType(in.getPrvAddrCountyCd(), county);
            }
            else if(name.equals("prvAddrStateCd") && in.getPrvAddrStateCd() != null  && !in.getPrvAddrStateCd().isEmpty()) {
                state = this.cdaMapHelper.mapToAddressType(in.getPrvAddrStateCd(), state);
            }
            else if(name.equals("prvAddrZipCodeTxt") && in.getPrvAddrZipCodeTxt() != null && !in.getPrvAddrZipCodeTxt().isEmpty()) {
                zip = in.getPrvAddrZipCodeTxt();
            }
            else if(name.equals("prvAddrCountryCd") && in.getPrvAddrCountryCd() != null && !in.getPrvAddrCountryCd().isEmpty()) {
                country = this.cdaMapHelper.mapToAddressType(in.getPrvAddrCountryCd(), country);
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
            var mapVal = cdaMapHelper.mapToCData(firstName);
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
            var mapVal = cdaMapHelper.mapToCData(lastName);
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
            var mapVal = cdaMapHelper.mapToCData(prefix);
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
            var mapVal = cdaMapHelper.mapToCData(suffix);
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
            var mapVal = cdaMapHelper.mapToCData(address1);
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
            var mapVal = cdaMapHelper.mapToCData(address2);
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
            var mapVal = cdaMapHelper.mapToCData(city);
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
            var mapVal = cdaMapHelper.mapToCData(county);
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
            var mapVal = cdaMapHelper.mapToCData(zip);
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
            var mapVal = cdaMapHelper.mapToCData(state);
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
            var mapVal = cdaMapHelper.mapToCData(country);
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
                telephone=telephone+ EXTN_STR+ extn;
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
        String dataType="";
        if (!in.getDataType().isEmpty()) {
            dataType = in.getDataType();
        }



        if (!in.getAnswerXmlTxt().isEmpty()) {
            ANY any = ANY.Factory.parse(in.getAnswerXmlTxt());
            out.set(any);
        }
        return out;
    }




}
