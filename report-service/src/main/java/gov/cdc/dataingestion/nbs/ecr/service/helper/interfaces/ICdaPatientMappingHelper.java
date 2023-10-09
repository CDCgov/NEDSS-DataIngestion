package gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaPatientMapper;
import gov.cdc.dataingestion.nbs.ecr.model.ValueMapper;
import gov.cdc.dataingestion.nbs.ecr.model.patient.CdaPatientTelecom;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgPatientDto;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;

import java.lang.reflect.Field;

public interface ICdaPatientMappingHelper {
    boolean validatePatientGenericField(Field field, EcrMsgPatientDto patient);
    ValueMapper getPatientVariableNameAndValue(Field field,
                                               EcrMsgPatientDto patient);
    CdaPatientMapper mapPatientStructureComponent(
            int patientComponentCounter,
            POCDMT000040ClinicalDocument1 clinicalDocument,
            String inv168) throws EcrCdaXmlException;
    POCDMT000040ClinicalDocument1 checkPatientRoleAddrArray(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRole(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkLanguageCode(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleAddrPostal(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleAddrState(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleAddrCity(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleAddrCountry(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleAddrCounty(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1
    checkPatientRoleBirthCountry(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleGenderCode(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleNameArray(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleFamilyName(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleSuffix(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRolePrefix(POCDMT000040ClinicalDocument1 clinicalDocument);
    POCDMT000040ClinicalDocument1 checkPatientRoleAlias(POCDMT000040ClinicalDocument1 clinicalDocument);
    CdaPatientTelecom mapPatientWpNumber(POCDMT000040ClinicalDocument1 clinicalDocument,
                                         String wpNumber,
                                         String PAT_WORK_PHONE_EXTENSION_TXT,
                                         String PAT_PHONE_AS_OF_DT,
                                         int phoneCounter) throws EcrCdaXmlException;
    CdaPatientTelecom mapPatientCellPhone(POCDMT000040ClinicalDocument1 clinicalDocument,
                                          String cellNumber,
                                          String PAT_PHONE_AS_OF_DT,
                                          int phoneCounter) throws EcrCdaXmlException;
    CdaPatientTelecom mapPatientEmail(POCDMT000040ClinicalDocument1 clinicalDocument,
                                      String PAT_EMAIL_ADDRESS_TXT,
                                      String PAT_PHONE_AS_OF_DT,
                                      int phoneCounter) throws EcrCdaXmlException;
    POCDMT000040ClinicalDocument1 mapPatientRaceCategory(EcrMsgPatientDto patient,
                                POCDMT000040ClinicalDocument1 clinicalDocument);

    CdaPatientTelecom mapPatientUrlAddress(POCDMT000040ClinicalDocument1 clinicalDocument,
                                           String PAT_URL_ADDRESS_TXT,
                                           String PAT_PHONE_AS_OF_DT,
                                           int phoneCounter) throws EcrCdaXmlException;
    POCDMT000040ClinicalDocument1 mapPatientAddress1(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                     String address1) throws EcrCdaXmlException;

    POCDMT000040ClinicalDocument1 mapPatientAddress2(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                     String address2) throws EcrCdaXmlException;

    POCDMT000040ClinicalDocument1 mapToPatientRaceDesc(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                       EcrMsgPatientDto patient);

    POCDMT000040ClinicalDocument1 mapPatientAddrCensusTract(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                            EcrMsgPatientDto patient);

    POCDMT000040ClinicalDocument1 mapPatientFirstName(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                      String patientFirstName) throws EcrCdaXmlException;

    POCDMT000040ClinicalDocument1 mapPatientMiddleName(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                       String patientMiddleName) throws EcrCdaXmlException;

    POCDMT000040ClinicalDocument1 mapPatientHomePhoneNumber(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                            String phoneCountryCode,
                                                            String homePhoneNumber,
                                                            String phoneAsDt,
                                                            String homeExtn) throws EcrCdaXmlException;
}
