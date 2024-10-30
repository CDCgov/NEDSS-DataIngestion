package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.RenderConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.lookup.PrePopMappingDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueDateDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueNumericDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueTxtDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCodeWithPA;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.ConditionCodeRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IAutoInvestigationService;
import gov.cdc.dataprocessing.utilities.DynamicBeanBinding;
import gov.cdc.dataprocessing.utilities.RulesEngineUtil;
import gov.cdc.dataprocessing.utilities.StringUtils;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.public_health_case.CdaPhcProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.PHC_PHYSICIAN;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class AutoInvestigationService implements IAutoInvestigationService {
    private static final Logger logger = LoggerFactory.getLogger(AutoInvestigationService.class);
    private final ConditionCodeRepository conditionCodeRepository;
    private final ICatchingValueService catchingValueService;
    private final ILookupService lookupService;
    public AutoInvestigationService(ConditionCodeRepository conditionCodeRepository,
                                    ICatchingValueService catchingValueService,
                                    ILookupService lookupService) {
        this.conditionCodeRepository = conditionCodeRepository;
        this.catchingValueService = catchingValueService;
        this.lookupService = lookupService;
    }

    public Object autoCreateInvestigation(ObservationContainer observationVO,
                                          EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        PageActProxyContainer pageActProxyContainer = null;
        PamProxyContainer pamProxyVO = null;
        PublicHealthCaseContainer phcVO= createPublicHealthCaseVO(observationVO, edxLabInformationDT);

        Collection<ActIdDto> theActIdDTCollection = new ArrayList<>();
        ActIdDto actIDDT = new ActIdDto();
        actIDDT.setItNew(true);
        actIDDT.setActIdSeq(1);
        actIDDT.setTypeCd(NEDSSConstant.ACT_ID_STATE_TYPE_CD);
        theActIdDTCollection.add(actIDDT);
        phcVO.setTheActIdDTCollection(theActIdDTCollection);
        if(edxLabInformationDT.getInvestigationType()!=null && edxLabInformationDT.getInvestigationType().equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)){
            ActIdDto actID1DT = new ActIdDto();
            actID1DT.setItNew(true);
            actID1DT.setActIdSeq(2);
            actID1DT.setTypeCd("CITY");
            theActIdDTCollection.add(actID1DT);
            phcVO.setTheActIdDTCollection(theActIdDTCollection);
        }
        if (edxLabInformationDT.getInvestigationType()!=null
                && (edxLabInformationDT.getInvestigationType().equalsIgnoreCase(NEDSSConstant.INV_FORM_VAR)
                || edxLabInformationDT.getInvestigationType().equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)))
        {
            pamProxyVO = new PamProxyContainer();
            pamProxyVO.setItNew(true);
            pamProxyVO.setItDirty(false);
            pamProxyVO.setPublicHealthCaseContainer(phcVO);
        }
        else
        {
            pageActProxyContainer = new PageActProxyContainer();
            pageActProxyContainer.setItNew(true);
            pageActProxyContainer.setItDirty(false);
            pageActProxyContainer.setPublicHealthCaseContainer(phcVO);
            pageActProxyContainer.setPageVO(new BasePamContainer());
            populateProxyFromPrePopMapping(pageActProxyContainer, edxLabInformationDT);
        }
        try {
            Object obj;

            if(pageActProxyContainer !=null)
            {
                obj= pageActProxyContainer;
            }
            else
            {
                obj=pamProxyVO;
            }
            return obj;
        } catch (Exception e) {
            throw new DataProcessingException("AutoInvestigationHandler-autoCreateInvestigation NEDSSSystemException raised"+e);
        }

    }

    @SuppressWarnings("java:S3776")
    public Object transferValuesTOActProxyVO(PageActProxyContainer pageActProxyContainer, PamProxyContainer pamActProxyVO,
                                             Collection<PersonContainer> personVOCollection,
                                             ObservationContainer rootObservationVO,
                                             Collection<Object> entities,
                                             Map<Object, Object> questionIdentifierMap) throws DataProcessingException{
        try {
            PersonContainer patientVO;
            boolean isOrgAsReporterOfPHCPartDT=false;
            boolean isPhysicianOfPHCDT=false;
            Collection<ParticipationDto> coll = rootObservationVO.getTheParticipationDtoCollection();
            Collection<ParticipationDto> partColl = new ArrayList<>();
            Collection<NbsActEntityDto> nbsActEntityDTColl = new ArrayList<>();
            long personUid;
            if(pageActProxyContainer !=null)
                personUid= pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid()-1;
            else{
                personUid=pamActProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid()-1;
            }
            if(personVOCollection!=null){
                for (PersonContainer personVO : personVOCollection) {
                    if (personVO.getThePersonDto().getCd().equals("PAT")) {
                        patientVO = personVO;
                        patientVO.getThePersonDto().setPersonUid(personUid);
                        Collection<PersonContainer> thePersonVOCollection = new ArrayList<>();
                        thePersonVOCollection.add(patientVO);
                        patientVO.setItNew(true);
                        patientVO.setItDirty(false);
                        patientVO.getThePersonDto().setItDirty(false);
                        patientVO.getThePersonDto().setItNew(true);
                        personVO.getThePersonDto().setElectronicInd(NEDSSConstant.ELECTRONIC_IND);
                        personVO.getThePersonDto().setStatusTime(new Timestamp(new Date().getTime()));
                        if (pageActProxyContainer != null)
                            pageActProxyContainer.setThePersonContainerCollection(thePersonVOCollection);
                        else {
                            pamActProxyVO.setThePersonVOCollection(thePersonVOCollection);
                        }
                        break;
                    }
                }
            }
            if(entities!=null && entities.size()>0){
                for (Object entity : entities) {
                    EdxRuleManageDto edxRuleManageDT = (EdxRuleManageDto) entity;
                    ParticipationDto participationDT = new ParticipationDto();
                    participationDT.setTypeCd(edxRuleManageDT.getParticipationTypeCode());
                    participationDT.setSubjectEntityUid(edxRuleManageDT.getParticipationUid());
                    participationDT.setSubjectClassCd(edxRuleManageDT.getParticipationClassCode());
                    if (participationDT.getTypeCd().equals("OrgAsReporterOfPHC")) {
                        isOrgAsReporterOfPHCPartDT = true;
                    } else if (participationDT.getTypeCd().equals(PHC_PHYSICIAN)) {
                        isPhysicianOfPHCDT = true;
                    }

                    createActEntityObject(participationDT, pageActProxyContainer, pamActProxyVO, nbsActEntityDTColl, partColl);
                }
            }
            if(coll!=null){

                for (ParticipationDto partDT : coll) {
                    boolean createActEntity = false;
                    String typeCd = partDT.getTypeCd();
                    if (typeCd.equalsIgnoreCase(EdxELRConstant.ELR_AUTHOR_CD) && partDT.getSubjectClassCd().equals(EdxELRConstant.ELR_ORG) && !isOrgAsReporterOfPHCPartDT) {
                        createActEntity = true;
                        partDT.setTypeCd("OrgAsReporterOfPHC");
                    }
                    if (typeCd.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD) && partDT.getSubjectClassCd().equals(EdxELRConstant.ELR_PERSON_CD) && !isPhysicianOfPHCDT) {
                        createActEntity = true;
                        partDT.setTypeCd(PHC_PHYSICIAN);
                    }
                    //gst- ND-4326 Physician not getting populated..
                    if (typeCd.equalsIgnoreCase(EdxELRConstant.ELR_ORDERER_CD) && partDT.getSubjectClassCd().equals(EdxELRConstant.ELR_PERSON_CD) && !isPhysicianOfPHCDT) {
                        createActEntity = true;
                        partDT.setTypeCd(PHC_PHYSICIAN);
                    }
                    //Transfer the ordering facility over if it is on the PageBuilder page
                    if (typeCd.equalsIgnoreCase(EdxELRConstant.ELR_ORDERER_CD) &&
                            partDT.getSubjectClassCd().equals(EdxELRConstant.ELR_ORG) &&
                            partDT.getCd().equals(EdxELRConstant.ELR_OP_CD) &&
                            questionIdentifierMap != null &&
                            questionIdentifierMap.containsKey("NBS291")) {
                        createActEntity = true;
                        partDT.setTypeCd("OrgAsClinicOfPHC");
                    }
                    if (typeCd.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_SUBJECT_CD)) {
                        createActEntity = true;
                        partDT.setTypeCd("SubjOfPHC");
                        partDT.setSubjectEntityUid(personUid);
                    }
                    if (createActEntity) {
                        createActEntityObject(partDT, pageActProxyContainer, pamActProxyVO, nbsActEntityDTColl, partColl);
                    }

                }

            }

            if(pageActProxyContainer !=null){
                pageActProxyContainer.setTheParticipationDtoCollection(partColl);
                BasePamContainer pamVO ;
                if(pageActProxyContainer.getPageVO()!=null)
                {
                    pamVO= pageActProxyContainer.getPageVO();
                }
                else
                {
                    pamVO = new BasePamContainer();
                }
                pamVO.setActEntityDTCollection(nbsActEntityDTColl);
                pageActProxyContainer.setPageVO(pamVO);
                return pageActProxyContainer;
            }
            else{
                pamActProxyVO.setTheParticipationDTCollection(partColl);
                BasePamContainer pamVO;
                if(pamActProxyVO.getPamVO()!=null)
                {
                    pamVO=pamActProxyVO.getPamVO();
                }
                else
                {
                    pamVO = new BasePamContainer();
                }
                pamVO.setActEntityDTCollection(nbsActEntityDTColl);
                pamActProxyVO.setPamVO(pamVO);
                return pamActProxyVO;
            }

        } catch (Exception e) {
            throw new DataProcessingException("AutoInvestigationHandler-transferValuesTOActProxyVO Exception raised"+e);
        }
    }

    private PublicHealthCaseContainer createPublicHealthCaseVO(ObservationContainer observationVO, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        PublicHealthCaseContainer phcVO = new PublicHealthCaseContainer();

        phcVO.getThePublicHealthCaseDto().setLastChgTime(new java.sql.Timestamp(new Date().getTime()));

        phcVO.getThePublicHealthCaseDto().setPublicHealthCaseUid((long) (edxLabInformationDT.getNextUid() - 1));
        //edxLabInformationDT.setNextUid(edxLabInformationDT.getNextUid());
        phcVO.getThePublicHealthCaseDto().setJurisdictionCd((observationVO.getTheObservationDto().getJurisdictionCd()));
        phcVO.getThePublicHealthCaseDto().setRptFormCmpltTime(observationVO.getTheObservationDto().getRptToStateTime());
        //phcVO.getThePublicHealthCaseDto().setCaseClassCd(EdxELRConstant.ELR_CONFIRMED_CD);

        phcVO.getThePublicHealthCaseDto().setAddTime(new Timestamp(new Date().getTime()));
        phcVO.getThePublicHealthCaseDto().setAddUserId(AuthUtil.authUser.getNedssEntryId());
        phcVO.getThePublicHealthCaseDto().setCaseTypeCd(EdxELRConstant.ELR_INDIVIDUAL);

        var res = conditionCodeRepository.findProgramAreaConditionCodeByConditionCode(edxLabInformationDT.getConditionCode());
        ConditionCodeWithPA programAreaVO = new ConditionCodeWithPA();
        if (res.isPresent()) {
            programAreaVO = res.get().get(0);
        }
        phcVO.getThePublicHealthCaseDto().setCd(programAreaVO.getConditionCd());
        phcVO.getThePublicHealthCaseDto().setProgAreaCd(programAreaVO.getStateProgAreaCode());

        if(SrteCache.checkWhetherPAIsStdOrHiv(programAreaVO.getStateProgAreaCode()))
            phcVO.getThePublicHealthCaseDto().setReferralBasisCd(NEDSSConstant.REFERRAL_BASIS_LAB);

        phcVO.getThePublicHealthCaseDto().setSharedInd(NEDSSConstant.TRUE);
        phcVO.getThePublicHealthCaseDto().setCdDescTxt(programAreaVO.getConditionShortNm());
        phcVO.getThePublicHealthCaseDto().setGroupCaseCnt(1);
        phcVO.getThePublicHealthCaseDto().setInvestigationStatusCd(EdxELRConstant.ELR_OPEN_CD);
        phcVO.getThePublicHealthCaseDto().setActivityFromTime(
                StringUtils.stringToStrutsTimestamp(StringUtils
                        .formatDate(new Timestamp((new Date()).getTime()))));
        Calendar now = Calendar.getInstance();
        String dateValue = (now.get(Calendar.MONTH)+1) +"/" + now.get(Calendar.DATE) +"/" + now.get(Calendar.YEAR);
        int[] weekAndYear = RulesEngineUtil.CalcMMWR(dateValue);
        phcVO.getThePublicHealthCaseDto().setMmwrWeek(String.valueOf(weekAndYear[0]));
        phcVO.getThePublicHealthCaseDto().setMmwrYear(String.valueOf(weekAndYear[1]));
        phcVO.getThePublicHealthCaseDto().setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        if (edxLabInformationDT.getConditionCode() != null) {
            phcVO.setCoinfectionCondition(SrteCache.coInfectionConditionCode.containsKey(edxLabInformationDT.getConditionCode()));
            if (phcVO.isCoinfectionCondition()) {
                phcVO.getThePublicHealthCaseDto().setCoinfectionId(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE);
            }
        }
        phcVO.getThePublicHealthCaseDto().setItNew(true);
        phcVO.getThePublicHealthCaseDto().setItDirty(false);
        phcVO.setItNew(true);
        phcVO.setItDirty(false);

        try{
            boolean isSTDProgramArea = SrteCache.checkWhetherPAIsStdOrHiv(phcVO.getThePublicHealthCaseDto().getProgAreaCd());
            if (isSTDProgramArea) {
                CaseManagementDto caseMgtDT = new CaseManagementDto();
                caseMgtDT.setPublicHealthCaseUid(phcVO.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                caseMgtDT.setCaseManagementDTPopulated(true);
                phcVO.setTheCaseManagementDto(caseMgtDT);
            }
        } catch(Exception ex){
            throw new DataProcessingException("Unexpected exception setting CaseManagementDto to PHC -->" +ex);
        }

        return phcVO;
    }

    @SuppressWarnings("java:S3776")
    protected void populateProxyFromPrePopMapping(PageActProxyContainer pageActProxyContainer,
                                                  EdxLabInformationDto edxLabInformationDT)
            throws DataProcessingException {
        try {
            lookupService.fillPrePopMap();
            TreeMap<Object, Object> fromPrePopMap =
                    (TreeMap<Object, Object>) OdseCache.fromPrePopFormMapping.get(NEDSSConstant.LAB_FORM_CD);
            if (fromPrePopMap == null) {
                fromPrePopMap = new TreeMap<>();
            }
            Collection<ObservationContainer> obsCollection = edxLabInformationDT.getLabResultProxyContainer()
                    .getTheObservationContainerCollection();
            TreeMap<Object, Object> prePopMap = new TreeMap<>();

            // Begin Dynamic Pre-pop mapping

            for (ObservationContainer obs : obsCollection) {
                if (obs.getTheObsValueNumericDtoCollection() != null
                        && obs.getTheObsValueNumericDtoCollection().size() > 0
                        && fromPrePopMap.containsKey(obs.getTheObservationDto().getCd()))
                {

                    List<ObsValueNumericDto> obsValueNumList = new ArrayList<>(obs.getTheObsValueNumericDtoCollection());
                    String value = obsValueNumList.get(0).getNumericUnitCd() == null
                            ? String.valueOf(obsValueNumList.get(0).getNumericValue1())
                            : obsValueNumList.get(0).getNumericValue1() + "^"
                            + obsValueNumList.get(0).getNumericUnitCd();
                    prePopMap.put(obs.getTheObservationDto().getCd(), value);
                }
                else if (obs.getTheObsValueDateDtoCollection() != null
                        && obs.getTheObsValueDateDtoCollection().size() > 0
                        && fromPrePopMap.containsKey(obs.getTheObservationDto().getCd()))
                {
                    List<ObsValueDateDto> obsValueDateList = new ArrayList<>(obs.getTheObsValueDateDtoCollection());

                    String value = StringUtils.formatDate(obsValueDateList.get(0).getFromTime());
                    prePopMap.put(obs.getTheObservationDto().getCd(), value);
                }
                else if (obs.getTheObsValueCodedDtoCollection() != null
                        && obs.getTheObsValueCodedDtoCollection().size() > 0)
                {

                    List<ObsValueCodedDto> obsValueCodeList = new ArrayList<>(obs.getTheObsValueCodedDtoCollection());

                    String key = obs.getTheObservationDto().getCd() + "$" + obsValueCodeList.get(0).getCode();
                    if (fromPrePopMap.containsKey(key)) {
                        prePopMap.put(key, obsValueCodeList.get(0).getCode());
                    } else if (fromPrePopMap.containsKey(obs.getTheObservationDto().getCd())) {
                        prePopMap.put(obs.getTheObservationDto().getCd(), obsValueCodeList.get(0).getCode());
                    }
                }
                else if (obs.getTheObsValueTxtDtoCollection() != null && obs.getTheObsValueTxtDtoCollection().size() > 0
                        && fromPrePopMap.containsKey(obs.getTheObservationDto().getCd()))
                {
                    for (ObsValueTxtDto obsValueTxtDT : obs.getTheObsValueTxtDtoCollection()) {
                        if (obsValueTxtDT.getTxtTypeCd() == null || obsValueTxtDT.getTxtTypeCd().trim().equals("")
                                || obsValueTxtDT.getTxtTypeCd().equalsIgnoreCase("O")) {
                            prePopMap.put(obs.getTheObservationDto().getCd(), obsValueTxtDT.getValueTxt());
                            break;
                        }
                    }
                }
            }
            populateFromPrePopMapping(prePopMap, pageActProxyContainer);

        } catch (Exception e) {
            throw new DataProcessingException("AutoInvestigationHandler-populateProxyFromPrePopMapping Exception raised" + e);
        }
    }

    @SuppressWarnings("java:S3776")
    private void populateFromPrePopMapping(TreeMap<Object, Object> prePopMap, PageActProxyContainer pageActProxyContainer)
            throws DataProcessingException {
        try {
            PublicHealthCaseDto phcDT = pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
            var res = conditionCodeRepository.findProgramAreaConditionCode(
                    1,
                    new ArrayList<>(Collections.singletonList(phcDT.getProgAreaCd()))
            );
            ProgramAreaContainer programAreaVO = null;
            if (res.isPresent()) {
                var items = res.get();

                var itemRes = items.stream()
                        .filter(cc -> Objects.equals(cc.getConditionCd(), phcDT.getCd()))
                        .findFirst();
                if (itemRes.isPresent()) {
                    programAreaVO = new ProgramAreaContainer();
                    programAreaVO.setConditionCd(itemRes.get().getConditionCd());
                    programAreaVO.setConditionShortNm(itemRes.get().getConditionShortNm());
                    programAreaVO.setStateProgAreaCode(itemRes.get().getStateProgAreaCode());
                    programAreaVO.setStateProgAreaCdDesc(itemRes.get().getStateProgAreaCdDesc());
                    programAreaVO.setInvestigationFormCd(itemRes.get().getInvestigationFormCd());
                }

            }
            if (programAreaVO == null) // level 2 condition for Hepatitis Diagnosis
            {
                var res2 = conditionCodeRepository.findProgramAreaConditionCode(
                        2,
                        new ArrayList<>(Collections.singletonList(phcDT.getProgAreaCd())));
                if (res2.isPresent()) {
                    var items = res2.get();

                    var itemRes = items.stream()
                            .filter(cc -> Objects.equals(cc.getConditionCd(), phcDT.getCd()))
                            .findFirst();

                    if (itemRes.isPresent()) {
                        programAreaVO = new ProgramAreaContainer();
                        programAreaVO.setConditionCd(itemRes.get().getConditionCd());
                        programAreaVO.setConditionShortNm(itemRes.get().getConditionShortNm());
                        programAreaVO.setStateProgAreaCode(itemRes.get().getStateProgAreaCode());
                        programAreaVO.setStateProgAreaCdDesc(itemRes.get().getStateProgAreaCdDesc());
                        programAreaVO.setInvestigationFormCd(itemRes.get().getInvestigationFormCd());
                    }
                }
            }
            String investigationFormCd = null;
            if (programAreaVO != null) {
                investigationFormCd = programAreaVO.getInvestigationFormCd();
            }

            Map<Object, Object> questionMap = (Map<Object, Object>) OdseCache.dmbMap.get(investigationFormCd);

            if (prePopMap == null || prePopMap.size() == 0)
                return;
            TreeMap<Object, Object> toPrePopMap = lookupService.getToPrePopFormMapping(investigationFormCd);
            if (toPrePopMap != null && toPrePopMap.size() > 0) {
                Collection<Object> toPrePopColl = toPrePopMap.values();
                Map<Object, Object> answerMap = new HashMap<>();
                for (Object obj : toPrePopColl) {
                    PrePopMappingDto toPrePopMappingDT = (PrePopMappingDto) obj;
                    String mappingKey = toPrePopMappingDT.getFromAnswerCode() == null
                            ? toPrePopMappingDT.getFromQuestionIdentifier()
                            : toPrePopMappingDT.getFromQuestionIdentifier() + "$"
                            + toPrePopMappingDT.getFromAnswerCode();
                    if (prePopMap.containsKey(mappingKey)) {
                        String value = null;
                        String dataLocation = null;
                        NbsQuestionMetadata quesMetadata = (NbsQuestionMetadata) questionMap
                                .get(toPrePopMappingDT.getToQuestionIdentifier());
                        if (quesMetadata != null)
                        {
                            dataLocation = quesMetadata.getDataLocation();
                        }
                        if (toPrePopMappingDT.getToDataType() != null
                                && toPrePopMappingDT.getToDataType().equals(NEDSSConstant.DATE_DATATYPE)) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                                String stringDate = (String) prePopMap.get(mappingKey);
                                if (stringDate != null && stringDate.length() > 8)
                                    stringDate = stringDate.substring(0, 8);
                                Date date = formatter.parse(stringDate);
                                value = sdf.format(date);
                            } catch (Exception ex) {
                                logger.info(ex.getMessage());
                            }
                        }
                        else if (toPrePopMappingDT.getToAnswerCode() != null)
                        {
                            value = toPrePopMappingDT.getToAnswerCode();
                        }
                        else
                        {
                            value = (String) prePopMap.get(mappingKey);
                        }

                        if (value != null && dataLocation != null
                                && dataLocation.startsWith(RenderConstant.PUBLIC_HEALTH_CASE)) {
                            String columnName = dataLocation.substring(dataLocation.indexOf(".") + 1);
                            DynamicBeanBinding.populateBean(phcDT, columnName, value);
                        } else if (value != null && dataLocation != null
                                && dataLocation.endsWith(RenderConstant.ANSWER_TXT)) {
                            NbsCaseAnswerDto caseAnswerDT = new NbsCaseAnswerDto();
                            caseAnswerDT.setAnswerTxt(value);
                            CdaPhcProcessor.setStandardNBSCaseAnswerVals(phcDT, caseAnswerDT);
                            caseAnswerDT.setNbsQuestionUid(quesMetadata.getNbsQuestionUid());
                            caseAnswerDT.setNbsQuestionVersionCtrlNbr(quesMetadata.getQuestionVersionNbr());
                            caseAnswerDT.setSeqNbr(0);
                            answerMap.put(quesMetadata.getNbsQuestionUid(), caseAnswerDT);
                        }
                    }
                }
                pageActProxyContainer.getPageVO().setPamAnswerDTMap(answerMap);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createActEntityObject(ParticipationDto partDT, PageActProxyContainer pageActProxyContainer,
                                       PamProxyContainer pamActProxyVO, Collection<NbsActEntityDto> nbsActEntityDTColl, Collection<ParticipationDto> partColl ) throws DataProcessingException {

        partDT.setActClassCd(NEDSSConstant.CLASS_CD_CASE);
        if(pageActProxyContainer !=null)
        {
            partDT.setActUid(pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid());
        }
        else
        {
            partDT.setActUid(pamActProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid());
        }
        var tree = catchingValueService.getCodedValue(partDT.getTypeCd());
        if (tree.containsKey(partDT.getTypeCd())) {
            partDT.setTypeDescTxt(tree.get(partDT.getTypeCd()));
        }

        partDT.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        partDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        partDT.setStatusTime(new java.sql.Timestamp(new Date().getTime()));
        partDT.setItNew(true);
        partDT.setItDirty(false);
        partColl.add(partDT);

        NbsActEntityDto nbsActEntityDT = new NbsActEntityDto();

        if(pageActProxyContainer !=null){
            nbsActEntityDT.setAddTime(pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getAddTime());
            nbsActEntityDT.setLastChgTime(pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getLastChgTime());
            nbsActEntityDT.setLastChgUserId(pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getLastChgUserId());
            nbsActEntityDT.setRecordStatusCd(pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getRecordStatusCd());
            nbsActEntityDT.setAddUserId(pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getAddUserId());
        }else{
            nbsActEntityDT.setAddTime(pamActProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getAddTime());
            nbsActEntityDT.setLastChgTime(pamActProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getLastChgTime());
            nbsActEntityDT.setLastChgUserId(pamActProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getLastChgUserId());
            nbsActEntityDT.setRecordStatusCd(pamActProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getRecordStatusCd());
            nbsActEntityDT.setAddUserId(pamActProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getAddUserId());
        }
        nbsActEntityDT.setActUid(partDT.getActUid());
        nbsActEntityDT.setEntityUid(partDT.getSubjectEntityUid());
        nbsActEntityDT.setEntityVersionCtrlNbr(1);
        nbsActEntityDT.setRecordStatusTime(partDT.getRecordStatusTime());
        nbsActEntityDT.setTypeCd(partDT.getTypeCd());
        nbsActEntityDT.setItNew(true);
        nbsActEntityDT.setItDirty(false);
        nbsActEntityDTColl.add(nbsActEntityDT);

    }

}
