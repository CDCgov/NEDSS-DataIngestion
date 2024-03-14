package gov.cdc.dataprocessing.service.implementation.core;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.repository.nbs.msgoute.NbsInterfaceRepository;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.auth.ISessionProfileService;
import gov.cdc.dataprocessing.service.interfaces.core.*;
import gov.cdc.dataprocessing.service.interfaces.matching.IObservationMatchingService;
import gov.cdc.dataprocessing.service.model.PersonAggContainer;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.ManagerUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static gov.cdc.dataprocessing.constant.ManagerEvent.EVENT_ELR;
@Service
@Slf4j
public class ManagerService implements IManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    private final IObservationService observationService;
    private final IPatientService patientService;
    private final IOrganizationService organizationService;
    private final IProgramAreaJurisdictionService programAreaJurisdictionService;
    private final ILabProcessingService labProcessingService;

    private final IPublicHealthCaseService publicHealthCaseService;
    private final IEdxLogService edxLogService;

    private final IHandleLabService handleLabService;

    private final IDataExtractionService dataExtractionService;

    private final NbsInterfaceRepository nbsInterfaceRepository;

    private final CheckingValueService checkingValueService;

    private final CacheManager cacheManager;

    private final ISessionProfileService sessionProfileService;

    private final IObservationMatchingService observationMatchingService;

    private final ManagerUtil managerUtil;

    @Autowired
    public ManagerService(IObservationService observationService,
                          IPatientService patientService,
                          IOrganizationService organizationService,
                          IProgramAreaJurisdictionService programAreaJurisdictionService,
                          ILabProcessingService labProcessingService,
                          IPublicHealthCaseService publicHealthCaseService,
                          IEdxLogService edxLogService, IHandleLabService handleLabService,
                          IDataExtractionService dataExtractionService,
                          NbsInterfaceRepository nbsInterfaceRepository,
                          CheckingValueService checkingValueService,
                          CacheManager cacheManager,
                          ISessionProfileService sessionProfileService,
                          IObservationMatchingService observationMatchingService,
                          ManagerUtil managerUtil) {
        this.observationService = observationService;
        this.patientService = patientService;
        this.organizationService = organizationService;
        this.programAreaJurisdictionService = programAreaJurisdictionService;
        this.labProcessingService = labProcessingService;
        this.publicHealthCaseService = publicHealthCaseService;
        this.edxLogService = edxLogService;
        this.handleLabService = handleLabService;
        this.dataExtractionService = dataExtractionService;
        this.nbsInterfaceRepository = nbsInterfaceRepository;
        this.checkingValueService = checkingValueService;
        this.cacheManager = cacheManager;
        this.sessionProfileService = sessionProfileService;
        this.observationMatchingService = observationMatchingService;
        this.managerUtil = managerUtil;
    }

    @Transactional
    public Object processDistribution(String eventType, String data) throws DataProcessingConsumerException {
        //TODO: determine which flow the data will be going through
        Object result = new Object();
        AuthUser profile = sessionProfileService.getSessionProfile("data-processing");
        if (profile != null) {
            AuthUtil.setGlobalAuthUser(profile);
            switch (eventType) {
                case EVENT_ELR:
                    result = processingELR(data);
                    break;
                default:
                    break;
            }
            AuthUtil.setGlobalAuthUser(null);
            return result;
        } else {
            throw new DataProcessingConsumerException("Invalid User");
        }

    }

    public Object processingHealthCase(String data) throws DataProcessingConsumerException {
        //TODO logic to for Health Case Flow
        Object result = new Object();
        try {
            //TODO: Public Health Case
            var healthCase = publicHealthCaseService.processingPublicHealthCase();

            //TODO: Auto Investigation
            var autoInvestigation = publicHealthCaseService.processingAutoInvestigation();
            return result;
        } catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage(), result);
        }
    }

    public Object processingHandleLab(String data)  throws DataProcessingConsumerException {
        //TODO logic to for Health Case Flow
        Object result = new Object();
        try {
            //TODO: Handling Lab Code
            boolean isLabReviewed = true;
            boolean isActExist = true;
            if (isLabReviewed) {
                result = handleLabService.processingReviewedLab();
            }
            else {
                if (isActExist) {
                    result = handleLabService.processingNonReviewLabWithAct();
                }
                else {
                    result = handleLabService.processingNonReviewLabWithoutAct();
                }
            }

            //TODO: Auto Investigation
            return result;
        } catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage(), result);
        }
    }

    public void processingEdxLog(String data) throws EdxLogException {
        edxLogService.processingLog();
    }

    private Object processingELR(String data) throws DataProcessingConsumerException {
        //TODO logic to execute data here
        NbsInterfaceModel nbsInterfaceModel = null;
        Object result = new Object();
        try {

            Gson gson = new Gson();



            EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
            edxLabInformationDto.setStatus(NbsInterfaceStatus.Success);
            edxLabInformationDto.setUserName(AuthUtil.authUser.getUserId());

            //TODO: uncomment when deploy
            nbsInterfaceModel = gson.fromJson(data, NbsInterfaceModel.class);


            //TODO: uncomment when debug
//            nbsInterfaceModel = nbsInterfaceRepository.findById(Integer.valueOf(data)).get();

            edxLabInformationDto.setNbsInterfaceUid(nbsInterfaceModel.getNbsInterfaceUid());


            checkingValueService.getAOELOINCCodes();
            checkingValueService.getRaceCodes();


            var cache = cacheManager.getCache("srte");
            if (cache != null) {
                Cache.ValueWrapper valueWrapper;
                valueWrapper = cache.get("loincCodes");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.loincCodesMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("raceCodes");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.raceCodesMap = (TreeMap<String, String>) cachedObject;
                    }
                }
            }
            //TODO: Parsing Data to Object
            LabResultProxyContainer parsedData = dataExtractionService.parsingDataToObject(nbsInterfaceModel, edxLabInformationDto);

            edxLabInformationDto.setLabResultProxyContainer(parsedData);

            if(nbsInterfaceModel.getObservationUid() !=null && nbsInterfaceModel.getObservationUid()>0) {
                edxLabInformationDto.setRootObserbationUid(nbsInterfaceModel.getObservationUid());
            }
            Long aPersonUid = null;

            //Checking for matching observation
            ObservationDT observationDT = null;
            observationDT = observationService.checkingMatchingObservation(edxLabInformationDto);

            if(observationDT!=null){
                LabResultProxyContainer matchedlabResultProxyVO = observationService.getObservationToLabResultContainer(observationDT.getObservationUid());
                observationMatchingService.processMatchedProxyVO(parsedData, matchedlabResultProxyVO, edxLabInformationDto );

                //TODO: CHECK THIS OUT
                aPersonUid = patientService.getMatchedPersonUID(matchedlabResultProxyVO);
                patientService.updatePersonELRUpdate(parsedData, matchedlabResultProxyVO);

                edxLabInformationDto.setRootObserbationUid(observationDT.getObservationUid());
                if(observationDT.getProgAreaCd()!=null && observationDT.getJurisdictionCd()!=null)
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


            PersonAggContainer personAggContainer = managerUtil.patientAggregation(parsedData, edxLabInformationDto);

            OrganizationVO orderingFacilityVO = organizationService.processingOrganization(parsedData);



            //TODO: VERIFY THIS BLOCK
            // Hit when Obs is matched
            if(edxLabInformationDto.isLabIsUpdateDRRQ() || edxLabInformationDto.isLabIsUpdateDRSA())
            {
                managerUtil.setPersonUIDOnUpdate(aPersonUid, parsedData);
            }
            edxLabInformationDto.setLabResultProxyContainer(parsedData);

            String nbsOperation = edxLabInformationDto.isLabIsCreate() ? "ADD" : "EDIT";

            ObservationVO orderTest = managerUtil.getObservationWithOrderDomainCode(parsedData);

            String programAreaCd = orderTest.getTheObservationDT().getProgAreaCd();
            String jurisdictionCd = orderTest.getTheObservationDT().getJurisdictionCd();

            //TODO: PROGRAM AREA
            var programArea = programAreaJurisdictionService.processingProgramArea();
            var jurisdiction = programAreaJurisdictionService.processingJurisdiction();

            //TODO: EVALUATE LAB PROCESSING
            observationDT = observationService.sendLabResultToProxy(parsedData);

            if(edxLabInformationDto.isLabIsCreate()){
                edxLabInformationDto.setLabIsCreateSuccess(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_2);
            }

            edxLabInformationDto.setLocalId(observationDT.getLocalId());
            edxLabInformationDto.getEdxActivityLogDT().setBusinessObjLocalId(observationDT.getLocalId());
            edxLabInformationDto.setRootObserbationUid(observationDT.getObservationUid());

            //TODO: CACHING
            // edxLabInformationDto.setProgramAreaName(CachedDropDowns.getProgAreadDesc(observationDT.getProgAreaCd()));
            // String jurisdictionName = CachedDropDowns.getJurisdictionDesc(observationDT.getJurisdictionCd());
            // edxLabInformationDto.setJurisdictionName(jurisdictionName);

            if(edxLabInformationDto.isLabIsCreateSuccess()&&(edxLabInformationDto.getProgramAreaName()==null
                    || edxLabInformationDto.getJurisdictionName()==null))
            {
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_1);
            }



            //TODO: Producing msg for Next Step
            // kafkaManagerProducer.sendData(healthCaseTopic, data);



            //TODO: Uncomment this after debugging
            // NOTE: Test updating NBS_Interface

            nbsInterfaceModel.setObservationUid(observationDT.getObservationUid().intValue());
            nbsInterfaceModel.setRecordStatusCd("COMPLETED_V2");
            nbsInterfaceRepository.save(nbsInterfaceModel);
            System.out.println("DONE");
            return result;
        } catch (Exception e) {
            if (nbsInterfaceModel != null) {
                //TODO: Uncomment this after debuggging
                nbsInterfaceModel.setRecordStatusCd("FAILED_V2");
                nbsInterfaceRepository.save(nbsInterfaceModel);
                System.out.println("ERROR");
            }

            throw new DataProcessingConsumerException(e.getMessage(), result);

        }
    }





}
