package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.PhdcAnswerDao;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.QuestionIdentifierMapDao;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import gov.cdc.nedss.phdc.cda.CD;
import gov.cdc.nedss.phdc.cda.CE;
import gov.cdc.nedss.phdc.cda.POCDMT000040CustodianOrganization;
import gov.cdc.nedss.phdc.cda.TS;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;

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



}
