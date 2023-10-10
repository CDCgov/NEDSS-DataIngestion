package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaPatientMapper;
import gov.cdc.dataingestion.nbs.ecr.model.patient.CdaPatientField;
import gov.cdc.dataingestion.nbs.ecr.model.patient.CdaPatientTelecom;
import gov.cdc.dataingestion.nbs.ecr.model.ValueMapper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaPatientMappingHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgPatientDto;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.GetStringsBeforePipe;

public class CdaPatientMappingHelper implements ICdaPatientMappingHelper {
    ICdaMapHelper cdaMapHelper;
    public CdaPatientMappingHelper(ICdaMapHelper cdaMapHelper) {
        this.cdaMapHelper = cdaMapHelper;
    }

    public CdaPatientMapper mapToPatient(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument, int patientComponentCounter, String inv168)
            throws EcrCdaXmlException {
        try {
            CdaPatientMapper mapper = new CdaPatientMapper();
            for(var patient : input.getMsgPatients()) {

                //region VARIABLE
                String address1 ="";
                String address2 ="";
                String homeExtn="";
                String PAT_HOME_PHONE_NBR_TXT  ="";
                String PAT_WORK_PHONE_EXTENSION_TXT="";
                String wpNumber="";
                String cellNumber="";
                String PAT_NAME_FIRST_TXT="";
                String PAT_NAME_MIDDLE_TXT="";
                String PAT_NAME_PREFIX_CD="";
                String PAT_NAME_LAST_TXT="";
                String PAT_NAME_SUFFIX_CD="";

                int phoneCounter = 0;
                String PAT_EMAIL_ADDRESS_TXT="";
                String PAT_URL_ADDRESS_TXT="";
                String PAT_PHONE_AS_OF_DT="";
                String PAT_PHONE_COUNTRY_CODE_TXT="";
                int patientIdentifier =0;
                int k = 1;
                CdaPatientField patientField = new CdaPatientField(
                        patientIdentifier,
                        address1,
                        address2,
                        k,
                        patientComponentCounter,
                        clinicalDocument,
                        PAT_WORK_PHONE_EXTENSION_TXT,
                        PAT_HOME_PHONE_NBR_TXT,
                        wpNumber,
                        PAT_PHONE_COUNTRY_CODE_TXT,
                        cellNumber,
                        PAT_NAME_PREFIX_CD,
                        PAT_NAME_FIRST_TXT,
                        PAT_NAME_MIDDLE_TXT,
                        PAT_NAME_LAST_TXT,
                        PAT_NAME_SUFFIX_CD,
                        PAT_EMAIL_ADDRESS_TXT,
                        PAT_URL_ADDRESS_TXT,
                        PAT_PHONE_AS_OF_DT,
                        inv168
                );

                //endregion
                if (input.getMsgPatients() != null && input.getMsgPatients().size() > 0) {
                    Field[] fields = EcrMsgPatientDto.class.getDeclaredFields();
                    for (Field field : fields) {

                        patientField = patientFieldCheck(field, clinicalDocument, patient, patientField);
                        address1 = patientField.getAddress1();
                        address2 = patientField.getAddress2();
                        k = patientField.getK();
                        patientComponentCounter = patientField.getPatientComponentCounter();
                        clinicalDocument = patientField.getClinicalDocument();
                        PAT_WORK_PHONE_EXTENSION_TXT = patientField.getWorkPhoneExt();
                        wpNumber = patientField.getWpNumber();
                        cellNumber = patientField.getCellNumber();
                        PAT_EMAIL_ADDRESS_TXT = patientField.getEmail();
                        PAT_URL_ADDRESS_TXT = patientField.getUrlAddress();
                        PAT_PHONE_AS_OF_DT = patientField.getPhoneAsDateTime();
                        inv168 = patientField.getInv168();

                        clinicalDocument = patientFieldCheckKMapping(k, clinicalDocument, patient, field);
                    }
                }

                var fieldModel = mapPatientPersonalInfo(clinicalDocument, patientField,
                        homeExtn, phoneCounter);
                clinicalDocument = fieldModel.getClinicalDocument();
                phoneCounter = fieldModel.getPhoneCounter();

                // wpNumber
                CdaPatientTelecom cdaPatientTeleComWpNumber =
                        mapPatientWpNumber(clinicalDocument, wpNumber, PAT_WORK_PHONE_EXTENSION_TXT,
                                PAT_PHONE_AS_OF_DT, phoneCounter);

                clinicalDocument = cdaPatientTeleComWpNumber.getClinicalDocument();
                phoneCounter= cdaPatientTeleComWpNumber.getPhoneCounter();

                // cellNumber
                CdaPatientTelecom cdaPatientTeleComCelLNumber =
                        mapPatientCellPhone(clinicalDocument, cellNumber,
                                PAT_PHONE_AS_OF_DT, phoneCounter);
                clinicalDocument = cdaPatientTeleComCelLNumber.getClinicalDocument();
                phoneCounter= cdaPatientTeleComCelLNumber.getPhoneCounter();

                // PAT_EMAIL_ADDRESS_TXT
                CdaPatientTelecom cdaPatientTelecomEmail =
                        mapPatientEmail(clinicalDocument, PAT_EMAIL_ADDRESS_TXT,
                                PAT_PHONE_AS_OF_DT, phoneCounter  );
                clinicalDocument = cdaPatientTelecomEmail.getClinicalDocument();
                phoneCounter= cdaPatientTelecomEmail.getPhoneCounter();


                // PAT_URL_ADDRESS_TXT
                CdaPatientTelecom cdaPatientTelecomUrl =
                        mapPatientUrlAddress(clinicalDocument, PAT_URL_ADDRESS_TXT,
                                PAT_PHONE_AS_OF_DT, phoneCounter);
                clinicalDocument = cdaPatientTelecomUrl.getClinicalDocument();

                clinicalDocument = mapPatientAddress1(clinicalDocument, address1);
                clinicalDocument = mapPatientAddress2(clinicalDocument, address2);

            }
            mapper.setClinicalDocument(clinicalDocument);
            mapper.setPatientComponentCounter(patientComponentCounter);
            mapper.setInv168(inv168);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }

    private CdaPatientField patientFieldCheck(Field field,
                                   POCDMT000040ClinicalDocument1 clinicalDocument,
                                   EcrMsgPatientDto patient,
                                   CdaPatientField cdaPatientField
                                   ) throws XmlException, ParseException, EcrCdaXmlException {
        if (field.getName().equals("patPrimaryLanguageCd") &&
                patient.getPatPrimaryLanguageCd() != null && !patient.getPatPrimaryLanguageCd().isEmpty()) {
            clinicalDocument = checkLanguageCode(clinicalDocument);
            clinicalDocument.getLanguageCode().setCode(patient.getPatPrimaryLanguageCd());
        }
        else if (field.getName().equals(PAT_LOCAL_ID_CONST) &&
                patient.getPatLocalId() != null && !patient.getPatLocalId().isEmpty()) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewId();
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(cdaPatientField.getPatientIdentifier()).setExtension(patient.getPatLocalId());
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(cdaPatientField.getPatientIdentifier()).setRoot("2.16.840.1.113883.4.1");
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(cdaPatientField.getPatientIdentifier()).setAssigningAuthorityName("LR");

            cdaPatientField.setPatientIdentifier(cdaPatientField.getPatientIdentifier() + 1);
        }
        else if (field.getName().equals("patIdMedicalRecordNbrTxt") &&
                patient.getPatIdMedicalRecordNbrTxt() != null && !patient.getPatIdMedicalRecordNbrTxt().isEmpty()) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewId();
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(cdaPatientField.getPatientIdentifier()).setExtension(patient.getPatIdMedicalRecordNbrTxt());
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(cdaPatientField.getPatientIdentifier()).setRoot("2.16.840.1.113883.4.1");
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(cdaPatientField.getPatientIdentifier()).setAssigningAuthorityName("LR_MRN");
            cdaPatientField.setPatientIdentifier(cdaPatientField.getPatientIdentifier() + 1);
        }
        else if (field.getName().equals("patIdSsnTxt") &&
                patient.getPatIdSsnTxt() != null && !patient.getPatIdSsnTxt().isEmpty()) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewId();
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(cdaPatientField.getPatientIdentifier()).setExtension(patient.getPatIdSsnTxt());
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(cdaPatientField.getPatientIdentifier()).setRoot("2.16.840.1.114222.4.5.1");
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(cdaPatientField.getPatientIdentifier()).setAssigningAuthorityName("SS");
            cdaPatientField.setPatientIdentifier(cdaPatientField.getPatientIdentifier() + 1);
        }
        else if (field.getName().equals("patAddrStreetAddr1Txt") && patient.getPatAddrStreetAddr1Txt() != null && !patient.getPatAddrStreetAddr1Txt().isEmpty()) {
            cdaPatientField.setAddress1(cdaPatientField.getAddress1() + patient.getPatAddrStreetAddr1Txt());
        }
        else if (field.getName().equals("patAddrStreetAddr2Txt") && patient.getPatAddrStreetAddr2Txt() != null && !patient.getPatAddrStreetAddr2Txt().isEmpty()) {
            cdaPatientField.setAddress2(cdaPatientField.getAddress2() + patient.getPatAddrStreetAddr2Txt());
        }
        else if(field.getName().equals("patAddrCityTxt") && patient.getPatAddrCityTxt() != null && !patient.getPatAddrCityTxt().isEmpty()) {
            clinicalDocument = checkPatientRoleAddrCity(clinicalDocument);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0)
                    .getCityArray(0).set(cdaMapHelper.mapToCData(patient.getPatAddrCityTxt()));
            cdaPatientField.setK(cdaPatientField.getK() + 1);
        }
        else if(field.getName().equals("patAddrStateCd") && patient.getPatAddrStateCd() != null && !patient.getPatAddrStateCd().isEmpty()) {
            clinicalDocument = checkPatientRoleAddrState(clinicalDocument);
            var nstate = this.cdaMapHelper.mapToAddressType(patient.getPatAddrStateCd(), STATE);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStateArray(0).set(cdaMapHelper.mapToCData(nstate));
            cdaPatientField.setK(cdaPatientField.getK() + 1);
        }
        else if(field.getName().equals("patAddrZipCodeTxt") && patient.getPatAddrZipCodeTxt() != null && !patient.getPatAddrZipCodeTxt().isEmpty()) {
            clinicalDocument = checkPatientRoleAddrPostal(clinicalDocument);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getPostalCodeArray(0).set(cdaMapHelper.mapToCData(patient.getPatAddrZipCodeTxt()));
            cdaPatientField.setK(cdaPatientField.getK() + 1);
        }
        else if(field.getName().equals("patAddrCountyCd") && patient.getPatAddrCountyCd() != null && !patient.getPatAddrCountyCd().isEmpty()) {
            clinicalDocument = checkPatientRoleAddrCounty(clinicalDocument);
            var val = this.cdaMapHelper.mapToAddressType(patient.getPatAddrCountyCd(), COUNTY);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountyArray(0).set(cdaMapHelper.mapToCData(val));
            cdaPatientField.setK(cdaPatientField.getK() + 1);
        }
        else if(field.getName().equals("patAddrCountryCd") && patient.getPatAddrCountryCd() != null && !patient.getPatAddrCountryCd().isEmpty()) {
            clinicalDocument = checkPatientRoleAddrCountry(clinicalDocument);
            var val = this.cdaMapHelper.mapToAddressType(patient.getPatAddrCountryCd(), COUNTRY);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountryArray(0).set(cdaMapHelper.mapToCData(val));
            cdaPatientField.setK(cdaPatientField.getK() + 1);
        }
        else if (field.getName().equals("patWorkPhoneExtensionTxt") && patient.getPatWorkPhoneExtensionTxt() != null) {
            cdaPatientField.setWorkPhoneExt(patient.getPatWorkPhoneExtensionTxt().toString());
        }
        else if (field.getName().equals("patHomePhoneNbrTxt") && patient.getPatHomePhoneNbrTxt() != null) {
            cdaPatientField.setHomePhoneNumber(patient.getPatHomePhoneNbrTxt());
        }
        else if (field.getName().equals("patWorkPhoneNbrTxt") && patient.getPatWorkPhoneNbrTxt() != null) {
            cdaPatientField.setWpNumber(patient.getPatWorkPhoneNbrTxt());
        }
        else if (field.getName().equals("patPhoneCountryCodeTxt") && patient.getPatPhoneCountryCodeTxt() != null) {
            cdaPatientField.setPhoneCountryCode(patient.getPatPhoneCountryCodeTxt().toString());
        }
        else if (field.getName().equals("patCellPhoneNbrTxt") && patient.getPatCellPhoneNbrTxt() != null) {
            cdaPatientField.setCellNumber(patient.getPatCellPhoneNbrTxt());
        }
        else if (field.getName().equals("patNamePrefixCd") && patient.getPatNamePrefixCd() != null && !patient.getPatNamePrefixCd().trim().isEmpty()) {
            cdaPatientField.setPtPrefix(patient.getPatNamePrefixCd());
        }
        else if (field.getName().equals("patNameFirstTxt") && patient.getPatNameFirstTxt() != null && !patient.getPatNameFirstTxt().trim().isEmpty()) {
            cdaPatientField.setPtFirstName(patient.getPatNameFirstTxt());
        }
        else if (field.getName().equals("patNameMiddleTxt") && patient.getPatNameMiddleTxt() != null && !patient.getPatNameMiddleTxt().trim().isEmpty()) {
            cdaPatientField.setPtMiddleName(patient.getPatNameMiddleTxt());
        }
        else if (field.getName().equals("patNameLastTxt") && patient.getPatNameLastTxt() != null && !patient.getPatNameLastTxt().trim().isEmpty()) {
            cdaPatientField.setPtLastName(patient.getPatNameLastTxt());
        }
        else if (field.getName().equals("patNameSuffixCd") && patient.getPatNameSuffixCd() != null && !patient.getPatNameSuffixCd().trim().isEmpty()) {
            cdaPatientField.setPtSuffix(patient.getPatNameSuffixCd());
        }
        else if (field.getName().equals("patNameAliasTxt") && patient.getPatNameAliasTxt() != null && !patient.getPatNameAliasTxt().trim().isEmpty()) {
            clinicalDocument = checkPatientRoleAlias(clinicalDocument);

            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).setUse(new ArrayList<> (Arrays.asList("P")));
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).getGivenArray(0).set(cdaMapHelper.mapToCData(patient.getPatNameAliasTxt()));
        }
        else if(field.getName().equals("patCurrentSexCd") && patient.getPatCurrentSexCd() != null && !patient.getPatCurrentSexCd().isEmpty()) {
            String questionCode = this.cdaMapHelper.mapToQuestionId(patient.getPatCurrentSexCd());
            clinicalDocument = checkPatientRoleGenderCode(clinicalDocument);
            CE administrativeGender = this.cdaMapHelper.mapToCEAnswerType(patient.getPatCurrentSexCd(), questionCode);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setAdministrativeGenderCode(administrativeGender);
        }
        else if(field.getName().equals("patBirthDt") && patient.getPatBirthDt() != null) {
            clinicalDocument = checkPatientRole(clinicalDocument);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setBirthTime(cdaMapHelper.mapToTsType(patient.getPatBirthDt().toString()));
        }
        else if(field.getName().equals("patMaritalStatusCd") && patient.getPatMaritalStatusCd() != null  && !patient.getPatMaritalStatusCd().isEmpty()) {
            String questionCode = this.cdaMapHelper.mapToQuestionId(patient.getPatMaritalStatusCd());
            CE ce = this.cdaMapHelper.mapToCEAnswerType(patient.getPatMaritalStatusCd(), questionCode);
            clinicalDocument = checkPatientRole(clinicalDocument);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setMaritalStatusCode(ce);
        }
        else if(field.getName().equals("patRaceCategoryCd") && patient.getPatRaceCategoryCd() != null  && !patient.getPatRaceCategoryCd().isEmpty()) {
            clinicalDocument = mapPatientRaceCategory(patient, clinicalDocument);
        }
        else if(field.getName().equals("patRaceDescTxt") && patient.getPatRaceDescTxt() != null  && !patient.getPatRaceDescTxt().isEmpty()) {
            clinicalDocument = mapToPatientRaceDesc(clinicalDocument, patient);
        }
        else if(field.getName().equals("patEthnicGroupIndCd") && patient.getPatEthnicGroupIndCd() != null  && !patient.getPatEthnicGroupIndCd().isEmpty()) {
            clinicalDocument = checkPatientRole(clinicalDocument);
            String questionCode = this.cdaMapHelper.mapToQuestionId(patient.getPatEthnicGroupIndCd());
            CE ce = this.cdaMapHelper.mapToCEAnswerType(patient.getPatEthnicGroupIndCd(), questionCode);

            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setEthnicGroupCode(ce);
        }
        else if(field.getName().equals("patBirthCountryCd") && patient.getPatBirthCountryCd() != null  && !patient.getPatBirthCountryCd().isEmpty()) {
            clinicalDocument = checkPatientRoleBirthCountry(clinicalDocument);
            String val = this.cdaMapHelper.mapToAddressType(patient.getPatBirthCountryCd(), COUNTRY);
            POCDMT000040Place place = POCDMT000040Place.Factory.newInstance();
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getBirthplace().setPlace(place);

            AD ad = AD.Factory.newInstance();
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getBirthplace().getPlace().setAddr(ad);

            AdxpCounty county = AdxpCounty.Factory.newInstance();

            XmlCursor cursor = county.newCursor();
            cursor.setTextValue(CDATA + val + CDATA);
            cursor.dispose();

            AdxpCounty[] countyArr = {county};
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getBirthplace().getPlace().getAddr().setCountyArray(countyArr);
        }
        else if(field.getName().equals("patAddrCensusTractTxt") && patient.getPatAddrCensusTractTxt() != null  && !patient.getPatAddrCensusTractTxt().isEmpty()) {
            clinicalDocument = mapPatientAddrCensusTract(clinicalDocument, patient);
            cdaPatientField.setK(cdaPatientField.getK() + 1);
        }
        else if (field.getName().equals("patEmailAddressTxt") && patient.getPatEmailAddressTxt() != null && !patient.getPatEmailAddressTxt().trim().isEmpty()) {
            cdaPatientField.setEmail(patient.getPatEmailAddressTxt());
        }
        else if (field.getName().equals("patUrlAddressTxt") && patient.getPatUrlAddressTxt() != null && !patient.getPatUrlAddressTxt().trim().isEmpty()) {
            cdaPatientField.setUrlAddress(patient.getPatUrlAddressTxt());
        }
        else if(field.getName().equals("patNameAsOfDt") && patient.getPatNameAsOfDt() != null) {
            clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
            PN pn = PN.Factory.newInstance();
            IVLTS time = IVLTS.Factory.newInstance();
            var ts = cdaMapHelper.mapToTsType(patient.getPatNameAsOfDt().toString());

            XmlCursor cursor = time.newCursor();
            cursor.toEndDoc();

            cursor.beginElement("low");
            cursor.insertAttributeWithValue(VALUE_NAME,  ts.getValue());

            cursor.toEndDoc();
            cursor.removeXml();
            cursor.dispose();


            pn.setValidTime(time);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setNameArray(0, pn);
        }
        else if (field.getName().equals("patPhoneAsOfDt") &&  patient.getPatPhoneAsOfDt() != null) {
            cdaPatientField.setPhoneAsDateTime(patient.getPatPhoneAsOfDt().toString());
        }
        else if ( validatePatientGenericField(field, patient) ) {
            var mapValue = getPatientVariableNameAndValue(field, patient);
            String colName = mapValue.getColName();
            String value = mapValue.getValue();
            var  clinicalDocumentMapper = mapPatientStructureComponent(cdaPatientField.getPatientComponentCounter(), clinicalDocument, cdaPatientField.getInv168());
            clinicalDocument = clinicalDocumentMapper.getClinicalDocument();
            cdaPatientField.setPatientComponentCounter(clinicalDocumentMapper.getPatientComponentCounter());
            POCDMT000040Component3 comp = clinicalDocument.getComponent().getStructuredBody().getComponentArray(cdaPatientField.getPatientComponentCounter());
            int patEntityCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(0).getSection().getEntryArray().length;
            var compPatient = mapToPatientNested(patEntityCounter, colName, value, comp);
            clinicalDocument.getComponent().getStructuredBody().setComponentArray(cdaPatientField.getPatientComponentCounter(), compPatient);
        }

        cdaPatientField.setClinicalDocument(clinicalDocument);
        return cdaPatientField;
    }

    private POCDMT000040ClinicalDocument1 patientFieldCheckKMapping(int k, POCDMT000040ClinicalDocument1 clinicalDocument, EcrMsgPatientDto patient, Field field) throws EcrCdaXmlException {
        if (k > 1) {
            clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).setUse(new ArrayList<>(Arrays.asList("H")));
        }
        if (k> 1 && field.getName().equals("patAddrAsOfDt") && patient.getPatAddrAsOfDt() != null ) {
            clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
            AD element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0);
            var ad = cdaMapHelper.mapToUsableTSElement(patient.getPatAddrAsOfDt().toString(), element, USESABLE_PERIOD);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().setAddrArray(0, (AD) ad);
        }

        return clinicalDocument;
    }

    private CdaPatientField mapPatientPersonalInfo(POCDMT000040ClinicalDocument1 clinicalDocument, CdaPatientField patientField,
                                        String homeExtn, int phoneCounter) throws EcrCdaXmlException {
        if(!patientField.getPtPrefix().isEmpty()) {
            clinicalDocument = checkPatientRolePrefix(clinicalDocument);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getPrefixArray(0).set(cdaMapHelper.mapToCData(patientField.getPtPrefix()));
        }
        if(!patientField.getPtFirstName().isEmpty()) {
            clinicalDocument = mapPatientFirstName(clinicalDocument,
                    patientField.getPtFirstName());
        }
        if(!patientField.getPtMiddleName().isEmpty()) {
            clinicalDocument = mapPatientMiddleName(clinicalDocument,
                    patientField.getPtMiddleName());
        }

        if(!patientField.getPtLastName().isEmpty()) {
            clinicalDocument = checkPatientRoleFamilyName(clinicalDocument);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getFamilyArray(0).set(cdaMapHelper.mapToCData(patientField.getPtLastName()));
        }
        if(!patientField.getPtSuffix().isEmpty()) {
            clinicalDocument = checkPatientRoleSuffix(clinicalDocument);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getSuffixArray(0).set(cdaMapHelper.mapToCData(patientField.getPtSuffix()));
        }
        if (!patientField.getHomePhoneNumber().isEmpty()) {
            clinicalDocument =  mapPatientHomePhoneNumber(
                    clinicalDocument,
                    patientField.getPhoneCountryCode(),
                    patientField.getHomePhoneNumber(),
                    patientField.getPhoneAsDateTime(),
                    homeExtn);
            phoneCounter =phoneCounter +1;
        }

        return new CdaPatientField(clinicalDocument, phoneCounter);
    }

    private boolean validatePatientGenericField(Field field, EcrMsgPatientDto patient) {
        switch (field.getName()) {
            case "patInfoAsOfDt":
                return patient.getPatInfoAsOfDt() != null;
            case "patAddrCommentTxt":
                return patient.getPatAddrCommentTxt() != null;
            case "patAdditionalGenderTxt":
                return patient.getPatAdditionalGenderTxt() != null;
            case "patSpeaksEnglishIndCd":
                return patient.getPatSpeaksEnglishIndCd() != null;
            case "patIdStateHivCaseNbrTxt":
                return patient.getPatIdStateHivCaseNbrTxt() != null;
            case "patEthnicityUnkReasonCd":
                return patient.getPatEthnicityUnkReasonCd() != null;
            case "patSexUnkReasonCd":
                return patient.getPatSexUnkReasonCd() != null;
            case "patPhoneCommentTxt":
                return patient.getPatPhoneCommentTxt() != null;
            case "patDeceasedIndCd":
                return patient.getPatDeceasedIndCd() != null;
            case "patPreferredGenderCd":
                return patient.getPatPreferredGenderCd() != null;
            case "patReportedAgeUnitCd":
                return patient.getPatReportedAgeUnitCd() != null;
            case "patCommentTxt":
                return patient.getPatCommentTxt() != null;
            case "patBirthSexCd":
                return patient.getPatBirthSexCd() != null;
            case "patDeceasedDt":
                return patient.getPatDeceasedDt() != null;
            case "patReportedAge":
                return patient.getPatReportedAge() != null;
            default:
                return false;
        }
    }

    private ValueMapper getPatientVariableNameAndValue(Field field,
                                                  EcrMsgPatientDto patient) {
        String colName = "";
        String value = "";
        if (field.getName().equals("patInfoAsOfDt") && isFieldValid(patient.getPatInfoAsOfDt())) {
            colName = "PAT_INFO_AS_OF_DT";
            value = patient.getPatInfoAsOfDt().toString();
        } else if (field.getName().equals("patAddrCommentTxt") && isFieldValid(patient.getPatAddrCommentTxt())) {
            colName = "PAT_ADDR_COMMENT_TXT";
            value = patient.getPatAddrCommentTxt();
        } else if (field.getName().equals("patAdditionalGenderTxt") && isFieldValid(patient.getPatAdditionalGenderTxt())) {
            colName = "PAT_ADDITIONAL_GENDER_TXT";
            value = patient.getPatAdditionalGenderTxt();
        } else if (field.getName().equals("patSpeaksEnglishIndCd") && isFieldValid(patient.getPatSpeaksEnglishIndCd())) {
            colName = "PAT_SPEAKS_ENGLISH_IND_CD";
            value = patient.getPatSpeaksEnglishIndCd();
        } else if (field.getName().equals("patIdStateHivCaseNbrTxt") && isFieldValid(patient.getPatIdStateHivCaseNbrTxt())) {
            colName = "PAT_ID_STATE_HIV_CASE_NBR_TXT";
            value = patient.getPatIdStateHivCaseNbrTxt();
        } else if (field.getName().equals("patEthnicityUnkReasonCd") && isFieldValid(patient.getPatEthnicityUnkReasonCd())) {
            colName = "PAT_ETHNICITY_UNK_REASON_CD";
            value = patient.getPatEthnicityUnkReasonCd();
        } else if (field.getName().equals("patSexUnkReasonCd") && isFieldValid(patient.getPatSexUnkReasonCd())) {
            colName = "PAT_SEX_UNK_REASON_CD";
            value = patient.getPatSexUnkReasonCd();
        } else if (field.getName().equals("patPhoneCommentTxt") && isFieldValid(patient.getPatPhoneCommentTxt())) {
            colName = "PAT_PHONE_COMMENT_TXT";
            value = patient.getPatPhoneCommentTxt();
        } else if (field.getName().equals("patDeceasedIndCd") && isFieldValid(patient.getPatDeceasedIndCd())) {
            colName = "PAT_DECEASED_IND_CD";
            value = patient.getPatDeceasedIndCd();
        } else if (field.getName().equals("patDeceasedDt") && isFieldValid(patient.getPatDeceasedDt())) {
            colName = "PAT_DECEASED_DT";
            value = patient.getPatDeceasedDt().toString();
        } else if (field.getName().equals("patPreferredGenderCd") && isFieldValid(patient.getPatPreferredGenderCd())) {
            colName = "PAT_PREFERRED_GENDER_CD";
            value = patient.getPatPreferredGenderCd();
        } else if (field.getName().equals("patReportedAge") && patient.getPatReportedAge() != null) {
            colName = "PAT_REPORTED_AGE";
            value = String.valueOf(patient.getPatReportedAge());
        } else if (field.getName().equals("patReportedAgeUnitCd") && isFieldValid(patient.getPatReportedAgeUnitCd())) {
            colName = "PAT_REPORTED_AGE_UNIT_CD";
            value = patient.getPatReportedAgeUnitCd();
        } else if (field.getName().equals("patCommentTxt") && isFieldValid(patient.getPatCommentTxt())) {
            colName = "PAT_COMMENT_TXT";
            value = patient.getPatCommentTxt();
        } else if (field.getName().equals("patBirthSexCd") && isFieldValid(patient.getPatBirthSexCd())) {
            colName = "PAT_BIRTH_SEX_CD";
            value = patient.getPatBirthSexCd();
        }

        ValueMapper map = new ValueMapper();
        map.setColName(colName);
        map.setValue(value);
        return map;
    }

    private POCDMT000040ClinicalDocument1 mapPatientStructureComponentCompCheck(POCDMT000040ClinicalDocument1 clinicalDocument) {
        if (clinicalDocument.getComponent() == null) {
            clinicalDocument.addNewComponent();
        }

        if (!clinicalDocument.getComponent().isSetStructuredBody()) {
            clinicalDocument.getComponent().addNewStructuredBody();
        }
        return clinicalDocument;
    }

    private CdaPatientMapper mapPatientStructureComponent(
            int patientComponentCounter,
            POCDMT000040ClinicalDocument1 clinicalDocument,
            String inv168) throws EcrCdaXmlException {
        if (patientComponentCounter < 0 ) {

            clinicalDocument =  mapPatientStructureComponentCompCheck(clinicalDocument);

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

            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setRoot(ROOT_ID);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setExtension(inv168);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setAssigningAuthorityName("LR");
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCode("297622");
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystem(CODE_SYSTEM);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystemName(CODE_SYSTEM_NAME);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setDisplayName("Social History");
            clinicalDocument.getComponent().getStructuredBody()
                    .getComponentArray(patientComponentCounter).getSection().getTitle().set(cdaMapHelper.mapToCData("SOCIAL HISTORY INFORMATION"));

        }

        CdaPatientMapper mapper = new CdaPatientMapper();
        mapper.setClinicalDocument(clinicalDocument);
        mapper.setPatientComponentCounter(patientComponentCounter);
        return mapper;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleAddrArray(POCDMT000040ClinicalDocument1 clinicalDocument) {
        if (clinicalDocument.getRecordTargetArray(0)
                .getPatientRole().getAddrArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0)
                    .getPatientRole().addNewAddr();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRole(POCDMT000040ClinicalDocument1 clinicalDocument) {
        if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
            clinicalDocument.getRecordTargetArray(0)
                    .getPatientRole().addNewPatient();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkLanguageCode(POCDMT000040ClinicalDocument1 clinicalDocument) {
        if(!clinicalDocument.isSetLanguageCode()){
            clinicalDocument.addNewLanguageCode();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleAddrPostal(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getPostalCodeArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewPostalCode();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleAddrState(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStateArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewState();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleAddrCity(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCityArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCity();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleAddrCountry(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountryArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCountry();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleAddrCounty(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountyArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCounty();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1
        checkPatientRoleBirthCountry(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRole(clinicalDocument);
        if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().isSetBirthplace()) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewBirthplace();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleGenderCode(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRole(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().isSetAdministrativeGenderCode()) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewAdministrativeGenderCode();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleNameArray(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRole(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleFamilyName(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getFamilyArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewFamily();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleSuffix(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getSuffixArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewSuffix();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRolePrefix(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getPrefixArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewPrefix();
        }
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 checkPatientRoleAlias(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRole(clinicalDocument);

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
        return clinicalDocument;
    }

    private CdaPatientTelecom mapPatientWpNumber(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                       String wpNumber,
                                                       String PAT_WORK_PHONE_EXTENSION_TXT,
                                                       String PAT_PHONE_AS_OF_DT,
                                                       int phoneCounter) throws EcrCdaXmlException {
        CdaPatientTelecom param = new CdaPatientTelecom();
        try {
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
                    element.set(XmlObject.Factory.parse(STUD));
                    var out = cdaMapHelper.mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, USESABLE_PERIOD);
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<>(Arrays.asList("WP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(wpNumber);

                phoneCounter = phoneCounter +1;
            }

            param.setClinicalDocument(clinicalDocument);
            param.setPhoneCounter(phoneCounter);
            param.setWpNumber(wpNumber);
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

        return param;
    }

    private CdaPatientTelecom mapPatientCellPhone(POCDMT000040ClinicalDocument1 clinicalDocument,
                                           String cellNumber,
                                           String PAT_PHONE_AS_OF_DT,
                                           int phoneCounter) throws EcrCdaXmlException {
        CdaPatientTelecom model = new CdaPatientTelecom();
        try {
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
                    element.set(XmlObject.Factory.parse(STUD));
                    var out = cdaMapHelper.mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, USESABLE_PERIOD);
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<>(Arrays.asList("MC")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(cellNumber);
                phoneCounter =phoneCounter +1;
            }

            model.setPhoneCounter(phoneCounter);
            model.setClinicalDocument(clinicalDocument);
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

        return model;
    }

    private CdaPatientTelecom mapPatientEmail(POCDMT000040ClinicalDocument1 clinicalDocument,
                               String PAT_EMAIL_ADDRESS_TXT,
                                       String PAT_PHONE_AS_OF_DT,
                                       int phoneCounter) throws EcrCdaXmlException {
        CdaPatientTelecom model = new CdaPatientTelecom();
        try {
            if(!PAT_EMAIL_ADDRESS_TXT.isEmpty()) {
                int pCount = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                } else {
                    pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                }


                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList(Arrays.asList("HP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(MAIL_TO+PAT_EMAIL_ADDRESS_TXT);
                if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                    TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                    // CHECK mapToUsableTSElement
                    element.set(XmlObject.Factory.parse(STUD));
                    var out = cdaMapHelper.mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, USESABLE_PERIOD);
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<>(Arrays.asList("HP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(MAIL_TO + PAT_EMAIL_ADDRESS_TXT);
                phoneCounter =phoneCounter +1;
            }
            model.setClinicalDocument(clinicalDocument);
            model.setPhoneCounter(phoneCounter);
        } catch (Exception e ){
            throw new EcrCdaXmlException(e.getMessage());
        }
        return model;
    }

    private POCDMT000040ClinicalDocument1 mapPatientRaceCategory(EcrMsgPatientDto patient, POCDMT000040ClinicalDocument1 clinicalDocument) throws EcrCdaXmlException {
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
            String questionCode = this.cdaMapHelper.mapToQuestionId("PAT_RACE_CATEGORY_CD");
            if (!questionCode.isEmpty()) {
                CE ce = this.cdaMapHelper.mapToCEAnswerType(val, questionCode);
                raceCode2List.add(ce);
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setRaceCode2Array(raceCode2List.toArray(new CE[0]));
            }
        }
        return clinicalDocument;
    }

    private CdaPatientTelecom mapPatientUrlAddress(POCDMT000040ClinicalDocument1 clinicalDocument,
                                             String PAT_URL_ADDRESS_TXT,
                                                         String PAT_PHONE_AS_OF_DT,
                                                         int phoneCounter) throws EcrCdaXmlException {
        CdaPatientTelecom model = new CdaPatientTelecom();
        try {
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
                    element.set(XmlObject.Factory.parse(STUD));
                    var out = cdaMapHelper.mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, USESABLE_PERIOD);
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("HP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(PAT_URL_ADDRESS_TXT);
                phoneCounter =phoneCounter +1;
            }
            model.setClinicalDocument(clinicalDocument);
            model.setPhoneCounter(phoneCounter);
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
        return model;
    }

    private POCDMT000040ClinicalDocument1 mapPatientAddress1(POCDMT000040ClinicalDocument1 clinicalDocument,
                                          String address1) throws EcrCdaXmlException {
        try {
            if(!address1.isEmpty()) {
                int c1 = 0;
                int c2 = 0;
                clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                }
                else {
                    c2 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray();
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray(c2).set(cdaMapHelper.mapToCData(address1));
            }
            return clinicalDocument;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }

    private POCDMT000040ClinicalDocument1 mapPatientAddress2(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                                   String address2) throws EcrCdaXmlException {
        try {
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
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray(c2).set(cdaMapHelper.mapToCData(address2));
            }
            return clinicalDocument;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }

    private POCDMT000040ClinicalDocument1 mapToPatientRaceDesc(POCDMT000040ClinicalDocument1 clinicalDocument,
                                     EcrMsgPatientDto patient) {
        var counter = 0;
        clinicalDocument = checkPatientRole(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewRaceCode2();
        }
        else {
            counter = clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array().length + 1 - 1;
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewRaceCode2();
        }
        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array(counter).setCode("OTH");
        ED originalText = ED.Factory.newInstance();
        XmlCursor cursor = originalText.newCursor();
        cursor.toEndDoc();
        cursor.beginElement("originalText");
        cursor.insertChars(CDATA + patient.getPatRaceDescTxt() + CDATA);
        cursor.dispose();

        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array(counter).setOriginalText(originalText);

        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 mapPatientAddrCensusTract(POCDMT000040ClinicalDocument1 clinicalDocument,
                                          EcrMsgPatientDto patient) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);

        AdxpCensusTract census = AdxpCensusTract.Factory.newInstance();
        XmlCursor cursor = census.newCursor();
        cursor.setTextValue( CDATA + patient.getPatAddrCensusTractTxt() + CDATA);
        cursor.dispose();

        int c = 0;
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCensusTractArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCensusTract();
        } else {
            c = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCensusTractArray().length;
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCensusTract();
        }

        clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).setCensusTractArray(c,census);

        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 mapPatientFirstName(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                             String patientFirstName) throws EcrCdaXmlException {
        clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
        var count = 0;
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
        } else {
            count = clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length + 1 - 1;
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
        }

        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).setUse(new ArrayList(Arrays.asList("L")));
        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray(count).set(cdaMapHelper.mapToCData(patientFirstName));
        return clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 mapPatientMiddleName(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                             String patientMiddleName) throws EcrCdaXmlException {
        clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
        var count = 0;
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
        } else {
            count = clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length + 1 - 1;
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
        }
        clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray(count).set(cdaMapHelper.mapToCData(patientMiddleName));

        return  clinicalDocument;
    }

    private POCDMT000040ClinicalDocument1 mapPatientHomePhoneNumber(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                          String phoneCountryCode,
                                                           String homePhoneNumber,
                                                           String phoneAsDt,
                                                           String homeExtn) throws EcrCdaXmlException {
        try {
            int pCount = 0;
            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
            } else {
                pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
            }

            String phoneHome;
            if(!phoneCountryCode.isEmpty()) {
                homePhoneNumber =  "+"+phoneCountryCode+"-"+homePhoneNumber;
            }
            int homeExtnSize = homeExtn.length();
            if(homeExtnSize>0){
                phoneHome=homePhoneNumber+ ";ext="+ homeExtn;
            }
            else {
                phoneHome=homePhoneNumber;
            }
            if(!phoneAsDt.isEmpty()){
                TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                element.set(XmlObject.Factory.parse(STUD));
                var out = cdaMapHelper.mapToUsableTSElement(phoneAsDt, element, USESABLE_PERIOD);
                clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
            }
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("HP")));
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(phoneHome);
            return clinicalDocument;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }


    }

    private POCDMT000040Component3 mapToPatientNested(int counter, String colName, String data, POCDMT000040Component3 component3) throws EcrCdaXmlException {
        var questionCode = this.cdaMapHelper.mapToQuestionId(colName);
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
        observation = this.cdaMapHelper.mapToObservation(questionCode, data, observation);

        component3.getSection().getEntryArray(counter).setObservation(observation);
        return component3;
    }
    
    private boolean isFieldValid(String fieldValue) {
        return fieldValue != null && !fieldValue.isEmpty();
    }

    private boolean isFieldValid(Date fieldValue) {
        return fieldValue != null;
    }

}
