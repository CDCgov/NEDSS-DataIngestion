package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

@Component
public class PersonUtil {

    private Long processLabPersonVOCollection(AbstractVO proxyVO) throws DataProcessingException {
        try {
            Collection<PersonContainer> personVOColl = null;
            boolean isMorbReport = false;

            //TODO: MORBIDITY
//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                personVOColl = ( (MorbidityProxyVO) proxyVO).getThePersonVOCollection();
//                isMorbReport = true;
//            }
            if (proxyVO instanceof LabResultProxyContainer)
            {
                personVOColl = ( (LabResultProxyContainer) proxyVO).getThePersonContainerCollection();
            }

            if (personVOColl == null)
            {
                throw new IllegalArgumentException("PersonVO collection is null");
            }

            PersonContainer personVO = null;
            Long patientMprUid = null;

            if (personVOColl != null && personVOColl.size() > 0)
            {
                for (Iterator<PersonContainer> anIterator = personVOColl.iterator(); anIterator.hasNext(); )
                {
                    personVO = (PersonContainer) anIterator.next();

                    if (personVO == null)
                    {
                        continue;
                    }

                    //Finds out the type of person being processed and if it is a new person object,
                    //and abort the processing if the parameters not provided or provided incorrectly
                    String personType = personVO.getThePersonDto().getCd();
                    boolean isNewVO = personVO.isItNew();

                    if (personType == null)
                    {
                        throw new DataProcessingException("Expected a non-null person type cd for this person uid: " + personVO.getThePersonDto().getPersonUid());
                    }

                    ObservationDT rootDT = getRootDT(proxyVO);

                    //Persists the person object
                    boolean isExternal = false;
                    String electronicInd = rootDT.getElectronicInd();
                    if(electronicInd != null
                            && ((isMorbReport && electronicInd.equals(NEDSSConstant.EXTERNAL_USER_IND))
                            || electronicInd.equals(NEDSSConstant.YES)))
                    {
                        isExternal = true;
                    }
                    Long realUid = null;
                    if(personVO.getRole()==null){
                        //TODO: INSERTION
                        realUid = setPerson(personType, personVO, isNewVO, isExternal);
                    }else{
                        realUid =personVO.getThePersonDto().getPersonUid();
                    }


                    //If it is a new person object, updates the associations with the newly created uid
                    if (isNewVO && realUid != null)
                    {
                        Long falseUid = personVO.getThePersonDto().getPersonUid();
                        logger.debug("false personUID: " + falseUid);
                        logger.debug("real personUID: " + realUid);

                        if (falseUid.intValue() < 0)
                        {
                            //TODO: FALSE TO NEW METHOD
                            this.setFalseToNew(proxyVO, falseUid, realUid);
                            //set the realUid to person after it has been set to participation
                            //this will help for jurisdiction derivation, this is only local to this call
                            personVO.getThePersonDto().setPersonUid(realUid);
                        }
                    }
                    else if (!isNewVO)
                    {
                        logger.debug("exisiting but updated person's UID: " + realUid);
                    }

                    //If it is patient, return the mpr uid, assuming only one patient in this processing
                    if (personType.equalsIgnoreCase(NEDSSConstant.PAT))
                    {
                        patientMprUid = patientRepositoryUtil.findPatientParentUidByUid(realUid);
                    }
                }
            }
            return patientMprUid;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


}
