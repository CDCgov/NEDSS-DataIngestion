package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.PhdcAnswerDao;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.QuestionIdentifierMapDao;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgOrganizationDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgProviderDto;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

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
                phone=phone+ EXTN_STR+ extn;
            }
            out.getParticipantRole().getTelecomArray(0).setValue(phone);

        }
        return out;
    }






}
