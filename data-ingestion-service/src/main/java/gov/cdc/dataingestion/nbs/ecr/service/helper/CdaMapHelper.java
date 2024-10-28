package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.shares.*;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.lookup.PhdcAnswerDao;
import gov.cdc.dataingestion.nbs.repository.model.dao.lookup.QuestionIdentifierMapDao;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseAnswerDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgOrganizationDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgProviderDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import javax.xml.namespace.QName;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.getStringsBeforePipe;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class CdaMapHelper implements ICdaMapHelper {

    private final ICdaLookUpService ecrLookUpService;

    public CdaMapHelper(ICdaLookUpService ecrLookUpService) {
        this.ecrLookUpService = ecrLookUpService;
    }

    public POCDMT000040Section mapOrgPlaceProviderActCommonField(POCDMT000040Section clinicalDocument,
                                                   int performerSectionCounter,
                                                   POCDMT000040Participant2 output) {
        clinicalDocument.getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

        clinicalDocument.getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
        clinicalDocument.getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
        clinicalDocument.getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);

        if (clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode() == null){
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewCode();
        }

        return clinicalDocument;
    }

    public OrgPlaceDocCommonField mapOrgPlaceDocCommonField(POCDMT000040Section clinicalDocument,
                                     int performerComponentCounter) throws EcrCdaXmlException {
        if (performerComponentCounter < 1) {
            clinicalDocument.getCode().setCode(CODE);
            clinicalDocument.getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
            clinicalDocument.getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
            clinicalDocument.getCode().setDisplayName(CODE_DISPLAY_NAME);
            clinicalDocument.getTitle().set(mapToPCData(CLINICAL_TITLE));
        }

        var model = mapActParticipantArray(clinicalDocument);
        clinicalDocument = model.getSection();
        int performerSectionCounter = model.getC(); // NOSONAR
        POCDMT000040Participant2 out = model.getOut();

        OrgPlaceDocCommonField orgPlace = new OrgPlaceDocCommonField();
        orgPlace.setPerformerSectionCounter(performerSectionCounter);
        orgPlace.setOut(out);
        orgPlace.setClinicalDocument(clinicalDocument);
        return orgPlace;
    }

    public XmlObject mapToCData(String data) throws EcrCdaXmlException {
        try {
            String escapedString = data.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");

            return XmlObject.Factory.parse("<CDATA>"+escapedString+"</CDATA>");
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }

    public XmlObject mapToPCData(String data) throws EcrCdaXmlException {
        try {
            return XmlObject.Factory.parse("<STRING>"+data+"</STRING>");
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }

    public ActParticipantArray mapActParticipantArray(POCDMT000040Section section) {
        int c;
        if ( section.getEntryArray().length == 0) {
            section.addNewEntry();
            c = 0;
        }
        else {
            c = section.getEntryArray().length;
            section.addNewEntry();
        }

        if (section.getEntryArray(c).getAct() == null) {
            section.getEntryArray(c).addNewAct();
            section.getEntryArray(c).getAct().addNewParticipant();
        } else {
            section.getEntryArray(c).getAct().addNewParticipant();
        }

        POCDMT000040Participant2 out = section.getEntryArray(c).getAct().getParticipantArray(0);

        ActParticipantArray model = new ActParticipantArray();
        model.setSection(section);
        model.setOut(out);
        model.setC(c);
        return model;
    }

    public MapParticipantRole mapToParticipantRoleCheck(
            POCDMT000040SubstanceAdministration output) {
        int c = 0;
        if (output.getParticipantArray().length == 0) {
            output.addNewParticipant().addNewParticipantRole().addNewId();
        } else {
            c = output.getParticipantArray().length;
            output.addNewParticipant().addNewParticipantRole().addNewId();
        }

        MapParticipantRole model = new MapParticipantRole();
        model.setOutput(output);
        model.setParticipant2(output.getParticipantArray(c));
        model.setC(c);
        return model;
    }

    public ANY mapMultiSelectDateMapXmlElement(ANY element, String value, EcrMsgCaseAnswerDto in) throws EcrCdaXmlException {
        XmlCursor cursor = element.newCursor();
        cursor.toFirstChild();
        cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "TS");
        cursor.setAttributeText(new QName("", value), null);
        String newValue = mapToTsType(in.getAnswerTxt()).toString();
        cursor.setAttributeText(new QName("", value), newValue);
        cursor.dispose();
        return element;
    }

    public MapStructure mapToStructureBodyCheck(POCDMT000040ClinicalDocument1 clinicalDocument) {
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

        MapStructure mapStruct = new MapStructure();
        mapStruct.setClinicalDocument(clinicalDocument);
        mapStruct.setC(c);
        return mapStruct;
    }

    public XmlObject mapToUsableTSElement(String data, XmlObject output, String name) throws EcrCdaXmlException {
        XmlCursor cursor = output.newCursor();
        cursor.toFirstChild();  // Move to the root element

        cursor.beginElement(name);
        cursor.insertAttributeWithValue("type", "IVL_TS");
        cursor.toFirstChild();  // Move inside childName
        cursor.beginElement("low");
        cursor.insertNamespace("", XML_NAME_SPACE_HOLDER);
        cursor.insertAttributeWithValue(VALUE_NAME, mapToTsType(data).getValue());
        cursor.dispose();
        return output;
    }

    public TS mapToTsType(String data) throws EcrCdaXmlException {
        try {
            TS ts = TS.Factory.newInstance();
            String result;
            boolean checkerCode = data.contains("/");
            boolean checkerCodeDash = data.contains("-");
            boolean checkerCodeDateOnly = checkerCode && data.length() == 10;
            boolean checkerDateOnly = checkerCodeDash && data.length() == 10;
            if (!checkerCode && !checkerCodeDash) {
                result = data;
            }
            else if (checkerDateOnly) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = inputFormat.parse(data);
                result = outputFormat.format(date);
            }
            else if (checkerCodeDash) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = inputFormat.parse(data);
                result = outputFormat.format(date);
            }
            else if (checkerCodeDateOnly) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = inputFormat.parse(data);
                result = outputFormat.format(date);
            }
            else {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = inputFormat.parse(data);
                result = outputFormat.format(date);
            }

            ts.setValue(result);
            return ts;
        } catch (Exception e) {
            throw  new EcrCdaXmlException(e.getMessage());
        }

    }

    public String mapToQuestionId(String data) throws EcrCdaXmlException {
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

    public CE mapToCEAnswerType(String data, String questionCode) throws EcrCdaXmlException {
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

    public String mapToAddressType(String data, String questionCode) throws EcrCdaXmlException {
        String output = "";
        if (data == null) {
            data = "";
        }

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

    public PhdcAnswerDao mapToCodedAnswer(String data, String questionCode) throws EcrCdaXmlException {
        PhdcAnswerDao model = new PhdcAnswerDao();
        String code;
        String transCodeSystem;
        String transCodeSystemName;
        String transDisplayName;
        String codeSystem;
        String codeSystemName;
        String displayName;

        // RhapsodyTableLookup(output, tableName, resultColumnName, defaultValue, queryColumn1, queryValue1, queryColumn2, queryValue2, ...)
        var phdcAnswer = ecrLookUpService.fetchPhdcAnswerByCriteriaForTranslationCode(questionCode, data);
        if (phdcAnswer != null) {
            code = phdcAnswer.getAnsToCode();
            transCodeSystem = phdcAnswer.getAnsFromCodeSystemCd();
            transCodeSystemName = phdcAnswer.getAnsFromCodeSystemCd();
            transDisplayName = phdcAnswer.getAnsFromDisplayNm();
            codeSystem = phdcAnswer.getAnsToCodeSystemCd();
            codeSystemName = phdcAnswer.getAnsToCodeSystemDescTxt();
            displayName = phdcAnswer.getAnsToDisplayNm();
        }
        else {
            transCodeSystem = ID_ROOT;
            codeSystem = ID_ROOT;
            code = NOT_MAPPED_VALUE;
            transCodeSystemName = NOT_MAPPED_VALUE;
            transDisplayName = NOT_MAPPED_VALUE;
            codeSystemName =NOT_MAPPED_VALUE;
            displayName = NOT_MAPPED_VALUE;
        }

        if (code.equalsIgnoreCase(NOT_MAPPED_VALUE)) {
            code = data;
        }

        if (code.equalsIgnoreCase("NULL") || code.isEmpty()) {
            code = data;
            codeSystem = transCodeSystem;
            codeSystemName = transCodeSystemName;
            displayName = transDisplayName;
        }

        model.setCode(code);
        model.setCodeSystem(codeSystem);
        model.setCodeSystemName(codeSystemName);
        model.setDisplayName(displayName);
        model.setTransCode(data);
        model.setTransCodeSystem(transCodeSystem);
        model.setTransCodeSystemName(transCodeSystemName);
        model.setTransDisplayName(transDisplayName);

        return model;
    }

    public POCDMT000040CustodianOrganization mapToElementValue(String data, POCDMT000040CustodianOrganization output, String name) {
        XmlCursor cursor = output.newCursor();

        // Counter is set to 4 here to move it to the next element of id as the batch requires
        // name to be the second element in the representedCustodianOrganization section
        int counter = 4;
        while (counter > 0) {
            cursor.toNextToken();
            counter--;
        }
        cursor.beginElement(name, XML_NAME_SPACE_HOLDER);
        cursor.insertChars(data);
        cursor.dispose();
        return output;
    }


    public String getCurrentUtcDateTimeInCdaFormat() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return utcNow.format(formatter);
    }

    /*
     * TEST NEEDED
     * */
    public POCDMT000040Observation mapToObservation(String questionCode, String data,
        POCDMT000040Observation observation) throws EcrCdaXmlException {

            observation.setClassCode("OBS");
            observation.setMoodCode(XActMoodDocumentObservation.EVN);
            String defaultQuestionIdentifier = "";

            PhdcQuestionLookUpDto questionLup = new PhdcQuestionLookUpDto();
            questionLup.setQuestionIdentifier(NOT_FOUND_VALUE);
            questionLup.setQuesCodeSystemCd(NOT_FOUND_VALUE);
            questionLup.setQuesCodeSystemDescTxt(NOT_FOUND_VALUE);
            questionLup.setQuesDisplayName(NOT_FOUND_VALUE);
            questionLup.setDataType(NOT_FOUND_VALUE);
            var result = ecrLookUpService.fetchPhdcQuestionByCriteria(questionCode);
            if (result != null) {

                if (observation.getCode() == null) {
                    observation.addNewCode();
                }
                observation.getCode().setCode(result.getQuestionIdentifier());
                observation.getCode().setCodeSystem(result.getQuesCodeSystemCd());
                observation.getCode().setCodeSystemName(result.getQuesCodeSystemDescTxt());
                observation.getCode().setDisplayName(result.getQuesDisplayName());

                //region DB LOOKUP
                var param = new Observation();
                param.setQuestionLup(questionLup);
                param.setDefaultQuestionIdentifier(defaultQuestionIdentifier);
                param.setQuestionCode(questionCode);
                var obs = mapToObservationLookupCheck(result,
                        param);

                defaultQuestionIdentifier = obs.getDefaultQuestionIdentifier();

                //endregion

                if (!result.getDataType().isEmpty()) {
                    if (result.getDataType().equalsIgnoreCase(DATA_TYPE_CODE)) {
                         mapToObservationDateTypeCoded(
                                 observation,
                                 data,
                                 result);
                    }
                    else {
                        mapToObservationDataTypeNotCoded(result,
                                 observation,
                                 data,
                                 defaultQuestionIdentifier);
                    }
                }
            } else {

                if (observation.getCode() == null) {
                    observation.addNewCode();
                }
                observation.getCode().setCode(data + questionCode);
                observation.getCode().setCodeSystem(CODE_NODE_MAPPED_VALUE);
                observation.getCode().setCodeSystemName(CODE_NODE_MAPPED_VALUE);
                observation.getCode().setDisplayName(CODE_NODE_MAPPED_VALUE);
            }
            return observation;

    }

    private void mapToObservationDateTypeCoded(
            POCDMT000040Observation observation,
            String data,
            PhdcQuestionLookUpDto result) throws EcrCdaXmlException {
        var dataList = getStringsBeforePipe(data);
        StringBuilder dataStr = new StringBuilder();
        for (String s : dataList) {
            dataStr.append(" ").append(s);

        }
        dataStr = new StringBuilder(dataStr.toString().trim());
        var value = mapToCEAnswerType(String.valueOf(dataStr), result.getQuestionIdentifier());
        observation.addNewValue();
        observation.setValueArray(0, value);
    }

    private  void mapToObservationDataTypeNotCoded(PhdcQuestionLookUpDto result,
                                                   POCDMT000040Observation observation,
                                                   String data,
                                                   String defaultQuestionIdentifier) throws EcrCdaXmlException {
        try {
            if (result.getDataType().equalsIgnoreCase("PART")) {
                // CHECK mapToObservation from ori 47
                if (observation.getValueArray().length == 0) {
                    observation.addNewValue();
                }

                if (observation.getCode() == null) {
                    observation.addNewCode();
                }

                II id = II.Factory.newInstance();

                var val = ecrLookUpService.fetchPhdcQuestionByCriteriaWithColumn("Question_Identifier", defaultQuestionIdentifier);
                id.setRoot(val.getQuesCodeSystemCd());
                id.setExtension(data);
                observation.setValueArray(0, id); // THIS
            }
            else if (result.getDataType().equalsIgnoreCase("DATE")) {
                var ts = mapToTsType(data);
                observation.addNewValue();
                observation.setValueArray(0, ts);
            }
            else {
                observation.addNewValue();

                ST st = ST.Factory.newInstance();
                XmlCursor cursor = st.newCursor();
                cursor.toNextToken();
                cursor.insertChars(CDATA + data + CDATA);
                cursor.dispose();
                observation.setValueArray(0, st);

            }
        } catch (Exception e) {
            throw  new EcrCdaXmlException(e.getMessage());
        }

    }

    private Observation mapToObservationLookupCheck(PhdcQuestionLookUpDto result,
                                             Observation param) throws EcrCdaXmlException {

        PhdcQuestionLookUpDto questionLup = param.getQuestionLup();
        String defaultQuestionIdentifier = param.getDefaultQuestionIdentifier();
        String questionCode = param.getQuestionCode();

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
        map.setDynamicQuestionIdentifier(NOT_FOUND_VALUE);
        QuestionIdentifierMapDto identifierMap = ecrLookUpService.fetchQuestionIdentifierMapByCriteriaByCriteria("Question_Identifier", questionCode);
        if(identifierMap != null && !identifierMap.getDynamicQuestionIdentifier().isEmpty()) {
            map.setDynamicQuestionIdentifier(identifierMap.getDynamicQuestionIdentifier());
        }

        if(map.getDynamicQuestionIdentifier().equalsIgnoreCase("STANDARD")
                || map.getDynamicQuestionIdentifier().equalsIgnoreCase(NOT_FOUND_VALUE)) {
            defaultQuestionIdentifier = questionCode;
        }
        param.setDefaultQuestionIdentifier(defaultQuestionIdentifier);

        return param;

    }



    public PhdcQuestionLookUpDto mapToCodedQuestionType(String questionIdentifier) throws EcrCdaXmlException {
        PhdcQuestionLookUpDto dto = new PhdcQuestionLookUpDto();
        dto.setQuesCodeSystemCd(NOT_FOUND_VALUE);
        dto.setQuesCodeSystemDescTxt(NOT_FOUND_VALUE);
        dto.setQuesDisplayName(NOT_FOUND_VALUE);
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

    public CE mapToCEQuestionType(String questionCode, CE output) throws EcrCdaXmlException {
        var ot = mapToCodedQuestionType(questionCode);
        output.setCodeSystem(ot.getQuesCodeSystemCd());
        output.setCodeSystemName(ot.getQuesCodeSystemDescTxt());
        output.setDisplayName(ot.getQuesDisplayName());
        output.setCode(questionCode);

        return output;
    }

    public XmlObject mapToSTValue(String input, XmlObject output) {
        XmlCursor cursor = output.newCursor();

        if (cursor.toChild(new QName(VALUE_NAME))) {
            cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "ST");
            cursor.toNextToken();
            cursor.insertChars(CDATA + input + CDATA);
        }

        cursor.dispose();
        return output;
    }

    public XmlObject mapToObservationPlace(String in, XmlObject out) {

        II id = II.Factory.newInstance();
        id.setRoot("2.3.3.3.322.23.34");
        id.setExtension(in);

//        XmlCursor cursor = out.newCursor();
//        cursor.toFirstChild();
//        cursor.setAttributeText(new QName(NAME_SPACE_URL, "xsi:type"), "II");
//        cursor.setAttributeText(new QName("", "root"), "2.3.3.3.322.23.34");
//        cursor.setAttributeText(new QName("", "extension"), in);
//        cursor.dispose();

        return id;
    }

    public POCDMT000040Participant2 mapToPSN(EcrMsgProviderDto in, POCDMT000040Participant2 out)
            throws EcrCdaXmlException {
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
        String email="";

        String prefix="";
        int teleCounter=0;

        out.setTypeCode("PRF");

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();

            var param = new Psn();
            param.setFirstName(firstName);
            param.setPrefix(prefix);
            param.setLastName(lastName);
            param.setSuffix(suffix);
            param.setDegree(degree);
            param.setAddress1(address1);
            param.setAddress2(address2);
            param.setCity(city);
            param.setCounty(county);
            param.setCountry(country);
            param.setState(state);
            param.setZip(zip);
            param.setTelephone(telephone);
            param.setExtn(extn);
            param.setEmail(email);
            var psn = mapToPSNFieldCheckAndMap( in,
                     out,
                     name,
                     param);

             firstName = psn.getFirstName();
             prefix = psn.getPrefix();
             lastName = psn.getLastName();
             suffix = psn.getSuffix();
             degree = psn.getDegree();
             address1 = psn.getAddress1();
             address2 = psn.getAddress2();
             city = psn.getCity();
             county = psn.getCounty();
             country = psn.getCountry();
             state = psn.getState();
             zip = psn.getZip();
             telephone = psn.getTelephone();
             extn = psn.getExtn();
             email = psn.getEmail();
             out = psn.getOut();
        }

        if(!firstName.isEmpty()){
            mapToPSNFirstName( firstName,   out);
        }
        if(!lastName.isEmpty()){
            mapToPSNLastName( lastName,   out);
        }
        if(!prefix.isEmpty()){
            mapToPSNPrefix( prefix,  out);
        }
        if(!suffix.isEmpty()){
            mapToPSNSuffix( suffix,   out);
        }
        if(!address1.isEmpty()){
            mapToPSNAddress1( address1,   out);
        }
        if(!address2.isEmpty()){
            mapToPSNAddress2( address2,   out);
        }
        if(!city.isEmpty()){
            mapToPSNCity( city,   out);
        }
        if(!county.isEmpty()){
            mapToPSNCounty( county,   out);
        }
        if(!zip.isEmpty()){
            mapToPSNZip( zip,   out);
        }

        if(!state.isEmpty()){
            mapToPSNState( state,   out);
        }
        if(!country.isEmpty()){
            mapToPSNCountry( country,   out);
        }
        if(!telephone.isEmpty()){
            var tele = mapToPSNTelephone( telephone,   out,  extn,
             teleCounter);
            out = tele.getOut();
            teleCounter = tele.getTeleCounter();
        }
        if(!email.isEmpty()){
            var emailInfo = mapToPSNEmail( email,   out,
             teleCounter);
            out = emailInfo.getOut();
        }

        return out;
    }

    private PsnTelephone mapToPSNEmail(String email,  POCDMT000040Participant2 out,
                                           int teleCounter) {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewTelecom();
        }
        else {
            out.getParticipantRole().addNewTelecom();
        }
        out.getParticipantRole().getTelecomArray(teleCounter).setUse(new ArrayList<String>(List.of("WP")));
        out.getParticipantRole().getTelecomArray(teleCounter).setValue(email);
        teleCounter= teleCounter + 1;
        PsnTelephone psnTelephone = new PsnTelephone();
        psnTelephone.setOut(out);
        psnTelephone.setTeleCounter(teleCounter);
        return psnTelephone;
    }


    private PsnTelephone mapToPSNTelephone(String telephone,  POCDMT000040Participant2 out, String extn,
                                                       int teleCounter)   {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewTelecom();
        } else {
            out.getParticipantRole().addNewTelecom();
        }
        out.getParticipantRole().getTelecomArray(teleCounter).setUse(new ArrayList<String>(List.of("WP")));
        int phoneExtnSize= extn.length();
        if(phoneExtnSize>0){
            telephone=telephone+ EXTN_STR+ extn;
        }

        out.getParticipantRole().getTelecomArray(teleCounter).setValue(telephone);
        teleCounter = teleCounter+1;

        PsnTelephone psnTelephone = new PsnTelephone();
        psnTelephone.setOut(out);
        psnTelephone.setTeleCounter(teleCounter);
        return psnTelephone;
    }

    private void mapToPSNCountry(String country,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
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

    private void mapToPSNState(String state,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
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

    private void mapToPSNZip(String zip,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
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

    private void mapToPSNCounty(String county,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
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

    private void mapToPSNCity(String city,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
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

    private void mapToPSNAddress2(String address2,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
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

    private void mapToPSNAddress1(String address1,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
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


    private void mapToPSNSuffix(String suffix,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewPlayingEntity().addNewName();
        }
//        else {
//            out.getParticipantRole().addNewPlayingEntity().addNewName();
//        }
        var mapVal = mapToPCData(suffix);
        EnSuffix enG = EnSuffix.Factory.newInstance();
        enG.set(mapVal);
        out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewSuffix();

        out.getParticipantRole().getPlayingEntity().getNameArray(0).setSuffixArray(0,  EnSuffix.Factory.newInstance());
        out.getParticipantRole().getPlayingEntity().getNameArray(0).getSuffixArray(0).set(mapVal);
    }


    private void mapToPSNPrefix(String prefix,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewPlayingEntity().addNewName();
        }
//        else {
//            out.getParticipantRole().addNewPlayingEntity().addNewName();
//        }
        var mapVal = mapToPCData(prefix);
        EnPrefix enG = EnPrefix.Factory.newInstance();
        enG.set(mapVal);
        out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewPrefix();
        out.getParticipantRole().getPlayingEntity().getNameArray(0).setPrefixArray(0,  EnPrefix.Factory.newInstance());
        out.getParticipantRole().getPlayingEntity().getNameArray(0).getPrefixArray(0).set(mapVal);
    }

    private void mapToPSNFirstName(String firstName,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
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
        out.getParticipantRole().getPlayingEntity().getNameArray(0).setUse(new ArrayList<String>(List.of("L")));
    }

    private void mapToPSNLastName(String lastName,  POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewPlayingEntity().addNewName();
        }
//        else {
//            out.getParticipantRole().addNewPlayingEntity().addNewName();
//        }
        var mapVal = mapToCData(lastName);
        EnFamily enG = EnFamily.Factory.newInstance();
        enG.set(mapVal);
        out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewFamily();
        out.getParticipantRole().getPlayingEntity().getNameArray(0).setFamilyArray(0,  EnFamily.Factory.newInstance());
        out.getParticipantRole().getPlayingEntity().getNameArray(0).getFamilyArray(0).set(mapVal);
        out.getParticipantRole().getPlayingEntity().getNameArray(0).setUse(new ArrayList<String>(List.of("L")));
    }

    private Psn mapToPSNFieldCheckAndMap(EcrMsgProviderDto in,
                                          POCDMT000040Participant2 out,
                                          String name,
                                          Psn param) throws EcrCdaXmlException {
        String firstName = param.getFirstName();
        String prefix = param.getPrefix();
        String lastName = param.getLastName();
        String suffix = param.getSuffix();
        String degree = param.getDegree();
        String address1 = param.getAddress1();
        String address2 = param.getAddress2();
        String city = param.getCity();
        String county = param.getCounty();
        String country = param.getCountry();
        String state = param.getState();
        String zip = param.getZip();
        String telephone = param.getTelephone();
        String extn = param.getExtn();
        String email = param.getEmail();

        Psn param2 = new Psn();
        param2.setFirstName(firstName);
        param2.setPrefix(prefix);
        param2.setLastName(lastName);
        param2.setSuffix(suffix);
        param2.setDegree(degree);
        param2.setAddress1(address1);
        param2.setAddress2(address2);
        param2.setCity(city);
        param2 = mapToPSNFieldCheckAndMapGenericP1( in,
                 out,
                 name,
                 param);
         firstName = param2.getFirstName();
         prefix = param2.getPrefix();
         lastName = param2.getLastName();
         suffix = param2.getSuffix();
         degree = param2.getDegree();
         address1 = param2.getAddress1();
         address2 = param2.getAddress2();
         city = param2.getCity();
         out = param2.getOut();

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
            mapToPSNFieldCheckAndMapQuickCode( in,
                     out);
        }
        else if( mapToPSNFieldCheckAndMapValidateField(name, in)) {
            email = in.getPrvEmailAddressTxt();
        }

        param.setFirstName(firstName);
        param.setPrefix(prefix);
        param.setLastName(lastName);
        param.setSuffix(suffix);
        param.setDegree(degree);
        param.setAddress1(address1);
        param.setAddress2(address2);
        param.setCity(city);
        param.setCounty(county);
        param.setCountry(country);
        param.setState(state);
        param.setZip(zip);
        param.setTelephone(telephone);
        param.setExtn(extn);
        param.setEmail(email);
        param.setOut(out);

        return param;
    }

    private boolean mapToPSNFieldCheckAndMapValidateField(String name, EcrMsgProviderDto in) {
        return name.equals("prvEmailAddressTxt") && in.getPrvEmailAddressTxt() != null && !in.getPrvEmailAddressTxt().isEmpty();
    }

    private Psn mapToPSNFieldCheckAndMapGenericP1(EcrMsgProviderDto in,
                                              POCDMT000040Participant2 out,
                                              String name,
                                              Psn param) {
        String firstName = param.getFirstName();
        String prefix = param.getPrefix();
        String lastName = param.getLastName();
        String suffix = param.getSuffix();
        String degree = param.getDegree();
        String address1 = param.getAddress1();
        String address2 = param.getAddress2();
        String city = param.getCity();

        if (name.equals("prvLocalId") && in.getPrvLocalId() != null && !in.getPrvLocalId().isEmpty()) {
            mapToPSNFieldCheckAndMapLocalId( in,
                    out);
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
        else if(name.equals("prvAddrStreetAddr1Txt")) {
            address1 = mapToPSNFieldCheckAndMapAddress1( in,  address1);
        }
        else if(name.equals("prvAddrStreetAddr2Txt")) {
            address2 = mapToPSNFieldCheckAndMapGenericP1Address2( in,  address2);
        }
        else if(name.equals("prvAddrCityTxt")) {
            city = mapToPSNFieldCheckAndMapCity(in, city);
        }

        param.setFirstName(firstName);
        param.setPrefix(prefix);
        param.setLastName(lastName);
        param.setSuffix(suffix);
        param.setDegree(degree);
        param.setAddress1(address1);
        param.setAddress2(address2);
        param.setCity(city);
        param.setOut(out);
        return param;
    }

    private String mapToPSNFieldCheckAndMapGenericP1Address2(EcrMsgProviderDto in, String address2) {
      if(in.getPrvAddrStreetAddr2Txt() != null && !in.getPrvAddrStreetAddr2Txt().isEmpty()) {
            address2 = mapToPSNFieldCheckAndMapAddress2( in, address2);
      }
      return address2;
    }

    private String mapToPSNFieldCheckAndMapAddress1(EcrMsgProviderDto in, String address1) {
        if(in.getPrvAddrStreetAddr1Txt() !=null && !in.getPrvAddrStreetAddr1Txt().isEmpty()) {
            address1 = in.getPrvAddrStreetAddr1Txt();
        }
        return address1;
    }

    private String mapToPSNFieldCheckAndMapAddress2(EcrMsgProviderDto in, String address2) {
        if(in.getPrvAddrStreetAddr2Txt() != null && !in.getPrvAddrStreetAddr2Txt().isEmpty()) {
            address2 = in.getPrvAddrStreetAddr2Txt();
        }
        return address2;
    }
    private String mapToPSNFieldCheckAndMapCity(EcrMsgProviderDto in, String city) {
        if(in.getPrvAddrCityTxt() != null && !in.getPrvAddrCityTxt().isEmpty()) {
            city = in.getPrvAddrCityTxt();
        }
        return city;
    }

    private void mapToPSNFieldCheckAndMapQuickCode(EcrMsgProviderDto in,
                                                                     POCDMT000040Participant2 out) {
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

    private void mapToPSNFieldCheckAndMapLocalId(EcrMsgProviderDto in,
                                    POCDMT000040Participant2 out) {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewId();
        } else {
            out.getParticipantRole().addNewId();
        }
        out.getParticipantRole().getIdArray(0).setExtension(in.getPrvLocalId());
        out.getParticipantRole().getIdArray(0).setRoot("2.16.840.1.113883.11.19745");
        out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
    }

    public POCDMT000040Participant2 mapToORG(EcrMsgOrganizationDto in,
                                             POCDMT000040Participant2 out)
            throws EcrCdaXmlException {
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

            Org param = new Org();
            param.setState(state);
            param.setStreetAddress1(streetAddress1);
            param.setStreetAddress2(streetAddress2);
            param.setCity(city);
            param.setCounty(county);
            param.setCountry(country);
            param.setZip(zip);
            param.setPhone(phone);
            param.setExtn(extn);
            var orgModel = mapToORGFieldCheckP1( in,
                     out,
                     name,
                     param);
             state= orgModel.getState();
             streetAddress1= orgModel.getStreetAddress1();
             streetAddress2= orgModel.getStreetAddress2();
             city = orgModel.getCity();
             county = orgModel.getCounty();
             country = orgModel.getCountry();
             zip = orgModel.getZip();
             phone= orgModel.getPhone();
             extn = orgModel.getExtn();
             out = orgModel.getOut();
        }



        int isAddressPopulated= 0;
        if(streetAddress1 != null){
            mapToORGFieldStreetAddress( streetAddress1,  out);
            isAddressPopulated=1;
        }
        if(streetAddress2 != null){
            mapToORGFieldStreetAddress2( streetAddress2,  out);
            isAddressPopulated=1;
        }
        if(city != null){
            mapToORGFieldStreetCity( city,  out);
            isAddressPopulated=1;
        }
        if(state != null){
            mapToORGFieldStreetState( state,  out);
            isAddressPopulated=1;
        }
        if(county != null){
            mapToORGFieldStreetCounty( county,  out);
            isAddressPopulated=1;
        }
        if(zip != null){
            mapToORGFieldZip( zip,  out);
            isAddressPopulated=1;
        }
        if(country != null){

            mapToORGFieldCountry( country,  out);
            isAddressPopulated=1;
        }
        if(isAddressPopulated>0) {
            mapToORGFieldAddressPopulated( out);
        }
        if(phone!= null){
            mapToORGFieldPhone( phone,  extn,  out);
        }
        return out;
    }

    private void mapToORGFieldPhone(String phone, String extn, POCDMT000040Participant2 out) {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewTelecom();
        } else {
            out.getParticipantRole().addNewTelecom();
        }
        out.getParticipantRole().getTelecomArray(0).setUse(new ArrayList<String>(List.of("WP")));
        int phoneExtnSize = extn.length();
        if(phoneExtnSize>0){
            phone=phone+ EXTN_STR+ extn;
        }
        out.getParticipantRole().getTelecomArray(0).setValue(phone);
    }

    private void mapToORGFieldAddressPopulated(POCDMT000040Participant2 out) {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).setUse(new ArrayList<String>(List.of("WP")));
    }

    private void mapToORGFieldCountry(String country, POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }

        out.getParticipantRole().getAddrArray(0).addNewCountry();

        out.getParticipantRole().getAddrArray(0).setCountryArray(0, AdxpCountry.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(mapToCData(country));
    }

    private void mapToORGFieldZip(String zip, POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }

        out.getParticipantRole().getAddrArray(0).addNewPostalCode();

        out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0, AdxpPostalCode.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(mapToCData(zip));
    }

    private void mapToORGFieldStreetCounty(String county, POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).addNewCounty();
        out.getParticipantRole().getAddrArray(0).setCountyArray(0, AdxpCounty.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(mapToCData(county));
    }

    private void mapToORGFieldStreetState(String state, POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).addNewState();
        out.getParticipantRole().getAddrArray(0).setStateArray(0, AdxpState.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getStateArray(0).set(mapToCData(state));
    }


    private void mapToORGFieldStreetCity(String city, POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).addNewCity();
        out.getParticipantRole().getAddrArray(0).setCityArray(0, AdxpCity.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getCityArray(0).set(mapToCData(city));
    }

    private void mapToORGFieldStreetAddress2(String streetAddress2, POCDMT000040Participant2 out) throws EcrCdaXmlException {
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
    }

    private void mapToORGFieldStreetAddress(String streetAddress1, POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }

        out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
        out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, AdxpStreetAddressLine.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(mapToCData(streetAddress1));
    }

    @SuppressWarnings("java:S3776")
    private Org mapToORGFieldCheckP1(EcrMsgOrganizationDto in,
                                      POCDMT000040Participant2 out,
                                      String name,
                                      Org param) throws EcrCdaXmlException {
        String state= param.getState();
        String streetAddress1= param.getStreetAddress1();
        String streetAddress2= param.getStreetAddress2();
        String city = param.getCity();
        String county = param.getCounty();
        String country = param.getCountry();
        String zip = param.getZip();
        String phone= param.getPhone();
        String extn = param.getExtn();

        if(name.equals("orgLocalId") && in.getOrgLocalId()!=null){
            mapToORGFieldCheckP1LocalId( in,out);
        }
        else if(name.equals("orgNameTxt") && in.getOrgNameTxt() != null){
            mapToORGFieldCheckP1OrgName( in,
                     out);
        }
        else if(name.equals("orgAddrStreetAddr1Txt") && mapToORGFieldCheckP1ValidateField(in)){
            streetAddress1= in.getOrgAddrStreetAddr1Txt();
        }
        else if(name.equals("orgAddrStreetAddr2Txt") && mapToORGFieldCheckP1ValidateField(in)){
            streetAddress2 =in.getOrgAddrStreetAddr2Txt();
        }
        else if(name.equals("orgAddrCityTxt") && mapToORGFieldCheckP1ValidateField(in)){
            city= in.getOrgAddrCityTxt();
        }
        else if(name.equals("orgAddrCountyCd") && mapToORGFieldCheckP1ValidateField(in)){
            county = mapToAddressType( in.getOrgAddrCountyCd(), county);
        }
        else if (name.equals("orgAddrStateCd") && mapToORGFieldCheckP1ValidateField(in)){
            state= mapToAddressType( in.getOrgAddrStateCd(), state);
        }
        else if(name.equals("orgAddrZipCodeTxt") && mapToORGFieldCheckP1ValidateField(in)){
            zip = in.getOrgAddrZipCodeTxt();
        }
        else if(name.equals("orgAddrCountryCd") && mapToORGFieldCheckP1ValidateField(in)){
            country = mapToAddressType( in.getOrgAddrCountryCd(), country);
        }
        else if(name.equals("orgPhoneNbrTxt") && mapToORGFieldCheckP1ValidateField(in)){
            phone=in.getOrgPhoneNbrTxt();
        }
        else if (name.equals("orgPhoneExtensionTxt") && in.getOrgPhoneExtensionTxt() != null)
        {
            extn= in.getOrgPhoneExtensionTxt().toString();
        }
        else if(name.equals("orgIdCliaNbrTxt") && in.getOrgIdCliaNbrTxt() != null){
            mapToORGFieldCheckP1CliaNbr( in,
                     out);
        }

        param.setState(state);
        param.setStreetAddress1(streetAddress1);
        param.setStreetAddress2(streetAddress2);
        param.setCity(city);
        param.setCounty(county);
        param.setCountry(country);
        param.setZip(zip);
        param.setPhone(phone);
        param.setExtn(extn);
        param.setOut(out);
        return param;
    }

    private boolean mapToORGFieldCheckP1ValidateField(EcrMsgOrganizationDto in) {
        return (in.getOrgAddrStreetAddr1Txt() != null && !in.getOrgAddrStreetAddr1Txt().isEmpty()) ||
                (in.getOrgAddrStreetAddr2Txt() != null && !in.getOrgAddrStreetAddr2Txt().isEmpty()) ||
                (in.getOrgAddrCityTxt() != null && !in.getOrgAddrCityTxt().isEmpty()) ||
                (in.getOrgAddrCountyCd() != null && !in.getOrgAddrCountyCd().isEmpty()) ||
                (in.getOrgAddrStateCd() != null && !in.getOrgAddrStateCd().isEmpty()) ||
                (in.getOrgAddrZipCodeTxt() != null && !in.getOrgAddrZipCodeTxt().isEmpty()) ||
                (in.getOrgAddrCountryCd() != null && !in.getOrgAddrCountryCd().isEmpty()) ||
                (in.getOrgPhoneNbrTxt() != null && !in.getOrgPhoneNbrTxt().isEmpty());
    }

    private void mapToORGFieldCheckP1CliaNbr(EcrMsgOrganizationDto in,
                                                                 POCDMT000040Participant2 out) {
        if (!in.getOrgIdCliaNbrTxt().isEmpty()) {
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

    private void mapToORGFieldCheckP1OrgName(EcrMsgOrganizationDto in,
                                                                 POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if ( !in.getOrgNameTxt().isEmpty()) {
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

    }

    private void mapToORGFieldCheckP1LocalId(EcrMsgOrganizationDto in,
                                        POCDMT000040Participant2 out) {
        if (!in.getOrgLocalId().isEmpty()) {
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewId();
            } else {
                out.getParticipantRole().addNewId();
            }
            out.getParticipantRole().getIdArray(0).setRoot(ID_ARR_ROOT);
            out.getParticipantRole().getIdArray(0).setExtension(in.getOrgLocalId());
            out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
        }
    }


    public String getValueFromMap(Map.Entry<String, Object> entry) {
        String value = null;
        if (entry.getValue() != null) {
            value = entry.getValue().toString();
        }
        return value;
    }




}
