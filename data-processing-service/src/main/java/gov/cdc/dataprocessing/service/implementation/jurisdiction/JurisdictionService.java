package gov.cdc.dataprocessing.service.implementation.jurisdiction;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.srte.model.JurisdictionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.JurisdictionCodeRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.JurisdictionParticipationRepository;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.ERROR;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class JurisdictionService implements IJurisdictionService {
    private static final Logger logger = LoggerFactory.getLogger(JurisdictionService.class); // NOSONAR

    private StringBuilder detailError= null;
    private final PatientRepositoryUtil patientRepositoryUtil;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;
    private final JurisdictionParticipationRepository jurisdictionParticipationRepository;
    private final JurisdictionCodeRepository jurisdictionCodeRepository;

    public JurisdictionService(PatientRepositoryUtil patientRepositoryUtil,
                               OrganizationRepositoryUtil organizationRepositoryUtil,
                               JurisdictionParticipationRepository jurisdictionParticipationRepository,
                               JurisdictionCodeRepository jurisdictionCodeRepository) {
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
        this.jurisdictionParticipationRepository = jurisdictionParticipationRepository;
        this.jurisdictionCodeRepository = jurisdictionCodeRepository;
    }

    public List<JurisdictionCode> getJurisdictionCode() {
       var jusCode = jurisdictionCodeRepository.findAll();
       if (!jusCode.isEmpty()) {
           return jusCode;
       }
       else
       {
           return new ArrayList<>();
       }
    }

    public void assignJurisdiction(PersonContainer patientContainer, PersonContainer providerContainer,
                                   OrganizationContainer organizationContainer, ObservationContainer observationRequest) throws DataProcessingException {

        HashMap<String, String> jurisdictionMap = null;
        if (patientContainer != null) {
            jurisdictionMap = resolveLabReportJurisdiction(patientContainer, providerContainer, organizationContainer, null);
        }

        if (jurisdictionMap!=null && jurisdictionMap.get(ELRConstant.JURISDICTION_HASHMAP_KEY) != null) {
            String jurisdiction = jurisdictionMap.get(ELRConstant.JURISDICTION_HASHMAP_KEY);
            observationRequest.getTheObservationDto().setJurisdictionCd(jurisdiction);
        }
        else
        {
            observationRequest.getTheObservationDto().setJurisdictionCd(null);
        }

    } // end of assignJursidiction()



    @SuppressWarnings("java:S3776")
    public String deriveJurisdictionCd(BaseContainer proxyVO, ObservationDto rootObsDT) throws DataProcessingException {
        //Retieve provider uid and patient uid
        Collection<ParticipationDto>  partColl = null;
        boolean isLabReport = false;
        String jurisdictionDerivationInd = AuthUtil.authUser.getJurisdictionDerivationInd();

        if (proxyVO instanceof LabResultProxyContainer)
        {
            isLabReport = true;
            partColl = ( (LabResultProxyContainer) proxyVO).getTheParticipationDtoCollection();
        }
        if (partColl == null || partColl.isEmpty())
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
            //it was assigned to orderingFacilityVO in the first implementation.not sure if it was correct.
            reportingFacilityVO = organizationRepositoryUtil.loadObject(orderingFacilityUid, null);
        }

        //Get the patient subject
        PersonContainer patientVO = null;
        Collection<PersonContainer>  personVOColl = null;
        if (isLabReport)
        {
            personVOColl = ( (LabResultProxyContainer) proxyVO).getThePersonContainerCollection();
        }
        if (patientUid != null && personVOColl != null && !personVOColl.isEmpty())
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
        Map<String, String> jMap = null;
        if (patientVO != null)
        {

            try
            {
                jMap = resolveLabReportJurisdiction(patientVO, providerVO, orderingFacilityVO, reportingFacilityVO);
            }
            catch (Exception cex)
            {
                throw new DataProcessingException("Error creating jurisdiction services.");
            }
        }

        //set jurisdiction for order test
        if (rootObsDT != null) {
            if (jMap != null && jMap.containsKey(ELRConstant.JURISDICTION_HASHMAP_KEY))
            {
                rootObsDT.setJurisdictionCd(jMap.get(ELRConstant.JURISDICTION_HASHMAP_KEY));
            }
            else
            {
                rootObsDT.setJurisdictionCd(null);
            }
        }


        //Return errors if any
        if (jMap != null && jMap.containsKey(ERROR))
        {
            return jMap.get(ERROR);
        }
        else
        {
            return null;
        }

    }

    /**
     * Description: this method find jurisdiction associated with patient, provider, organization.
     * Jurisdiction is identified based on Postal locator
     * */
    @SuppressWarnings("java:S3776")
    private HashMap<String, String> resolveLabReportJurisdiction(PersonContainer patientContainer,
                                                                 PersonContainer providerContainer,
                                                                 OrganizationContainer organizationContainer,
                                                                 OrganizationContainer organizationContainer2) throws DataProcessingException {
        Collection<String> patientJurisdictionCollection;
        Collection<String> providerJurisdictionCollection;
        Collection<String> organizationJurisdictionCollection = null;
        HashMap<String, String> map = new HashMap<>();
        detailError = new StringBuilder();
        String jurisdiction =null;
        //Initial value was not set in the first implementation.
        detailError.append("Patient: ");
        patientJurisdictionCollection = findJurisdiction(patientContainer.getTheEntityLocatorParticipationDtoCollection(), "H", "PST");

        // Check to see the subject size.  Only proceed if the subject size is not greater than 1.
        if (patientJurisdictionCollection.size() <= 1)
        {
            // Check the result to make sure that there is a value for the subject's jurisdiction.
            // If not then go and find the jurisdiction based on the provider
            if (patientJurisdictionCollection.size() == 1)
            {
                Iterator<String> iter = patientJurisdictionCollection.iterator();
                jurisdiction = iter.next();
                map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
            }

            if (jurisdiction==null && providerContainer !=null)
            {
                detailError.append("Provider: ");
                providerJurisdictionCollection = findJurisdiction(providerContainer.getTheEntityLocatorParticipationDtoCollection(), "WP", "PST");
                if(providerJurisdictionCollection.size() == 1) {
                    // Check the result to make sure that there is a value for the provider's jurisdiction.
                    // If not then go and find the jurisdiction based on the provider
                    Iterator<String> iter = providerJurisdictionCollection.iterator();
                    jurisdiction = iter.next();
                    map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
                }
            }

            if(jurisdiction==null){
                if (organizationContainer != null)
                {
                    detailError.append("Ordering Facility: ");
                    organizationJurisdictionCollection = findJurisdiction(organizationContainer.getTheEntityLocatorParticipationDtoCollection(), "WP", "PST");
                }
                if (organizationJurisdictionCollection != null && organizationJurisdictionCollection.size() == 1)
                {
                    // Check the result to make sure that there is a value for the organization's jurisdiction.
                    // If not then go and find the jurisdiction based on the organization
                    Iterator<String> iter = organizationJurisdictionCollection.iterator();
                    jurisdiction = iter.next();
                    map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
                }
            }

            if (jurisdiction == null) {
                organizationJurisdictionCollection = null;
                if (organizationContainer2 != null) {
                    detailError.append("Ordering Facility: ");
                    organizationJurisdictionCollection = findJurisdiction(organizationContainer2.getTheEntityLocatorParticipationDtoCollection(), "WP", "PST");
                }
                if (organizationJurisdictionCollection != null && organizationJurisdictionCollection.size() == 1) {
                    // Check to see the organization size. Only proceed if the
                    // organization size is not greater than 1.
                    // Check the result to make sure that there is a value
                    // for the organization's jurisdiction.
                    // If not then go and find the jurisdiction based on the
                    // organization
                    Iterator<String> iter = organizationJurisdictionCollection.iterator();
                    jurisdiction = iter.next();
                    map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
                }
            }
        }

        detailError= null;
        return map;
    }

    @SuppressWarnings("java:S3776")
    private Collection<String> findJurisdiction(Collection<EntityLocatorParticipationDto> entityLocatorPartColl, String useCd, String classCd)
    {
            PostalLocatorDto postalDt;
            Collection<String> coll = new ArrayList<>();

            // Check to make sure that you are able to get a locator participation
            if (entityLocatorPartColl != null && !entityLocatorPartColl.isEmpty())
            {
                for (EntityLocatorParticipationDto dt : entityLocatorPartColl) {
                    // for subject the use code	= "H" class cd = "PST"
                    // for provider the use code = "W" class cd = "PST"
                    if (dt.getUseCd().equals(useCd) && dt.getClassCd().equals(classCd)) {
                        postalDt = dt.getThePostalLocatorDto();

                        // Parse the zip if is valid.
                        if (postalDt != null) {

                            String searchZip;
                            searchZip = parseZip(postalDt.getZipCd());

                            if (searchZip == null) {
                                searchZip = "NO ZIP";
                            }
                            detailError.append(searchZip);
                            detailError.append(", ");


                            // Attempt to find the jurisicition by zip code.  if you do not retrieve any
                            // data then attempt to retriece by county.  If no data then retrieve
                            // by city.

                            var res = jurisdictionParticipationRepository.findJurisdiction(searchZip, "Z");
                            if (res.isPresent()) {
                                coll = res.get();
                            }
                            if (coll.isEmpty()) {
                                String cityDesc = postalDt.getCityDescTxt();
                                if (cityDesc == null)
                                {
                                    cityDesc = "NO CITY";
                                }
                                detailError.append(cityDesc);
                                detailError.append(", ");
                                if (postalDt.getCityDescTxt() != null) {
                                    var resCity = jurisdictionParticipationRepository.findJurisdictionForCity(postalDt.getCityDescTxt(), postalDt.getStateCd(), "C");
                                    if (resCity.isPresent()) {
                                        coll = resCity.get();
                                    }

                                }
                                String countyDesc = postalDt.getCntyDescTxt();
                                if (countyDesc == null) {
                                    countyDesc = "NO COUNTY";
                                }
                                detailError.append(countyDesc);
                                detailError.append(", ");

                                if (coll.isEmpty()) {
                                    var resJus = jurisdictionParticipationRepository.findJurisdiction(postalDt.getCntyCd(), "N");
                                    if (resJus.isPresent()) {
                                        coll = resJus.get();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(!detailError.toString().equals("Provider: ")) {
                //this will remove the trailing ","
                String detail = detailError.substring(0,(detailError.toString().length() - 2));
                detail = detail + " ";
                detailError = new StringBuilder(detail);
            }
            return coll;
    }


    /**
     * Uses a StringTokenizer to parse a zip code using "-" as a delimeter.
     * Returns the first token of the zip.
     * @param searchZip  String
     * @return String
     */
    private String parseZip(String searchZip)
    {
        if(searchZip != null && searchZip.trim().length()>5)
        {
            return searchZip.substring(0, 5);
        }
        else
        {
            return searchZip;
        }
    }



}
