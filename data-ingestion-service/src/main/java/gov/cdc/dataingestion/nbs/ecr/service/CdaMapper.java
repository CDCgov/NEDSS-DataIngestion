package gov.cdc.dataingestion.nbs.ecr.service;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.container.CdaContainerComp;
import gov.cdc.dataingestion.nbs.ecr.service.helper.*;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.*;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;

@Service
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class CdaMapper implements ICdaMapper {
    
    private final ICdaLookUpService ecrLookUpService;


    private final ICdaMapHelper cdaMapHelper;
    private final ICdaPatientMappingHelper patientMappingHelper;
    private final ICdaCaseMappingHelper caseMappingHelper;
    private final ICdaXmlAnswerMappingHelper xmlAnswerMappingHelper;
    private final ICdaProviderMappingHelper providerMappingHelper;
    private final ICdaOrgMappingHelper orgMappingHelper;
    private final ICdaPlaceMappingHelper placeMappingHelper;
    private final ICdaInterviewMappingHelper interviewMappingHelper;
    private final ICdaTreatmentMappingHelper treatmentMappingHelper;

    @Autowired
    public CdaMapper(ICdaLookUpService ecrLookUpService) {
        this.ecrLookUpService = ecrLookUpService;
        this.cdaMapHelper = new CdaMapHelper(this.ecrLookUpService);
        this.patientMappingHelper = new CdaPatientMappingHelper(this.cdaMapHelper);
        this.caseMappingHelper = new CdaCaseMappingHelper(this.cdaMapHelper);
        this.xmlAnswerMappingHelper = new CdaXmlAnswerMappingHelper();
        this.providerMappingHelper = new CdaProviderMappingHelper(this.cdaMapHelper);
        this.orgMappingHelper = new CdaOrgMappingHelper(this.cdaMapHelper);
        this.placeMappingHelper = new CdaPlaceMappingHelper(this.cdaMapHelper);
        this.interviewMappingHelper = new CdaInterviewMappingHelper(this.cdaMapHelper);
        this.treatmentMappingHelper = new CdaTreatmentMappingHelper(this.cdaMapHelper);
    }

    public String tranformSelectedEcrToCDAXml(EcrSelectedRecord input) throws EcrCdaXmlException {
        String inv168 = "";

        //region DOCUMENT INITIATION
        ClinicalDocumentDocument1 rootDocument = ClinicalDocumentDocument1.Factory.newInstance();
        POCDMT000040ClinicalDocument1 clinicalDocument = POCDMT000040ClinicalDocument1.Factory.newInstance();

        var containerModel = mapParentContainer(clinicalDocument,
                input, inv168);
        clinicalDocument = containerModel.getClinicalDocument();
        inv168 = containerModel.getInv168();

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

        var pat =  this.patientMappingHelper.mapToPatient(input, clinicalDocument, patientComponentCounter, inv168);
        clinicalDocument = pat.getClinicalDocument();
        inv168 = pat.getInv168();

        var ecrCase = caseMappingHelper.mapToCaseTop(input, clinicalDocument, componentCounter, clinicalCounter,
        componentCaseCounter, inv168);
        clinicalDocument = ecrCase.getClinicalDocument();
        componentCounter = ecrCase.getComponentCounter();
        inv168 = ecrCase.getInv168();

        var ecrXmlAnswer = xmlAnswerMappingHelper.mapToXmlAnswerTop(input,
                clinicalDocument, componentCounter);
        clinicalDocument = ecrXmlAnswer.getClinicalDocument();
        componentCounter = ecrXmlAnswer.getComponentCounter();

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

        var ecrProvider = this.providerMappingHelper.mapToProviderTop(input, clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection(),
                inv168, performerComponentCounter, componentCounter,
                 performerSectionCounter);

        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).setSection(ecrProvider.getClinicalSection());
        performerComponentCounter = ecrProvider.getPerformerComponentCounter();
        componentCounter = ecrProvider.getComponentCounter();
        performerSectionCounter = ecrProvider.getPerformerSectionCounter();

        var ecrOrganization = this.orgMappingHelper.mapToOrganizationTop(input, clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection(),
                performerComponentCounter, componentCounter, performerSectionCounter);
        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).setSection(ecrProvider.getClinicalSection());
        performerComponentCounter = ecrOrganization.getPerformerComponentCounter();
        componentCounter = ecrOrganization.getComponentCounter();
        performerSectionCounter = ecrOrganization.getPerformerSectionCounter();

        POCDMT000040Section interestedPartyComp = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection();
        var ecrPlace = this.placeMappingHelper.mapToPlaceTop(input, performerComponentCounter,
                componentCounter, performerSectionCounter, interestedPartyComp);
        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).setSection(ecrPlace.getSection());
        componentCounter = ecrPlace.getComponentCounter();

        var ecrInterview = this.interviewMappingHelper.mapToInterviewTop(input, clinicalDocument, interviewCounter, componentCounter);
        clinicalDocument = ecrInterview.getClinicalDocument();
        componentCounter = ecrInterview.getComponentCounter();

        var ecrTreatment = this.treatmentMappingHelper.mapToTreatmentTop(input, clinicalDocument,
                treatmentCounter, componentCounter, treatmentSectionCounter);
        clinicalDocument = ecrTreatment.getClinicalDocument();

        mapCustodian(clinicalDocument);

        mapAuthor(clinicalDocument);

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
        //endregion

        return convertXmlToString(rootDocument);

    }

    private CdaContainerComp mapParentContainer(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                EcrSelectedRecord input, String inv168) throws EcrCdaXmlException {

        Integer versionCtrNbr = null;

        //region CONTAINER COMPONENT CREATION
        CS[] realmCodeArray = { CS.Factory.newInstance()};
        clinicalDocument.setRealmCodeArray(realmCodeArray);
        clinicalDocument.getRealmCodeArray(0).setCode("US");

        clinicalDocument.setTypeId(POCDMT000040InfrastructureRootTypeId.Factory.newInstance());
        clinicalDocument.getTypeId().setRoot("2.16.840.1.113883.1.3");
        clinicalDocument.getTypeId().setExtension("POCD_HD000040");

        if (input.getMsgContainer().getInvLocalId() != null && !input.getMsgContainer().getInvLocalId().isEmpty()) {
            clinicalDocument.setId(II.Factory.newInstance());
            clinicalDocument.getId().setRoot(ROOT_ID);
            clinicalDocument.getId().setExtension(input.getMsgContainer().getInvLocalId());
            clinicalDocument.getId().setAssigningAuthorityName("LR");
            inv168 = input.getMsgContainer().getInvLocalId();
        }

        if (input.getMsgContainer().getOngoingCase() != null && !input.getMsgContainer().getOngoingCase().isEmpty()) {
            clinicalDocument.setSetId(II.Factory.newInstance());
            clinicalDocument.getSetId().setExtension("ONGOING_CASE");
            clinicalDocument.getSetId().setDisplayable(input.getMsgContainer().getOngoingCase().equalsIgnoreCase("yes"));
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

        clinicalDocument.getTitle().set(cdaMapHelper.mapToPCData("Public Health Case Report - Data from Legacy System to CDA"));

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

        var model = new CdaContainerComp();
        model.setClinicalDocument(clinicalDocument);
        model.setInv168(inv168);
        return model;
    }

    private void mapCustodian(POCDMT000040ClinicalDocument1 clinicalDocument) throws EcrCdaXmlException {
        int k =0;
        String custodianValue;
        clinicalDocument.addNewCustodian().addNewAssignedCustodian().addNewRepresentedCustodianOrganization().addNewId();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().addNewAddr();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().addNewTelecom();


        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getIdArray(0).setExtension(mapToTranslatedValue("CUS101"));
        custodianValue = mapToTranslatedValue("CUS102");

        var element = clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization();
        element = this.cdaMapHelper.mapToElementValue(custodianValue, element, "name");
        clinicalDocument.getCustodian().getAssignedCustodian().setRepresentedCustodianOrganization(element);

        custodianValue = mapToTranslatedValue("CUS103");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewStreetAddressLine();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(cdaMapHelper.mapToPCData(custodianValue));
        k = k+1;
        custodianValue = mapToTranslatedValue("CUS104");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewStreetAddressLine();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(cdaMapHelper.mapToPCData(custodianValue));

        k = 0;
        custodianValue = mapToTranslatedValue("CUS105");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewCity();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCityArray(k).set(cdaMapHelper.mapToPCData(custodianValue));

        custodianValue = mapToTranslatedValue("CUS106");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewState();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStateArray(k).set(cdaMapHelper.mapToPCData(custodianValue));

        custodianValue = mapToTranslatedValue("CUS107");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewPostalCode();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getPostalCodeArray(k).set(cdaMapHelper.mapToPCData(custodianValue));

        custodianValue = mapToTranslatedValue("CUS108");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewCountry();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCountryArray(k).set(cdaMapHelper.mapToPCData(custodianValue));

        custodianValue = mapToTranslatedValue("CUS109");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getTelecom().setValue(custodianValue);

    }

    private void mapAuthor(POCDMT000040ClinicalDocument1 clinicalDocument) throws EcrCdaXmlException {
        String value;
        clinicalDocument.addNewAuthor().addNewAssignedAuthor();
        clinicalDocument.getAuthorArray(0).addNewTime();
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().addNewId();

        value = mapToTranslatedValue("AUT101");
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getIdArray(0).setRoot(value);

        clinicalDocument.getAuthorArray(0).getAssignedAuthor().addNewAssignedPerson().addNewName();
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getAssignedPerson().getNameArray(0).addNewFamily();
        value = mapToTranslatedValue("AUT102");
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getAssignedPerson().getNameArray(0).getFamilyArray(0).set(cdaMapHelper.mapToPCData(value));

        OffsetDateTime now = OffsetDateTime.now();
        String formattedDateTime = formatDateTime(now);

        clinicalDocument.getAuthorArray(0).getTime().setValue(formattedDateTime);
    }

    private static String formatDateTime(OffsetDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return dateTime.format(formatter);
    }

    private String convertXmlToString(ClinicalDocumentDocument1 clinicalDocument) {
            XmlOptions options = new XmlOptions();
            // Use a default namespace instead of a prefixed one (like urn:)
            options.setUseDefaultNamespace();

            String xmlOutput = clinicalDocument.xmlText(options);

            xmlOutput = xmlOutput.replaceAll("<STRING[^>]*>([^<]+)</STRING>", "$1");// NOSONAR // remove string tag
            xmlOutput = xmlOutput.replaceAll("\\[CDATA\\](.*?)\\[CDATA\\]", "<![CDATA[$1]]>");// NOSONAR // replace CDATA with real CDATA
            xmlOutput = xmlOutput.replaceAll("<CDATA[^>]*>(.*?)</CDATA>", "<![CDATA[$1]]>");// NOSONAR // replace CDATA with real CDATA
            xmlOutput = xmlOutput.replaceAll("<(\\w+)></\\1>", "");// NOSONAR // remove empty <tag></tag>
            xmlOutput = xmlOutput.replaceAll("<STUD xmlns=\"\">STUD</STUD>", "");// NOSONAR // remove STUD tag
            xmlOutput = xmlOutput.replaceAll("<stud xmlns=\"\">stud</stud>", "");// NOSONAR // remove STUD tag
            xmlOutput = xmlOutput.replaceAll("(?m)^\\s*$[\n\r]{1,}", "");// NOSONAR // remove new line

            xmlOutput = xmlOutput.replaceAll("sdtcxmlnamespaceholder=\""+ XML_NAME_SPACE_HOLDER +"\"", "xmlns:sdtcxmlnamespaceholder=\""+XML_NAME_SPACE_HOLDER+"\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("sdt=\"urn:hl7-org:sdtc\"", "xmlns:sdt=\"urn:hl7-org:sdtc\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xmlns:xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xmlns:xmlns", "xmlns");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xmlns:urn=\"urn:hl7-org:v3\"", "");// NOSONAR

            xmlOutput = xmlOutput.replaceAll("schemaLocation=\""+ XML_NAME_SPACE_HOLDER +" CDA_SDTC.xsd\"", "xsi:schemaLocation=\""+XML_NAME_SPACE_HOLDER +" CDA_SDTC.xsd\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("<section xmlns=\"\">", "<section>");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("<xmlns=\"\">", "");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xmlns=\"\"", "");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xsi:type=\"urn:CE\"", "xsi:type=\"CE\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xsi:type=\"urn:ST\"", "xsi:type=\"ST\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xsi:type=\"urn:II\"", "xsi:type=\"II\"");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("xsi:type=\"urn:TS\"", "xsi:type=\"TS\"");// NOSONAR

            xmlOutput = xmlOutput.replaceAll("\\^NOT_MAPPED", "");// NOSONAR
            xmlOutput = xmlOutput.replaceAll("NOT_MAPPED","");// NOSONAR

            xmlOutput = "<?xml version=\"1.0\"?>\n" + xmlOutput;
            return xmlOutput;
    }

    private String mapToTranslatedValue(String input) throws EcrCdaXmlException {
        var res = ecrLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", input);
        if (res != null && !res.getSampleValue().isEmpty()) {
            return res.getSampleValue();
        }
        else {
            return NOT_FOUND_VALUE;
        }
    }
}
