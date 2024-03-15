package gov.cdc.dataprocessing.service.implementation.jurisdiction;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JurisdictionService implements IJurisdictionService {
    private StringBuffer detailError= null;
    private final PatientRepositoryUtil patientRepositoryUtil;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;

    public JurisdictionService(PatientRepositoryUtil patientRepositoryUtil,
                               OrganizationRepositoryUtil organizationRepositoryUtil) {
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
    }

    private HashMap<Object, Object> resolveLabReportJurisdiction(PersonContainer subject,
                                                                PersonContainer provider,
                                                                OrganizationContainer organizationContainer,
                                                                OrganizationContainer organizationContainer2) throws DataProcessingException {

        try {
            Collection<Object> subjectColl = null;
            Collection<Object> providerColl = null;
            Collection<Object> organizationColl = null;
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            detailError = new StringBuffer();
            String jurisdiction =null;
            //TODO: JURISDICTION
            //subjectColl = findJurisdictionForPatient(subject);

            // Check to see the subject size.  Only proceed if the subject size is not greater than 1.
            if (subjectColl.size() <= 1)
            {
                // Check the result to make sure that there is a value for the subject's jurisdiction.
                // If not then go and find the jurisdiction based on the provider
                if (subjectColl.size() == 1)
                {
                    Iterator<Object> iter = subjectColl.iterator();
                    jurisdiction = (String) iter.next();
                    map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
                }
                if (jurisdiction==null && provider!=null)
                {
                    //TODO: JURISDICTION
                    //providerColl = findJurisdictionForProvider(provider);
                    if(!(providerColl.size()==0))

                        // Check to see the provider size.  Only proceed if the provider size is not greater than 1.
                        if (providerColl.size() <= 1)
                        {
                            // Check the result to make sure that there is a value for the provider's jurisdiction.
                            // If not then go and find the jurisdiction based on the provider
                            if (providerColl.size() == 1)
                            {

                                Iterator<Object> iter = providerColl.iterator();
                                jurisdiction = (String) iter.next();
                                map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
                            }

                        }
                }
                if(jurisdiction==null){
                    if (organizationContainer != null)
                    {
                        //TODO: JURISDICTION
                        //organizationColl = findJurisdictionForOrganization(organizationContainer);
                    }
                    if (organizationColl != null)
                    {

                        // Check to see the organization size.  Only proceed if the organization size is not greater than 1.
                        if (organizationColl.size() <= 1)
                        {
                            // Check the result to make sure that there is a value for the organization's jurisdiction.
                            // If not then go and find the jurisdiction based on the organization
                            if (organizationColl.size() == 1)
                            {

                                Iterator<Object> iter = organizationColl.iterator();
                                jurisdiction = (String) iter.next();
                                map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
                            }
                        }
                    }
                }

                if (jurisdiction == null) {
                    organizationColl = null;
                    if (organizationContainer2 != null) {
                        //TODO: JURISDICTION
                        //organizationColl = findJurisdictionForOrganization(organizationContainer2);
                    }
                    if (organizationColl != null) {

                        // Check to see the organization size. Only proceed if the
                        // organization size is not greater than 1.
                        if (organizationColl.size() <= 1) {
                            // Check the result to make sure that there is a value
                            // for the organization's jurisdiction.
                            // If not then go and find the jurisdiction based on the
                            // organization
                            if (organizationColl.size() == 1) {

                                Iterator<Object> iter = organizationColl.iterator();
                                jurisdiction = (String) iter.next();
                                map.put(ELRConstant.JURISDICTION_HASHMAP_KEY,
                                        jurisdiction);
                            }
                        }
                    }
                }
            }

            detailError= null;
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataProcessingException(e.getMessage(), e);
        }
    }



    public String deriveJurisdictionCd(BaseContainer proxyVO, ObservationDto rootObsDT) throws DataProcessingException {
        try {
            //Retieve provider uid and patient uid
            Collection<ParticipationDto>  partColl = null;
            boolean isLabReport = false, isMorbReport = false;
            String jurisdictionDerivationInd = AuthUtil.authUser.getJurisdictionDerivationInd();
//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                isMorbReport = true;
//                partColl = ( (MorbidityProxyVO) proxyVO).getTheParticipationDtoCollection();
//            }


            if (proxyVO instanceof LabResultProxyContainer)
            {
                isLabReport = true;
                partColl = ( (LabResultProxyContainer) proxyVO).getTheParticipationDtoCollection();
            }
            if (partColl == null || partColl.size() <= 0)
            {
                throw new DataProcessingException("Participation collection is null or empty, it is: " + partColl);
            }

            Long providerUid = null;
            Long patientUid = null;
            Long orderingFacilityUid = null;
            Long reportingFacilityUid = null;

            for (ParticipationDto partDT : partColl) {
                if (partDT == null) {
                    continue;
                }

                String typeCd = partDT.getTypeCd();
                String subjectClassCd = partDT.getSubjectClassCd();
                if (typeCd != null && (typeCd.equalsIgnoreCase(NEDSSConstant.PAR101_TYP_CD)
                        || typeCd.equalsIgnoreCase(NEDSSConstant.MOB_PHYSICIAN_OF_MORB_REPORT))
                        && subjectClassCd != null && subjectClassCd.equalsIgnoreCase(NEDSSConstant.PERSON_CLASS_CODE)) {
                    providerUid = partDT.getSubjectEntityUid();
                } else if (typeCd != null
                        && (typeCd.equalsIgnoreCase(NEDSSConstant.PAR110_TYP_CD)
                        || typeCd.equalsIgnoreCase(NEDSSConstant.MOB_SUBJECT_OF_MORB_REPORT))) {
                    patientUid = partDT.getSubjectEntityUid();
                } else if (typeCd != null
                        && (typeCd.equalsIgnoreCase(NEDSSConstant.PAR102_TYP_CD))) {
                    orderingFacilityUid = partDT.getSubjectEntityUid();
                } else if (jurisdictionDerivationInd != null
                        && jurisdictionDerivationInd.equals(NEDSSConstant.YES)
                        && typeCd != null
                        && typeCd.equalsIgnoreCase(NEDSSConstant.PAR111_TYP_CD)
                        && subjectClassCd != null
                        && subjectClassCd.equalsIgnoreCase(NEDSSConstant.PAR111_SUB_CD)
                        && rootObsDT != null
                        && rootObsDT.getCtrlCdDisplayForm() != null
                        && rootObsDT.getCtrlCdDisplayForm().equalsIgnoreCase(NEDSSConstant.LAB_REPORT)
                        && rootObsDT.getElectronicInd() != null
                        && rootObsDT.getElectronicInd().equals(NEDSSConstant.EXTERNAL_USER_IND))
                    reportingFacilityUid = partDT.getSubjectEntityUid();
            }

            //Get the provider vo from db
            PersonContainer providerVO = null;
            OrganizationContainer orderingFacilityVO = null;
            OrganizationContainer reportingFacilityVO = null;
            try
            {
                if (providerUid != null)
                {
                    providerVO = patientRepositoryUtil.loadPerson(providerUid);
                }
                if (orderingFacilityUid != null)
                {
                    // orderingFacilityVO = getOrganization(orderingFacilityUid);
                    orderingFacilityVO = organizationRepositoryUtil.loadObject(orderingFacilityUid, null);
                }
                if(reportingFacilityUid!=null)
                {
                    // reportingFacilityVO = getOrganization(reportingFacilityUid);
                    orderingFacilityVO = organizationRepositoryUtil.loadObject(orderingFacilityUid, null);
                }
            }
            catch (Exception rex)
            {
                throw new DataProcessingException("Error retieving provider with UID:"+ providerUid +" OR Ordering Facility, its uid is: " + orderingFacilityUid);
            }

            //Get the patient subject
            PersonContainer patientVO = null;
            Collection<PersonContainer>  personVOColl = null;
            if (isLabReport)
            {
                personVOColl = ( (LabResultProxyContainer) proxyVO).getThePersonContainerCollection();
            }
//            if (isMorbReport)
//            {
//                personVOColl = ( (MorbidityProxyVO) proxyVO).getThePersonVOCollection();
//
//            }
            if (patientUid != null && personVOColl != null && personVOColl.size() > 0)
            {
                for (PersonContainer pVO : personVOColl) {
                    if (pVO == null || pVO.getThePersonDto() == null) {
                        continue;
                    }
                    if (pVO.getThePersonDto().getPersonUid().compareTo(patientUid) == 0) {
                        patientVO = pVO;
                        break;
                    }
                }
            }

            //Derive the jurisdictionCd
            Map<Object, Object> jMap = null;
            if (patientVO != null)
            {

                try
                {
                    //TODO: JURISDICTION
                    jMap = resolveLabReportJurisdiction(patientVO, providerVO, orderingFacilityVO, reportingFacilityVO);
                }
                catch (Exception cex)
                {
                    throw new DataProcessingException("Error creating jurisdiction services.");
                }
            }

            //set jurisdiction for order test
            if (jMap != null && jMap.containsKey(ELRConstant.JURISDICTION_HASHMAP_KEY))
            {
                rootObsDT.setJurisdictionCd( (String) jMap.get(ELRConstant.JURISDICTION_HASHMAP_KEY));
            }
            else
            {
                rootObsDT.setJurisdictionCd(null);
            }

            //Return errors if any
            if (jMap != null && jMap.containsKey("ERROR"))
            {
                return (String) jMap.get("ERROR");
            }
            else
            {
                return null;
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }


}
