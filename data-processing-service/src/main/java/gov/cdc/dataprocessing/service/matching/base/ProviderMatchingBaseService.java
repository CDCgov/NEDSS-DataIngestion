package gov.cdc.dataprocessing.service.matching.base;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Person;
import gov.cdc.dataprocessing.service.CheckingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.model.Coded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
public class ProviderMatchingBaseService extends MatchingBaseService{
    private static final Logger logger = LoggerFactory.getLogger(ProviderMatchingBaseService.class);

    public ProviderMatchingBaseService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CheckingValueService checkingValueService) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, checkingValueService);
    }

    protected String telePhoneTxtProvider(PersonVO personVO) {
        String nameTeleStr = null;
        String carrot = "^";

        if (personVO.getTheEntityLocatorParticipationDTCollection() != null
                && personVO.getTheEntityLocatorParticipationDTCollection().size() > 0) {
            Iterator<EntityLocatorParticipationDT> addIter = personVO.getTheEntityLocatorParticipationDTCollection().iterator();
            while (addIter.hasNext()) {
                EntityLocatorParticipationDT entLocPartDT = (EntityLocatorParticipationDT) addIter.next();
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.TELE)) {
                    if (entLocPartDT.getCd() != null && entLocPartDT.getCd().equals(NEDSSConstant.PHONE)) {
                        TeleLocatorDT teleLocDT = entLocPartDT.getTheTeleLocatorDT();
                        if (teleLocDT != null && teleLocDT.getPhoneNbrTxt() != null && !teleLocDT.getPhoneNbrTxt().equals(""))
                            nameTeleStr = carrot + teleLocDT.getPhoneNbrTxt();

                    }
                }
            }
        }
        if (nameTeleStr != null)
        {
            nameTeleStr = getNameStringForProvider(personVO) + nameTeleStr;
        }
        return nameTeleStr;
    }
    // Creating string for name and address for providers
    protected String nameAddressStreetOneProvider(PersonVO personVO) {
        String nameAddStr = null;
        String carrot = "^";
        if (personVO.getTheEntityLocatorParticipationDTCollection() != null && personVO.getTheEntityLocatorParticipationDTCollection().size() > 0) {
            Iterator<EntityLocatorParticipationDT> addIter = personVO.getTheEntityLocatorParticipationDTCollection().iterator();
            while (addIter.hasNext()) {
                EntityLocatorParticipationDT entLocPartDT = (EntityLocatorParticipationDT) addIter.next();
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.POSTAL)) {
                    if (entLocPartDT.getCd() != null
                            && entLocPartDT.getCd().equals(NEDSSConstant.OFFICE_CD)
                            && entLocPartDT.getUseCd() != null
                            && entLocPartDT.getUseCd().equals(NEDSSConstant.WORK_PLACE)) {
                        PostalLocatorDT postLocDT = entLocPartDT.getThePostalLocatorDT();
                        if (postLocDT != null) {
                            if ((postLocDT.getStreetAddr1() != null && !postLocDT.getStreetAddr1().equals(""))
                                    && (postLocDT.getCityDescTxt() != null && !postLocDT.getCityDescTxt().equals(""))
                                    && (postLocDT.getStateCd() != null && !postLocDT.getStateCd().equals(""))
                                    && (postLocDT.getZipCd() != null && !postLocDT.getZipCd().equals(""))) {
                                nameAddStr = carrot
                                        + postLocDT.getStreetAddr1() + carrot
                                        + postLocDT.getCityDescTxt() + carrot
                                        + postLocDT.getStateCd() + carrot
                                        + postLocDT.getZipCd();
                            }
                        }
                    }
                }
            }

        }
        if (nameAddStr != null)
            nameAddStr = getNameStringForProvider(personVO) + nameAddStr;
        return nameAddStr;
    }
    protected Long processingProvider(PersonVO personVO, String businessObjLookupName, String businessTriggerCd) throws DataProcessingException {
        try {
            boolean callOrgHashCode= false;
            if(personVO.isItNew() && personVO.getThePersonDT().isItNew() && personVO.getThePersonDT().getElectronicInd().equalsIgnoreCase("Y")
                    && !personVO.getThePersonDT().isCaseInd()){
                callOrgHashCode= true;
                personVO.getThePersonDT().setEdxInd("Y");
            }
            long personUid= persistingProvider(personVO, "PROVIDER", businessTriggerCd );

            if(callOrgHashCode){
                try {
                    personVO.getThePersonDT().setPersonUid(personUid);
                    /**
                     * THIS CODE HAS THING TO DO WITH ORGANIZATION
                     * */
                    setProvidertoEntityMatch(personVO);
                } catch (Exception e) {
                    logger.error("EntityControllerEJB.setProvider method exception thrown for matching criteria:"+e);
                    throw new DataProcessingException("EntityControllerEJB.setProvider method exception thrown for matching criteria:"+e);
                }
            }
            return personUid;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }
    private Long persistingProvider(PersonVO personVO, String businessObjLookupName, String businessTriggerCd) throws DataProcessingException  {
        Long personUID =  -1L;
        String localId = "";
        boolean isELRCase = false;
        try {
            localId = personVO.getThePersonDT().getLocalId();
            if (localId == null) {
                personVO.getThePersonDT().setEdxInd("Y");
                isELRCase = true;
            }

            /**
             * TODO: double check this
             * */

            Collection<EntityLocatorParticipationDT> collParLocator = null;
            Collection<RoleDT> colRole = null;
            Collection<ParticipationDT> colPar = null;


            collParLocator = personVO.getTheEntityLocatorParticipationDTCollection();
            if (collParLocator != null) {
                getEntityHelper().iterateELPDTForEntityLocatorParticipation(collParLocator);
                personVO.setTheEntityLocatorParticipationDTCollection(collParLocator);
            }

            colRole = personVO.getTheRoleDTCollection();
            if (colRole != null) {
                getEntityHelper().iterateRDT(colRole);
                personVO.setTheRoleDTCollection(colRole);
            }
            colPar = personVO.getTheParticipationDTCollection();
            if (colPar != null) {
                getEntityHelper().iteratePDTForParticipation(colPar);
                personVO.setTheParticipationDTCollection(colPar);
            }

            getPatientRepositoryUtil().preparePersonNameBeforePersistence(personVO);

            if (personVO.isItNew()) {
                Person p = getPatientRepositoryUtil().createPerson(personVO);
                personUID = p.getPersonUid();
            }
            else {
                getPatientRepositoryUtil().updateExistingPerson(personVO);
                personUID = personVO.getThePersonDT().getPersonUid();

            }


        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return personUID;

    }
    private void setProvidertoEntityMatch(PersonVO personVO) throws Exception {

        Long entityUid = personVO.getThePersonDT().getPersonUid();
        String identifier = null;
        int identifierHshCd = 0;
        List identifierList = null;
        identifierList = getIdentifierForProvider(personVO);
        if (identifierList != null && !identifierList.isEmpty()) {
            for (int k = 0; k < identifierList.size(); k++) {
                identifier = (String) identifierList.get(k);
                if (identifier != null)
                {
                    identifier = identifier.toUpperCase();
                }
                identifierHshCd = identifier.hashCode();
                if (identifier != null) {
                    EdxEntityMatchDT edxEntityMatchDT = new EdxEntityMatchDT();
                    edxEntityMatchDT.setEntityUid(entityUid);
                    edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
                    edxEntityMatchDT.setMatchString(identifier);
                    edxEntityMatchDT.setMatchStringHashCode((long)identifierHshCd);
                    try {
                        getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDT);
                    } catch (Exception e) {
                        logger.error("Error in creating the EdxEntityMatchDT with identifier:" + identifier + " " + e.getMessage());
                        throw new DataProcessingException(e.getMessage(), e);
                    }
                }

            }

        }

        // Matching with name and address with street address1 alone
        String nameAddStrSt1 = null;
        int nameAddStrSt1hshCd = 0;
        nameAddStrSt1 = nameAddressStreetOneProvider(personVO);
        if (nameAddStrSt1 != null) {
            nameAddStrSt1 = nameAddStrSt1.toUpperCase();
            nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
        }

        // Continue for name Telephone with no extension
        String nameTelePhone = null;
        int nameTelePhonehshCd = 0;
        nameTelePhone = telePhoneTxtProvider(personVO);
        if (nameTelePhone != null) {
            nameTelePhone = nameTelePhone.toUpperCase();
            nameTelePhonehshCd = nameTelePhone.hashCode();
        }

        EdxEntityMatchDT edxEntityMatchDT = null;
        // Create the name and address with no street 2(only street1)
        if (nameAddStrSt1 != null) {
            edxEntityMatchDT = new EdxEntityMatchDT();
            edxEntityMatchDT.setEntityUid(entityUid);
            edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDT.setMatchString(nameAddStrSt1);
            edxEntityMatchDT.setMatchStringHashCode((long)nameAddStrSt1hshCd);
            try {
                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDT);
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameAddStrSt1:" + nameAddStrSt1 + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }

        }
        // Create the name and address with nameTelePhone
        if (nameTelePhone != null) {
            edxEntityMatchDT = new EdxEntityMatchDT();
            edxEntityMatchDT.setEntityUid(entityUid);
            edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDT.setMatchString(nameTelePhone);
            edxEntityMatchDT.setMatchStringHashCode((long)nameTelePhonehshCd);
            try {
                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDT);
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameTelePhone:" + nameTelePhone + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        if (edxEntityMatchDT != null) {
            getPatientRepositoryUtil().updateExistingPersonEdxIndByUid(edxEntityMatchDT.getEntityUid());
        }

    }
    private List<String> getIdentifierForProvider(PersonVO personVO) throws DataProcessingException {
        String carrot = "^";
        List<String> identifierList = new ArrayList<String>();
        String identifier = null;
        Collection<EntityIdDT> newEntityIdDTColl = new ArrayList<>();
        try{
            if (personVO.getTheEntityIdDTCollection() != null
                    && personVO.getTheEntityIdDTCollection().size() > 0) {
                Collection<EntityIdDT> entityIdDTColl = personVO.getTheEntityIdDTCollection();
                Iterator<EntityIdDT> entityIdIterator = entityIdDTColl.iterator();
                while (entityIdIterator.hasNext()) {
                    EntityIdDT entityIdDT = (EntityIdDT) entityIdIterator.next();
                    if ((entityIdDT.getStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_ACTIVE))) {
                        if ((entityIdDT.getRootExtensionTxt() != null)
                                && (entityIdDT.getTypeCd() != null)
                                && (entityIdDT.getAssigningAuthorityCd() != null)
                                && (entityIdDT.getAssigningAuthorityDescTxt() !=null)
                                && (entityIdDT.getAssigningAuthorityIdType() != null)) {
                            identifier = entityIdDT.getRootExtensionTxt()
                                    + carrot + entityIdDT.getTypeCd() + carrot
                                    + entityIdDT.getAssigningAuthorityCd()
                                    + carrot
                                    + entityIdDT.getAssigningAuthorityDescTxt()
                                    + carrot + entityIdDT.getAssigningAuthorityIdType();
                        }else {
                            try {

//                                Coded coded = new Coded();
//                                coded.setCode(entityIdDT.getAssigningAuthorityCd());
//                                coded.setCodesetName(NEDSSConstant.EI_AUTH_PRV);
//                                coded.setCodesetTableName(DataTable.CODE_VALUE_GENERAL);
//                                NotificationSRTCodeLookupTranslationDAOImpl lookupDAO = new NotificationSRTCodeLookupTranslationDAOImpl();
//                                lookupDAO.retrieveSRTCodeInfo(coded);

                                Coded coded = new Coded();
                                coded.setCode(entityIdDT.getAssigningAuthorityCd());
                                coded.setCodesetName(NEDSSConstant.EI_AUTH);
                                coded.setCodesetTableName("Code_value_general");

                                //TODO: This call out to code value general Repos and Caching the recrod
//                                NotificationSRTCodeLookupTranslationDAOImpl lookupDAO = new NotificationSRTCodeLookupTranslationDAOImpl();
//                                lookupDAO.retrieveSRTCodeInfo(coded);

//                                var codedValueGenralList = getCheckingValueService().findCodeValuesByCodeSetNmAndCode(coded.getCodesetName(), coded.getCode());



                                if (entityIdDT.getRootExtensionTxt() != null
                                        && entityIdDT.getTypeCd() != null
                                        && coded.getCode()!=null
                                        && coded.getCodeDescription()!=null
                                        && coded.getCodeSystemCd()!=null){
                                    identifier = entityIdDT.getRootExtensionTxt()
                                            + carrot + entityIdDT.getTypeCd() + carrot
                                            + coded.getCode() + carrot
                                            + coded.getCodeDescription() + carrot
                                            + coded.getCodeSystemCd();
                                }


                            }catch (Exception ex) {
                                String errorMessage = "The assigning authority "
                                        + entityIdDT.getAssigningAuthorityCd()
                                        + " does not exists in the system. ";
                                logger.debug(ex.getMessage() + errorMessage);
                            }
                        }
                        if (entityIdDT.getTypeCd()!=null && !entityIdDT.getTypeCd().equalsIgnoreCase("LR")) {
                            newEntityIdDTColl.add(entityIdDT);
                        }
                        if (identifier != null) {
                            identifierList.add(identifier);
                        }

                    }

                }

            }
            personVO.setTheEntityIdDTCollection(newEntityIdDTColl);

        }catch (Exception ex) {
            String errorMessage = "Exception while creating hashcode for Provider entity IDs . ";
            logger.debug(ex.getMessage() + errorMessage);
            throw new DataProcessingException(errorMessage, ex);
        }
        return identifierList;

    }
    // getting Last name,First name for the providers
    private String getNameStringForProvider(PersonVO personVO) {
        String nameStr = null;
        if (personVO.getThePersonNameDTCollection() != null && personVO.getThePersonNameDTCollection().size() > 0) {
            Collection<PersonNameDT> PersonNameDTColl = personVO.getThePersonNameDTCollection();
            Iterator<PersonNameDT> nameCollIter = PersonNameDTColl.iterator();
            while (nameCollIter.hasNext()) {
                PersonNameDT personNameDT = (PersonNameDT) nameCollIter.next();
                if (personNameDT.getNmUseCd() == null)
                {
                    String Message = "personNameDT.getNmUseCd() is null";
                    logger.debug(Message);
                }
                if (personNameDT.getNmUseCd() != null && personNameDT.getNmUseCd().equals(NEDSSConstant.LEGAL)) {
                    if (personNameDT.getLastNm() != null || personNameDT.getFirstNm() != null)
                        nameStr = personNameDT.getLastNm() + personNameDT.getFirstNm();
                }
            }
        }
        return nameStr;
    }

}
