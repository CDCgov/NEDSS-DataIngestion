package gov.cdc.dataingestion.nbs.ecr.model.patient;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgPatientDto;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@NoArgsConstructor
@Getter
@Setter
/// TO USE LATER FOR REFACTOR
public class CdaPatientField {
    private Field[] fields;
    private int patientIdentifier;
    private String address1;
    private String address2;
    private int k;
    private int raceCodeCounter;
    private int patientComponentCounter;
    private String inv168;
    private EcrMsgPatientDto patient;
    private POCDMT000040ClinicalDocument1 clinicalDocument;

    private String PAT_WORK_PHONE_EXTENSION_TXT;
    private String PAT_HOME_PHONE_NBR_TXT;
    private String wpNumber;
    private String PAT_PHONE_COUNTRY_CODE_TXT;
    private String cellNumber;
    private String PAT_NAME_PREFIX_CD;
    private String PAT_NAME_FIRST_TXT;
    private String PAT_NAME_MIDDLE_TXT;
    private String PAT_NAME_LAST_TXT;
    private String PAT_NAME_SUFFIX_CD;
    private String PAT_EMAIL_ADDRESS_TXT;
    private String PAT_URL_ADDRESS_TXT;
    private String PAT_PHONE_AS_OF_DT;
}
