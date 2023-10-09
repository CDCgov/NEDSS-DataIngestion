package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.PhdcAnswerDao;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.QuestionIdentifierMapDao;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import javax.xml.namespace.QName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.GetStringsBeforePipe;

public class CdaMapHelper implements ICdaMapHelper {

    private ICdaLookUpService ecrLookUpService;

    public CdaMapHelper(ICdaLookUpService ecrLookUpService) {
        this.ecrLookUpService = ecrLookUpService;
    }

    public XmlObject mapToCData(String data) throws EcrCdaXmlException {
        try {
            XmlObject xmlObject = XmlObject.Factory.parse("<CDATA>"+data+"</CDATA>");
            return xmlObject;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }


    public XmlObject mapToStringData(String data) throws EcrCdaXmlException {
        try {
            XmlObject xmlObject = XmlObject.Factory.parse("<STRING>"+data+"</STRING>");
            return xmlObject;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }

    public XmlObject mapToUsableTSElement(String data, XmlObject output, String name) throws EcrCdaXmlException {
        XmlCursor cursor = output.newCursor();
        cursor.toFirstChild();  // Move to the root element

        cursor.beginElement(name);
        cursor.insertAttributeWithValue("type", "IVL_TS");
        cursor.toFirstChild();  // Move inside childName
        cursor.beginElement("low");
        cursor.insertNamespace("", XML_NAME_SPACE_HOLDER);
        cursor.insertAttributeWithValue(VALUE_NAME, mapToTsType(data).getValue().toString());
        cursor.dispose();
        return output;
    }

    public TS mapToTsType(String data) throws EcrCdaXmlException {
        try {
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
        } catch (Exception e) {
            throw  new EcrCdaXmlException(e.getMessage());
        }

    }

    public String mapToQuestionId(String data) {
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

    public CE mapToCEAnswerType(String data, String questionCode) {
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

    public String mapToAddressType(String data, String questionCode) {
        String output = "";
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

    public PhdcAnswerDao mapToCodedAnswer(String data, String questionCode) {
        PhdcAnswerDao model = new PhdcAnswerDao();
        String translation="";
        String isTranslationReq= "YES";
        String code = "";
        String transCode = data;
        String transCodeSystem = "";
        String transCodeSystemName = "";
        String transDisplayName = "";
        String codeSystem = "";
        String CODE_SYSTEM_NAME = "";
        String displayName = "";

        // RhapsodyTableLookup(output, tableName, resultColumnName, defaultValue, queryColumn1, queryValue1, queryColumn2, queryValue2, ...)
        var phdcAnswer = ecrLookUpService.fetchPhdcAnswerByCriteriaForTranslationCode(questionCode, data);
        if (phdcAnswer != null) {
            isTranslationReq = phdcAnswer.getCodeTranslationRequired();
            code = phdcAnswer.getAnsToCode();
            transCodeSystem = phdcAnswer.getAnsFromCodeSystemCd();
            transCodeSystemName = phdcAnswer.getAnsFromCodeSystemCd();
            transDisplayName = phdcAnswer.getAnsFromDisplayNm();
            codeSystem = phdcAnswer.getAnsToCodeSystemCd();
            CODE_SYSTEM_NAME = phdcAnswer.getAnsToCodeSystemDescTxt();
            displayName = phdcAnswer.getAnsToDisplayNm();
        }
        else {
            transCodeSystem = ID_ROOT;
            codeSystem = ID_ROOT;
            isTranslationReq = NOT_MAPPED_VALUE;
            code = NOT_MAPPED_VALUE;
            transCodeSystemName = NOT_MAPPED_VALUE;
            transDisplayName = NOT_MAPPED_VALUE;
            CODE_SYSTEM_NAME =NOT_MAPPED_VALUE;
            displayName = NOT_MAPPED_VALUE;
        }

        if (code.equalsIgnoreCase(NOT_MAPPED_VALUE)) {
            code = data;
        }

        if (code.equalsIgnoreCase("NULL") || code.isEmpty()) {
            code = data ;
            codeSystem = transCodeSystem;
            CODE_SYSTEM_NAME = transCodeSystemName;
            displayName = transDisplayName;
        }

        model.setCode(code);
        model.setCodeSystem(codeSystem);
        model.setCodeSystemName(CODE_SYSTEM_NAME);
        model.setDisplayName(displayName);
        model.setTransCode(transCode);
        model.setTransCodeSystem(transCodeSystem);
        model.setTransCodeSystemName(transCodeSystemName);
        model.setTransDisplayName(transDisplayName);

        return model;
    }

    public POCDMT000040CustodianOrganization mapToElementValue(String data, POCDMT000040CustodianOrganization output, String name) {
        XmlCursor cursor = output.newCursor();
        cursor.toFirstChild();
        cursor.beginElement(name);
        cursor.insertAttributeWithValue("xmlns", XML_NAME_SPACE_HOLDER);
        cursor.insertProcInst("CDATA", data);
        cursor.dispose();

        return output;
    }


    public String getCurrentUtcDateTimeInCdaFormat() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX");
        String formattedDate = utcNow.format(formatter);
        return formattedDate;
    }

    /*
     * TEST NEEDED
     * */
    public POCDMT000040Observation mapToObservation(String questionCode, String data,
        POCDMT000040Observation observation) throws EcrCdaXmlException {
        try {
            observation.setClassCode("OBS");
            observation.setMoodCode(XActMoodDocumentObservation.EVN);
            String dataType="DATE";
            String defaultQuestionIdentifier = "";

            PhdcQuestionLookUpDto questionLup = new PhdcQuestionLookUpDto();
            questionLup.setQuestionIdentifier(NOT_FOUND_VALUE);
            questionLup.setQuesCodeSystemCd(NOT_FOUND_VALUE);
            questionLup.setQuesCodeSystemDescTxt(NOT_FOUND_VALUE);
            questionLup.setQuesDisplayName(NOT_FOUND_VALUE);
            questionLup.setDataType(NOT_FOUND_VALUE);
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
                map.setDynamicQuestionIdentifier(NOT_FOUND_VALUE);
                QuestionIdentifierMapDto identifierMap = ecrLookUpService.fetchQuestionIdentifierMapByCriteriaByCriteria("Question_Identifier", questionCode);
                if(identifierMap != null && !identifierMap.getDynamicQuestionIdentifier().isEmpty()) {
                    map.setDynamicQuestionIdentifier(identifierMap.getDynamicQuestionIdentifier());
                }

                if(map.getDynamicQuestionIdentifier().equalsIgnoreCase("STANDARD")
                        || map.getDynamicQuestionIdentifier().equalsIgnoreCase(NOT_FOUND_VALUE)) {
                    defaultQuestionIdentifier = questionCode;
                }

                //endregion

                if (!result.getDataType().isEmpty()) {
                    if (result.getDataType().equalsIgnoreCase(DATA_TYPE_CODE)) {
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

                            ANY any = ANY.Factory.parse(VALUE_TAG);
                            var element = any;
                            XmlCursor cursor = element.newCursor();
                            cursor.toFirstAttribute();
                            cursor.toNextToken();
                            cursor.insertAttributeWithValue(new QName(NAME_SPACE_URL, "type"), "II");
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

//                        ANY any = ANY.Factory.parse(VALUE_TAG);
//                        var element = any;
//                        XmlCursor cursor = element.newCursor();
//                        if (cursor.toFirstAttribute() || !cursor.toEndToken().isStart()) { // Added check here
//                            cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "TS");
//                            if (cursor.getAttributeText(new QName(value)) != null) {
//                                cursor.setAttributeText(new QName(value), mapToTsType(data).toString());
//                            } else {
//                                cursor.toStartDoc();
//                                cursor.toNextToken(); // Moves to the start of the element
//                                cursor.insertAttributeWithValue(value, mapToTsType(data).toString());
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
//                        ANY any = ANY.Factory.parse(VALUE_TAG);
//
//                        var element = any;
//                        XmlCursor cursor = element.newCursor();
//                        cursor.toFirstAttribute();
//                        cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "ST");
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
                observation.getCode().setCodeSystem(CODE_NODE_MAPPED_VALUE);
                observation.getCode().setCodeSystemName(CODE_NODE_MAPPED_VALUE);
                observation.getCode().setDisplayName(CODE_NODE_MAPPED_VALUE);
            }
            return observation;
        } catch ( Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }



    public PhdcQuestionLookUpDto mapToCodedQuestionType(String questionIdentifier) {
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

    public CE mapToCEQuestionType(String questionCode, CE output) {
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
            // Set the attributes of the 'value' element
            cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "ST");
            // Add child CDATA to 'value' element
            cursor.toNextToken(); // Move to the end of the current element (value element)
            cursor.insertChars(input);
        }

        cursor.dispose();
        return output;
    }

    public XmlObject mapToObservationPlace(String in, XmlObject out) {
        XmlCursor cursor = out.newCursor();
        cursor.toFirstChild();
        cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "II");
        cursor.setAttributeText(new QName("", "root"), "2.3.3.3.322.23.34");
        cursor.setAttributeText(new QName("", "extension"), in);
        cursor.dispose();

        return out;
    }

}
