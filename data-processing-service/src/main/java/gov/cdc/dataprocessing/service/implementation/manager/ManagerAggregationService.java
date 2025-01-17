package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerAggregationService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationService;
import gov.cdc.dataprocessing.service.interfaces.person.IPersonService;
import gov.cdc.dataprocessing.service.interfaces.role.IRoleService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.service.model.person.PersonAggContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
public class ManagerAggregationService implements IManagerAggregationService {
    private final IOrganizationService organizationService;
    private final IPersonService patientService;
    private final IUidService uidService;
    private final IObservationService observationService;
    private final IObservationMatchingService observationMatchingService;
    private final IProgramAreaService programAreaService;
    private final IJurisdictionService jurisdictionService;
    private final IRoleService roleService;
    private static final String THREAD_EXCEPTION_MSG = "Thread was interrupted";
    private static final Logger logger = LoggerFactory.getLogger(ManagerAggregationService.class);

    public ManagerAggregationService(IOrganizationService organizationService,
                                     IPersonService patientService,
                                     IUidService uidService,
                                     IObservationService observationService,
                                     IObservationMatchingService observationMatchingService,
                                     IProgramAreaService programAreaService,
                                     IJurisdictionService jurisdictionService,
                                     IRoleService roleService) {
        this.organizationService = organizationService;
        this.patientService = patientService;
        this.uidService = uidService;
        this.observationService = observationService;
        this.observationMatchingService = observationMatchingService;
        this.programAreaService = programAreaService;
        this.jurisdictionService = jurisdictionService;
        this.roleService = roleService;
    }


    public EdxLabInformationDto processingObservationMatching(EdxLabInformationDto edxLabInformationDto,
                                                       LabResultProxyContainer labResultProxyContainer,
                                                       Long aPersonUid) throws DataProcessingException {
        ObservationDto observationDto = observationMatchingService.checkingMatchingObservation(edxLabInformationDto);
        if(observationDto !=null){
            logger.info("OBSERVE: Matched OBS Found");
            LabResultProxyContainer matchedlabResultProxyVO = observationService.getObservationToLabResultContainer(observationDto.getObservationUid());
            observationMatchingService.processMatchedProxyVO(labResultProxyContainer, matchedlabResultProxyVO, edxLabInformationDto );

            patientService.getMatchedPersonUID(matchedlabResultProxyVO);
            patientService.updatePersonELRUpdate(labResultProxyContainer, matchedlabResultProxyVO);

            edxLabInformationDto.setRootObserbationUid(observationDto.getObservationUid());
            if(observationDto.getProgAreaCd()!=null && observationDto.getJurisdictionCd()!=null)
            {
                edxLabInformationDto.setLabIsUpdateDRRQ(true);
            }
            else
            {
                edxLabInformationDto.setLabIsUpdateDRSA(true);
            }
            edxLabInformationDto.setPatientMatch(true);
        }
        else {
            edxLabInformationDto.setLabIsCreate(true);
        }

        return edxLabInformationDto;
    }

    public void serviceAggregation(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto) throws
            DataProcessingException, DataProcessingConsumerException {
        PersonAggContainer personAggContainer;
        OrganizationContainer organizationContainer;
        Collection<ObservationContainer> observationContainerCollection = labResult.getTheObservationContainerCollection();
        Collection<PersonContainer> personContainerCollection = labResult.getThePersonContainerCollection();
        observationAggregation(labResult, edxLabInformationDto, observationContainerCollection);
        personAggContainer =  patientAggregation(labResult, edxLabInformationDto, personContainerCollection);
        organizationContainer= organizationService.processingOrganization(labResult);
        roleAggregation(labResult);
        progAndJurisdictionAggregation(labResult, edxLabInformationDto, personAggContainer, organizationContainer);
    }

    public void serviceAggregationAsync(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto) throws
            DataProcessingException, DataProcessingConsumerException {
        PersonAggContainer personAggContainer;
        OrganizationContainer organizationContainer;
        Collection<ObservationContainer> observationContainerCollection = labResult.getTheObservationContainerCollection();
        Collection<PersonContainer> personContainerCollection = labResult.getThePersonContainerCollection();

        observationAggregation(labResult, edxLabInformationDto, observationContainerCollection);
        personAggContainer = patientAggregation(labResult, edxLabInformationDto, personContainerCollection);

        Map<Long, Long> patientCount = personContainerCollection.stream()
                .collect(Collectors.groupingBy(
                        pc -> pc.getThePersonDto().getPersonUid(),
                        Collectors.counting()
                ));
        long repetitiveCount = patientCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .count();
        if (repetitiveCount > 0) {
            logger.info("CRITICAL, multiple repetitive uid found. {}", repetitiveCount);
        }

        organizationContainer = organizationService.processingOrganization(labResult);

        roleAggregation(labResult);
        progAndJurisdictionAggregationAsync(labResult, edxLabInformationDto, personAggContainer, organizationContainer);
    }

    protected void progAndJurisdictionAggregation(LabResultProxyContainer labResult,
                                                                          EdxLabInformationDto edxLabInformationDto,
                                                                          PersonAggContainer personAggContainer,
                                                                          OrganizationContainer organizationContainer) {
            // Pulling Jurisdiction and Program from OBS
            ObservationContainer observationRequest = null;
            Collection<ObservationContainer> observationResults = new ArrayList<>();
            for (ObservationContainer obsVO : labResult.getTheObservationContainerCollection()) {
                String obsDomainCdSt1 = obsVO.getTheObservationDto().getObsDomainCdSt1();

                // Observation hit this is originated from Observation Result
                if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_RESULT_CD)) {
                    observationResults.add(obsVO);
                }

                // Observation hit is originated from Observation Request (ROOT)
                else if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)) {
                    observationRequest = obsVO;
                }
            }

            if (observationRequest != null && observationRequest.getTheObservationDto().getProgAreaCd() == null) {
                try {
                    programAreaService.getProgramArea(observationResults, observationRequest, edxLabInformationDto.getSendingFacilityClia());
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            if (observationRequest != null && observationRequest.getTheObservationDto().getJurisdictionCd() == null) {
                try {
                    jurisdictionService.assignJurisdiction(personAggContainer.getPersonContainer(), personAggContainer.getProviderContainer(),
                            organizationContainer, observationRequest);
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

    }


    @SuppressWarnings("java:S3776")
    protected void progAndJurisdictionAggregationAsync(LabResultProxyContainer labResult,
                                                                        EdxLabInformationDto edxLabInformationDto,
                                                                        PersonAggContainer personAggContainer,
                                                                        OrganizationContainer organizationContainer) {
        ObservationContainer observationRequest = null;
        Collection<ObservationContainer> observationResults = new ArrayList<>();
        for (ObservationContainer obsVO : labResult.getTheObservationContainerCollection()) {
            String obsDomainCdSt1 = obsVO.getTheObservationDto().getObsDomainCdSt1();

            // Observation hit this is originated from Observation Result
            if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_RESULT_CD)) {
                observationResults.add(obsVO);
            }

            // Observation hit is originated from Observation Request (ROOT)
            else if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)) {
                observationRequest = obsVO;
            }
        }

        if (observationRequest != null && observationRequest.getTheObservationDto().getProgAreaCd() == null) {
            try {
                programAreaService.getProgramArea(observationResults, observationRequest, edxLabInformationDto.getSendingFacilityClia());
            } catch (DataProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        if (observationRequest != null && observationRequest.getTheObservationDto().getJurisdictionCd() == null) {
            try {
                jurisdictionService.assignJurisdiction(personAggContainer.getPersonContainer(), personAggContainer.getProviderContainer(),
                        organizationContainer, observationRequest);
            } catch (DataProcessingException e) {
                throw new RuntimeException(e);
            }
        }

//        return CompletableFuture.runAsync(() -> {
//            // Pulling Jurisdiction and Program from OBS
//            ObservationContainer observationRequest = null;
//            Collection<ObservationContainer> observationResults = new ArrayList<>();
//            for (ObservationContainer obsVO : labResult.getTheObservationContainerCollection()) {
//                String obsDomainCdSt1 = obsVO.getTheObservationDto().getObsDomainCdSt1();
//
//                // Observation hit this is originated from Observation Result
//                if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_RESULT_CD)) {
//                    observationResults.add(obsVO);
//                }
//
//                // Observation hit is originated from Observation Request (ROOT)
//                else if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)) {
//                    observationRequest = obsVO;
//                }
//            }
//
//            if (observationRequest != null && observationRequest.getTheObservationDto().getProgAreaCd() == null) {
//                try {
//                    programAreaService.getProgramArea(observationResults, observationRequest, edxLabInformationDto.getSendingFacilityClia());
//                } catch (DataProcessingException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            if (observationRequest != null && observationRequest.getTheObservationDto().getJurisdictionCd() == null) {
//                try {
//                    jurisdictionService.assignJurisdiction(personAggContainer.getPersonContainer(), personAggContainer.getProviderContainer(),
//                            organizationContainer, observationRequest);
//                } catch (DataProcessingException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
    }

    @SuppressWarnings("java:S3776")
    protected void roleAggregation(LabResultProxyContainer labResult) {
        Map<Object, RoleDto> mappedExistingRoleCollection  = new HashMap<>();
        Map<Object, RoleDto> mappedNewRoleCollection  = new HashMap<>();
        if(labResult.getTheRoleDtoCollection()!=null){

            Collection<RoleDto> coll=labResult.getTheRoleDtoCollection();
            if(!coll.isEmpty())
            {
                for (RoleDto roleDT : coll) {
                    if (roleDT.isItDelete()) {
                        mappedExistingRoleCollection.put(roleDT.getSubjectEntityUid() + roleDT.getCd() + roleDT.getScopingEntityUid(), roleDT);
                    } else {
                        mappedNewRoleCollection.put(roleDT.getSubjectEntityUid() + roleDT.getCd() + roleDT.getScopingEntityUid(), roleDT);
                    }

                }
            }
        }
        ArrayList<Object> list = new ArrayList<>();

        //update scenario
        if(!mappedNewRoleCollection.isEmpty()){
            Set<Object> set = mappedNewRoleCollection.keySet();
            for (Object o : set) {
                String key = (String) o;
                if (mappedExistingRoleCollection.containsKey(key)) {
                    //Do not delete/modify the role as it exists in both updated and old ELR
                    // UPDATE Role bucket
                    mappedExistingRoleCollection.remove(key);


                } else {
                    //insert Role as it is new in new/updated ELR
                    //NEW role Bucket
                    RoleDto roleDT = mappedNewRoleCollection.get(key);
                    list.add(roleDT);
                }

            }

            //will add all roles that are part of the old collection but are not contained in the new collection
            // MARK FOR DELETE Role bucket
            list.addAll(mappedExistingRoleCollection.values());
        }


        Map<Object, RoleDto> modifiedRoleMap = new HashMap<>();
        ArrayList<RoleDto> listFinal = new ArrayList<>();
        if(!list.isEmpty()){
            for (Object o : list) {
                RoleDto roleDT = (RoleDto) o;
                if (roleDT.isItDelete()) {
                    listFinal.add(roleDT);
                    //We have already taken care of the deduplication of role in the above code
                    continue;
                }
                //We will write the role if there are no existing role relationships.
                if (roleDT.getScopingEntityUid() == null)
                {
                    long count;
                    count = roleService.loadCountBySubjectCdComb(roleDT).longValue();
                    if (count == 0) {
                        roleDT.setRoleSeq(count + 1);
                        modifiedRoleMap.put(roleDT.getSubjectEntityUid() + roleDT.getRoleSeq() + roleDT.getCd(), roleDT);
                    }

                }
                else
                {
                    int checkIfExisits;
                    checkIfExisits = roleService.loadCountBySubjectScpingCdComb(roleDT);

                    if (checkIfExisits == 0) {
                        long countForPKValues;
                        countForPKValues = roleService.loadCountBySubjectCdComb(roleDT);
                        //We will write the role relationship for follwoing provider in scope of ELR patient
                        if (countForPKValues == 0
                                || (
                                roleDT.getCd() != null
                                        && (
                                        roleDT.getCd().equals(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD)
                                                || roleDT.getCd().equals(EdxELRConstant.ELR_COPY_TO_CD)
                                                || roleDT.getSubjectClassCd().equals(EdxELRConstant.ELR_CON)
                                )
                        )
                        ) {

                            if (roleDT.getRoleSeq() != null && roleDT.getRoleSeq().intValue() == 2
                                    && roleDT.getSubjectClassCd().equals(EdxELRConstant.ELR_MAT_CD)
                                    && roleDT.getScopingRoleCd().equals(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD)
                            ) {
                                //Material is a special as provider to material is created with role sequence 2
                            } else {
                                roleDT.setRoleSeq(countForPKValues + 1);
                            }
                            modifiedRoleMap.put(roleDT.getSubjectEntityUid() + roleDT.getRoleSeq() + roleDT.getCd() + roleDT.getScopingEntityUid(), roleDT);

                        }
                    }

                }
            }

            if(!modifiedRoleMap.isEmpty()){
                Collection<RoleDto> roleCollection = modifiedRoleMap.values();
                listFinal.addAll(roleCollection);
            }

            labResult.setTheRoleDtoCollection(listFinal);

        }


    }

    @SuppressWarnings("java:S3776")
    protected void observationAggregation(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto, Collection<ObservationContainer> observationContainerCollection) {
        if (observationContainerCollection != null && !observationContainerCollection.isEmpty()) {
            for (ObservationContainer obsVO : observationContainerCollection) {
                if (obsVO.getTheObservationDto().getObservationUid() == edxLabInformationDto.getRootObserbationUid()
                        && edxLabInformationDto.getRootObserbationUid() > 0
                ) {
                    long falseUid = -1;
                    uidService.setFalseToNewForObservation(labResult, falseUid, obsVO.getTheObservationDto().getObservationUid());
                    if (obsVO.getTheActIdDtoCollection() != null) {
                        for (ActIdDto actIdDto : obsVO.getTheActIdDtoCollection()) {
                            actIdDto.setItNew(false);
                            actIdDto.setItDirty(true);
                            actIdDto.setActUid(obsVO.getTheObservationDto().getObservationUid());
                        }
                    }
                    break;
                }
            }
        }


    }

    protected PersonAggContainer patientAggregation(LabResultProxyContainer labResultProxyContainer,
                                                    EdxLabInformationDto edxLabInformationDto,
                                                  Collection<PersonContainer>  personContainerCollection) throws DataProcessingConsumerException, DataProcessingException {

        PersonAggContainer container = new PersonAggContainer();
        PersonContainer personContainerObj = null;
        PersonContainer providerVOObj = null;
        if (personContainerCollection != null && !personContainerCollection.isEmpty() ) {
            Iterator<PersonContainer> it = personContainerCollection.iterator();
            boolean orderingProviderIndicator = false;

            while (it.hasNext()) {
                PersonContainer personContainer = it.next();
                if (personContainer.getRole() != null && personContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)) {
                    patientService.processingNextOfKin(labResultProxyContainer, personContainer);
                    edxLabInformationDto.setNextOfKin(true);
                }
                else {
                    if (personContainer.thePersonDto.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_CD)) {
                        personContainerObj =  patientService.processingPatient(labResultProxyContainer, edxLabInformationDto, personContainer);
                    }
                    else if (personContainer.thePersonDto.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PROVIDER_CD)) {
                        providerVOObj = patientService.processingProvider(labResultProxyContainer, edxLabInformationDto, personContainer, orderingProviderIndicator);
                    }
                }
            }
        }

        container.setPersonContainer(personContainerObj);
        container.setProviderContainer(providerVOObj);
        return container;
    }
}
