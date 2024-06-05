package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NokMatchingBaseService extends PatientMatchingBaseService {
    private static final Logger logger = LoggerFactory.getLogger(NokMatchingBaseService.class);

    public NokMatchingBaseService(EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
                                  EntityHelper entityHelper, PatientRepositoryUtil patientRepositoryUtil,
                                  CachingValueService cachingValueService,
                                  PrepareAssocModelHelper prepareAssocModelHelper) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService, prepareAssocModelHelper);
    }

    protected void setPersonHashCdNok(PersonContainer personContainer) throws DataProcessingException {
        try {
            long personUid = personContainer.getThePersonDto().getPersonParentUid();

            // DELETE Patient Matching Hash String
            getEdxPatientMatchRepositoryUtil().deleteEdxPatientMatchDTColl(personUid);
            try {
                if(personContainer.getThePersonDto().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)){
                    personContainer.getThePersonDto().setPersonUid(personUid);
                    // INSERTING Patient Matching Hash String
                    setPersonToMatchEntityNok(personContainer);
                }
            } catch (Exception e) {
                //per defect #1836 change to warning..
                logger.warn("Unable to setPatientHashCd for personUid: "+personUid);
                logger.warn("Exception in setPatientToEntityMatch -> unhandled exception: " +e.getMessage());
            }
        } catch (Exception e) {
            logger.error("EntityControllerEJB.setPatientHashCd: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }
    protected List<String> nameAddressStreetOneNOK(PersonContainer personContainer) {
        String nameAddStr = null;
        String carrot = "^";
        List<String> nameAddressStreetOnelNOKist = new ArrayList<>();
        if (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && personContainer.getTheEntityLocatorParticipationDtoCollection().size() > 0) {

            for (EntityLocatorParticipationDto entLocPartDT : personContainer.getTheEntityLocatorParticipationDtoCollection()) {
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getRecordStatusCd() != null
                        && entLocPartDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE) && entLocPartDT.getClassCd().equals(
                        NEDSSConstant.POSTAL)) {
                    if (entLocPartDT.getCd() != null) {
                        PostalLocatorDto postLocDT = entLocPartDT.getThePostalLocatorDto();
                        if (postLocDT != null) {
                            if ((postLocDT.getStreetAddr1() != null && !postLocDT.getStreetAddr1().equals(""))
                                    && (postLocDT.getCityDescTxt() != null && !postLocDT.getCityDescTxt().equals(""))
                                    && (postLocDT.getStateCd() != null && !postLocDT.getStateCd().equals("")) && (postLocDT.getZipCd() != null
                                    && !postLocDT.getZipCd().equals(""))) {

                                nameAddStr = carrot + postLocDT.getStreetAddr1() + carrot + postLocDT.getCityDescTxt() + carrot
                                        + postLocDT.getStateCd() + carrot + postLocDT.getZipCd();
                            }
                        }
                    }
                }
            }
            if (nameAddStr != null)
            {
                nameAddStr = getNamesStr(personContainer) + nameAddStr;
            }
            nameAddressStreetOnelNOKist.add(nameAddStr);

        }

        return nameAddressStreetOnelNOKist;
    }
    protected List<String> telePhoneTxtNOK(PersonContainer personContainer) {
        String nameTeleStr = null;
        String carrot = "^";
        List<String> telePhoneTxtList = new ArrayList<>();
        if (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && personContainer.getTheEntityLocatorParticipationDtoCollection().size() > 0) {
            for (EntityLocatorParticipationDto entLocPartDT : personContainer.getTheEntityLocatorParticipationDtoCollection()) {
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.TELE)
                        && entLocPartDT.getRecordStatusCd() != null && entLocPartDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    if (entLocPartDT.getCd() != null) {
                        TeleLocatorDto teleLocDT = entLocPartDT.getTheTeleLocatorDto();
                        if (teleLocDT != null && teleLocDT.getPhoneNbrTxt() != null && !teleLocDT.getPhoneNbrTxt().equals("")) {
                            nameTeleStr = carrot + teleLocDT.getPhoneNbrTxt();
                        }

                    }
                    if (nameTeleStr != null) {

                        if (getNamesStr(personContainer) != null) {
                            nameTeleStr = getNamesStr(personContainer) + nameTeleStr;
                            telePhoneTxtList.add(nameTeleStr);
                        } else {
                            return null;
                        }
                    }
                }

            }
        }

        return telePhoneTxtList;
    }
    private void setPersonToMatchEntityNok(PersonContainer personContainer) throws DataProcessingException {
        Long patientUid = personContainer.getThePersonDto().getPersonUid();
        EdxPatientMatchDto edxPatientMatchDto;
        String cdDescTxt = personContainer.thePersonDto.getCdDescTxt();
        if (cdDescTxt != null && cdDescTxt.equalsIgnoreCase(EdxELRConstant.ELR_NOK_DESC)) {
            String nameAddStrSt1 ;
            int nameAddStrSt1hshCd;
            List<String> nameAddressStreetOneStrList = nameAddressStreetOneNOK(personContainer);
            if (nameAddressStreetOneStrList != null
                    && !nameAddressStreetOneStrList.isEmpty()) {
                for (String s : nameAddressStreetOneStrList) {
                    nameAddStrSt1 = s;
                    if (nameAddStrSt1 != null) {
                        nameAddStrSt1 = nameAddStrSt1.toUpperCase();
                        nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
                        if (nameAddStrSt1 != null) {
                            edxPatientMatchDto = new EdxPatientMatchDto();
                            edxPatientMatchDto.setPatientUid(patientUid);
                            edxPatientMatchDto.setTypeCd(NEDSSConstant.NOK);
                            edxPatientMatchDto.setMatchString(nameAddStrSt1);
                            edxPatientMatchDto.setMatchStringHashCode((long) nameAddStrSt1hshCd);
                            try {
                                getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDto);
                            } catch (Exception e) {
                                logger.error("Error in creating the setEdxPatientMatchDT with nameAddString:" + nameAddStrSt1 + " " + e.getMessage());
                                throw new DataProcessingException(e.getMessage(), e);
                            }
                        }

                    }
                }
            }
            List<String> nameTelePhoneStrList = telePhoneTxtNOK(personContainer);
            String nameTelePhone;
            int nameTelePhonehshCd;
            if (nameTelePhoneStrList != null && !nameTelePhoneStrList.isEmpty()) {
                for (String s : nameTelePhoneStrList) {
                    nameTelePhone = s;
                    if (nameTelePhone != null) {
                        nameTelePhone = nameTelePhone.toUpperCase();
                        nameTelePhonehshCd = nameTelePhone.hashCode();
                        if (nameTelePhone != null) {
                            edxPatientMatchDto = new EdxPatientMatchDto();
                            edxPatientMatchDto.setPatientUid(patientUid);
                            edxPatientMatchDto.setTypeCd(NEDSSConstant.NOK);
                            edxPatientMatchDto.setMatchString(nameTelePhone);
                            edxPatientMatchDto.setMatchStringHashCode((long) nameTelePhonehshCd);
                            try {
                                getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDto);
                            } catch (Exception e) {
                                logger.error("Error in creating the EdxEntityMatchDT with nameTelePhone:" + nameTelePhone + " " + e.getMessage());
                                throw new DataProcessingException(e.getMessage(), e);
                            }
                        }

                    }
                }// for loop
            }

        }// end of method
    }

}
