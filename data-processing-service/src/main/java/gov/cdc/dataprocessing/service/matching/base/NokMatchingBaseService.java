package gov.cdc.dataprocessing.service.matching.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import gov.cdc.dataprocessing.model.classic_model.dto.EntityLocatorParticipationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.PostalLocatorDT;
import gov.cdc.dataprocessing.model.classic_model.dto.TeleLocatorDT;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.service.CheckingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class NokMatchingBaseService extends PatientMatchingBaseService {
    private static final Logger logger = LoggerFactory.getLogger(NokMatchingBaseService.class);

    public NokMatchingBaseService(EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
                                  EntityHelper entityHelper, PatientRepositoryUtil patientRepositoryUtil,
                                  CheckingValueService checkingValueService) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, checkingValueService);
    }

    protected void setPersonHashCdNok(PersonVO personVO) throws DataProcessingException {
        try {
            long personUid = personVO.getThePersonDT().getPersonParentUid();

            // DELETE Patient Matching Hash String
            getEdxPatientMatchRepositoryUtil().deleteEdxPatientMatchDTColl(personUid);
            try {
                if(personVO.getThePersonDT().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)){
                    personVO.getThePersonDT().setPersonUid(personUid);
                    // INSERTING Patient Matching Hash String
                    setPersonToMatchEntityNok(personVO);
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
    protected List<String> nameAddressStreetOneNOK(PersonVO personVO) {
        String nameAddStr = null;
        String carrot = "^";
        List<String> nameAddressStreetOnelNOKist = new ArrayList();
        if (personVO.getTheEntityLocatorParticipationDTCollection() != null && personVO.getTheEntityLocatorParticipationDTCollection().size() > 0) {
            Iterator<EntityLocatorParticipationDT> addIter = personVO.getTheEntityLocatorParticipationDTCollection().iterator();

            while (addIter.hasNext()) {
                EntityLocatorParticipationDT entLocPartDT = (EntityLocatorParticipationDT) addIter.next();
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getRecordStatusCd() !=null
                        && entLocPartDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE) && entLocPartDT.getClassCd().equals(
                        NEDSSConstant.POSTAL)) {
                    if (entLocPartDT.getCd() != null) {
                        PostalLocatorDT postLocDT = entLocPartDT.getThePostalLocatorDT();
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
                nameAddStr = getNamesStr(personVO) + nameAddStr;
            }
            nameAddressStreetOnelNOKist.add(nameAddStr);

        }

        return nameAddressStreetOnelNOKist;
    }
    protected List<String> telePhoneTxtNOK(PersonVO personVO) {
        String nameTeleStr = null;
        String carrot = "^";
        List<String> telePhoneTxtList = new ArrayList();
        if (personVO.getTheEntityLocatorParticipationDTCollection() != null && personVO.getTheEntityLocatorParticipationDTCollection().size() > 0) {
            Iterator<EntityLocatorParticipationDT> addIter = personVO.getTheEntityLocatorParticipationDTCollection().iterator();
            while (addIter.hasNext()) {
                EntityLocatorParticipationDT entLocPartDT = (EntityLocatorParticipationDT) addIter.next();
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.TELE)
                        && entLocPartDT.getRecordStatusCd()!=null && entLocPartDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    if (entLocPartDT.getCd() != null) {
                        TeleLocatorDT teleLocDT = entLocPartDT.getTheTeleLocatorDT();
                        if (teleLocDT != null && teleLocDT.getPhoneNbrTxt() != null && !teleLocDT.getPhoneNbrTxt().equals(""))
                        {
                            nameTeleStr = carrot + teleLocDT.getPhoneNbrTxt();
                        }

                    }
                    if (nameTeleStr != null) {

                        if (getNamesStr(personVO) != null) {
                            nameTeleStr = getNamesStr(personVO) + nameTeleStr;
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
    private void setPersonToMatchEntityNok(PersonVO personVO) throws DataProcessingException {
        Long patientUid = personVO.getThePersonDT().getPersonUid();
        EdxPatientMatchDT edxPatientMatchDT = new EdxPatientMatchDT();
        String cdDescTxt = personVO.thePersonDT.getCdDescTxt();
        if (cdDescTxt != null && cdDescTxt.equalsIgnoreCase(EdxELRConstant.ELR_NOK_DESC)) {
            String nameAddStrSt1 = null;
            int nameAddStrSt1hshCd = 0;
            List nameAddressStreetOneStrList = nameAddressStreetOneNOK(personVO);
            if (nameAddressStreetOneStrList != null
                    && !nameAddressStreetOneStrList.isEmpty()) {
                for (int k = 0; k < nameAddressStreetOneStrList.size(); k++) {
                    nameAddStrSt1 = (String) nameAddressStreetOneStrList.get(k);
                    if (nameAddStrSt1 != null) {
                        nameAddStrSt1 = nameAddStrSt1.toUpperCase();
                        nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
                        if (nameAddStrSt1 != null) {
                            edxPatientMatchDT = new EdxPatientMatchDT();
                            edxPatientMatchDT.setPatientUid(patientUid);
                            edxPatientMatchDT.setTypeCd(NEDSSConstant.NOK);
                            edxPatientMatchDT.setMatchString(nameAddStrSt1);
                            edxPatientMatchDT.setMatchStringHashCode((long)nameAddStrSt1hshCd);
                            try {
                                getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDT);
                            } catch (Exception e) {
                                logger.error("Error in creating the setEdxPatientMatchDT with nameAddString:" + nameAddStrSt1 + " " + e.getMessage());
                                throw new DataProcessingException(e.getMessage(), e);
                            }
                        }

                    }
                }
            }
            List nameTelePhoneStrList = telePhoneTxtNOK(personVO);
            String nameTelePhone = null;
            int nameTelePhonehshCd = 0;
            if (nameTelePhoneStrList != null && !nameTelePhoneStrList.isEmpty()) {
                for (int k = 0; k < nameTelePhoneStrList.size(); k++) {
                    nameTelePhone = (String) nameTelePhoneStrList.get(k);
                    if (nameTelePhone != null) {
                        nameTelePhone = nameTelePhone.toUpperCase();
                        nameTelePhonehshCd = nameTelePhone.hashCode();
                        if (nameTelePhone != null) {
                            edxPatientMatchDT = new EdxPatientMatchDT();
                            edxPatientMatchDT.setPatientUid(patientUid);
                            edxPatientMatchDT.setTypeCd(NEDSSConstant.NOK);
                            edxPatientMatchDT.setMatchString(nameTelePhone);
                            edxPatientMatchDT.setMatchStringHashCode((long)nameTelePhonehshCd);
                            try {
                                getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDT);
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
