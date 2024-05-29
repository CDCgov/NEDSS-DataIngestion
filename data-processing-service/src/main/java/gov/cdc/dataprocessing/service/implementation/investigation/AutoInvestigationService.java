package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.RenderConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.CaseManagementDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.*;
import gov.cdc.dataprocessing.model.dto.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.lookup.PrePopMappingDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueDateDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueNumericDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueTxtDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCodeWithPA;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.ConditionCodeRepository;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IAutoInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.service.interfaces.other.ICatchingValueService;
import gov.cdc.dataprocessing.utilities.DynamicBeanBinding;
import gov.cdc.dataprocessing.utilities.RulesEngineUtil;
import gov.cdc.dataprocessing.utilities.StringUtils;
import gov.cdc.dataprocessing.utilities.component.public_health_case.CdaPhcProcessor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AutoInvestigationService implements IAutoInvestigationService {

    private final ConditionCodeRepository conditionCodeRepository;
    private final ICatchingValueService catchingValueService;
    private final ILookupService lookupService;
    public AutoInvestigationService(ConditionCodeRepository conditionCodeRepository,
                                    ICatchingValueService catchingValueService,
                                    LookupService lookupService) {
        this.conditionCodeRepository = conditionCodeRepository;
        this.catchingValueService = catchingValueService;
        this.lookupService = lookupService;
    }

    public Object autoCreateInvestigation(ObservationContainer observationVO,
                                          EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        PageActProxyVO pageActProxyVO = null;
        PamProxyContainer pamProxyVO = null;
        PublicHealthCaseVO phcVO= createPublicHealthCaseVO(observationVO, edxLabInformationDT);

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
            pamProxyVO.setPublicHealthCaseVO(phcVO);
        }
        else
        {
            pageActProxyVO = new PageActProxyVO();
            pageActProxyVO.setItNew(true);
            pageActProxyVO.setItDirty(false);
            pageActProxyVO.setPublicHealthCaseVO(phcVO);
            populateProxyFromPrePopMapping(pageActProxyVO, edxLabInformationDT);
        }
        try {
            Object obj=null;

            if(pageActProxyVO!=null)
            {
                obj=pageActProxyVO;
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

    public Object transferValuesTOActProxyVO(PageActProxyVO pageActProxyVO,PamProxyContainer pamActProxyVO,
                                             Collection<PersonContainer> personVOCollection,
                                             ObservationContainer rootObservationVO,
                                             Collection<Object> entities,
                                             Map<Object, Object> questionIdentifierMap) throws DataProcessingException{
        try {
            PersonContainer patientVO =null;
            boolean isOrgAsReporterOfPHCPartDT=false;
            boolean isPhysicianOfPHCDT=false;
            Collection<ParticipationDto> coll = rootObservationVO.getTheParticipationDtoCollection();
            Collection<ParticipationDto> partColl = new ArrayList<>();
            Collection<NbsActEntityDto> nbsActEntityDTColl = new ArrayList<>();
            long personUid=-1;
            if(pageActProxyVO!=null)
                personUid=pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid()-1;
            else{
                personUid=pamActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid()-1;
            }
            if(personVOCollection!=null){
                Iterator<PersonContainer> it=personVOCollection.iterator();
                while (it.hasNext()){
                    PersonContainer personVO=(PersonContainer)it.next();
                    if(personVO.getThePersonDto().getCd().equals("PAT")){
                        patientVO= personVO;
                        patientVO.getThePersonDto().setPersonUid(personUid);
                        Collection<PersonContainer> thePersonVOCollection = new ArrayList<>();
                        thePersonVOCollection.add(patientVO);
                        patientVO.setItNew(true);
                        patientVO.setItDirty(false);
                        patientVO.getThePersonDto().setItDirty(false);
                        patientVO.getThePersonDto().setItNew(true);
                        personVO.getThePersonDto().setElectronicInd(NEDSSConstant.ELECTRONIC_IND);
                        personVO.getThePersonDto().setStatusTime(new Timestamp(new Date().getTime()));
                        if(pageActProxyVO!=null)
                            pageActProxyVO.setThePersonContainerCollection(thePersonVOCollection);
                        else{
                            pamActProxyVO.setThePersonVOCollection(thePersonVOCollection);
                        }
                        break;
                    }
                }
            }
            if(entities!=null && entities.size()>0){
                Iterator iterator = entities.iterator();
                while(iterator.hasNext()){
                    EdxRuleManageDto edxRuleManageDT =(EdxRuleManageDto)iterator.next();
                    ParticipationDto participationDT = new ParticipationDto();
                    participationDT.setTypeCd(edxRuleManageDT.getParticipationTypeCode());
                    participationDT.setSubjectEntityUid(edxRuleManageDT.getParticipationUid());
                    participationDT.setSubjectClassCd(edxRuleManageDT.getParticipationClassCode());
                    if(participationDT.getTypeCd().equals("OrgAsReporterOfPHC")){
                        isOrgAsReporterOfPHCPartDT=true;
                    }else if(participationDT.getTypeCd().equals("PhysicianOfPHC")){
                        isPhysicianOfPHCDT=true;
                    }

                    createActEntityObject(participationDT,pageActProxyVO,pamActProxyVO,nbsActEntityDTColl,partColl);
                }
            }
            if(coll!=null){

                Iterator<ParticipationDto> it=coll.iterator();
                while (it.hasNext()){
                    ParticipationDto partDT = (ParticipationDto)it.next();
                    boolean createActEntity=false;
                    String typeCd =partDT.getTypeCd();
                    if(typeCd.equalsIgnoreCase(EdxELRConstant.ELR_AUTHOR_CD)&& partDT.getSubjectClassCd().equals(EdxELRConstant.ELR_ORG) && !isOrgAsReporterOfPHCPartDT){
                        createActEntity=true;
                        partDT.setTypeCd("OrgAsReporterOfPHC");
                    }
                    if(typeCd.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)&& partDT.getSubjectClassCd().equals(EdxELRConstant.ELR_PERSON_CD) && !isPhysicianOfPHCDT ){
                        createActEntity=true;
                        partDT.setTypeCd("PhysicianOfPHC");
                    }
                    //gst- ND-4326 Physician not getting populated..
                    if(typeCd.equalsIgnoreCase(EdxELRConstant.ELR_ORDERER_CD) && partDT.getSubjectClassCd().equals(EdxELRConstant.ELR_PERSON_CD) && !isPhysicianOfPHCDT ){
                        createActEntity=true;
                        partDT.setTypeCd("PhysicianOfPHC");
                    }
                    //Transfer the ordering facility over if it is on the PageBuilder page
                    if(typeCd.equalsIgnoreCase(EdxELRConstant.ELR_ORDERER_CD)&&
                            partDT.getSubjectClassCd().equals(EdxELRConstant.ELR_ORG) &&
                            partDT.getCd().equals(EdxELRConstant.ELR_OP_CD) &&
                            questionIdentifierMap != null &&
                            questionIdentifierMap.containsKey("NBS291")){
                        createActEntity=true;
                        partDT.setTypeCd("OrgAsClinicOfPHC");
                    }
                    if(typeCd.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_SUBJECT_CD) ){
                        createActEntity=true;
                        partDT.setTypeCd("SubjOfPHC");
                        partDT.setSubjectEntityUid(personUid);
                    }
                    if(createActEntity){
                        createActEntityObject(partDT,pageActProxyVO,pamActProxyVO,nbsActEntityDTColl,partColl);
                    }

                }

            }

            if(pageActProxyVO!=null){
                pageActProxyVO.setTheParticipationDtoCollection(partColl);
                BasePamContainer pamVO = null;
                if(pageActProxyVO.getPageVO()!=null)
                    pamVO=pageActProxyVO.getPageVO();
                else
                    pamVO = new BasePamContainer();
                pamVO.setActEntityDTCollection(nbsActEntityDTColl);
                pageActProxyVO.setPageVO(pamVO);
                return pageActProxyVO;
            }
            else{
                pamActProxyVO.setTheParticipationDTCollection(partColl);
                BasePamContainer pamVO = null;
                if(pamActProxyVO.getPamVO()!=null)
                    pamVO=pamActProxyVO.getPamVO();
                else
                    pamVO = new BasePamContainer();
                pamVO.setActEntityDTCollection(nbsActEntityDTColl);
                pamActProxyVO.setPamVO(pamVO);
                return pamActProxyVO;
            }

        } catch (Exception e) {
            throw new DataProcessingException("AutoInvestigationHandler-transferValuesTOActProxyVO Exception raised"+e);
        }
    }

    private PublicHealthCaseVO createPublicHealthCaseVO(ObservationContainer observationVO, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        PublicHealthCaseVO phcVO = new PublicHealthCaseVO();

        phcVO.getThePublicHealthCaseDT().setLastChgTime(new java.sql.Timestamp(new Date().getTime()));

        phcVO.getThePublicHealthCaseDT().setPublicHealthCaseUid(Long.valueOf(edxLabInformationDT.getNextUid()-1));
        //edxLabInformationDT.setNextUid(edxLabInformationDT.getNextUid());
        phcVO.getThePublicHealthCaseDT().setJurisdictionCd((observationVO.getTheObservationDto().getJurisdictionCd()));
        phcVO.getThePublicHealthCaseDT().setRptFormCmpltTime(observationVO.getTheObservationDto().getRptToStateTime());
        //phcVO.getThePublicHealthCaseDT().setCaseClassCd(EdxELRConstant.ELR_CONFIRMED_CD);

        phcVO.getThePublicHealthCaseDT().setAddTime(new Timestamp(new Date().getTime()));
        //TODO: SECURITY
        //phcVO.getThePublicHealthCaseDT().setAddUserId(Long.valueOf("securityObj.getEntryID()"));
        phcVO.getThePublicHealthCaseDT().setAddUserId(Long.valueOf(2121L));
        phcVO.getThePublicHealthCaseDT().setCaseTypeCd(EdxELRConstant.ELR_INDIVIDUAL);

        var res = conditionCodeRepository.findProgramAreaConditionCodeByConditionCode(edxLabInformationDT.getConditionCode());
        ConditionCodeWithPA programAreaVO = new ConditionCodeWithPA();
        if (res.isPresent()) {
            programAreaVO = res.get().get(0);
        }
        phcVO.getThePublicHealthCaseDT().setCd(programAreaVO.getConditionCd());
        phcVO.getThePublicHealthCaseDT().setProgAreaCd(programAreaVO.getStateProgAreaCode());

        if(SrteCache.checkWhetherPAIsStdOrHiv(programAreaVO.getStateProgAreaCode()))
            phcVO.getThePublicHealthCaseDT().setReferralBasisCd(NEDSSConstant.REFERRAL_BASIS_LAB);

        phcVO.getThePublicHealthCaseDT().setSharedInd(NEDSSConstant.TRUE);
        phcVO.getThePublicHealthCaseDT().setCdDescTxt(programAreaVO.getConditionShortNm());
        phcVO.getThePublicHealthCaseDT().setGroupCaseCnt(1);
        phcVO.getThePublicHealthCaseDT().setInvestigationStatusCd(EdxELRConstant.ELR_OPEN_CD);
        phcVO.getThePublicHealthCaseDT().setActivityFromTime(
                StringUtils.stringToStrutsTimestamp(StringUtils
                        .formatDate(new Timestamp((new Date()).getTime()))));
        Calendar now = Calendar.getInstance();
        String dateValue = (now.get(Calendar.MONTH)+1) +"/" + now.get(Calendar.DATE) +"/" + now.get(Calendar.YEAR);
        int[] weekAndYear = RulesEngineUtil.CalcMMWR(dateValue);
        phcVO.getThePublicHealthCaseDT().setMmwrWeek(weekAndYear[0]+"");
        phcVO.getThePublicHealthCaseDT().setMmwrYear(weekAndYear[1]+"");
        phcVO.getThePublicHealthCaseDT().setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        if (edxLabInformationDT.getConditionCode() != null) {
            phcVO.setCoinfectionCondition(SrteCache.coInfectionConditionCode.containsKey(edxLabInformationDT.getConditionCode())? true:false);
            if (phcVO.isCoinfectionCondition()) {
                phcVO.getThePublicHealthCaseDT().setCoinfectionId(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE);
            }
        }
        phcVO.getThePublicHealthCaseDT().setItNew(true);
        phcVO.getThePublicHealthCaseDT().setItDirty(false);
        phcVO.setItNew(true);
        phcVO.setItDirty(false);

        try{
            boolean isSTDProgramArea = SrteCache.checkWhetherPAIsStdOrHiv(phcVO.getThePublicHealthCaseDT().getProgAreaCd());
            if (isSTDProgramArea) {
                //gt-ND-4592 - STD_HIV_DATAMART Fails To Populate Investigations Created From An ELR
                // per Pradeep need an empty case mgt
                CaseManagementDT caseMgtDT = new CaseManagementDT();
                caseMgtDT.setPublicHealthCaseUid(phcVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                //caseMgtDT.setItNew(true); //not currently used
                //caseMgtDT.setItDirty(false); //not currently used
                caseMgtDT.setCaseManagementDTPopulated(true);
                phcVO.setTheCaseManagementDT(caseMgtDT);
            }
        } catch(Exception ex){
            throw new DataProcessingException("Unexpected exception setting CaseManagementDT to PHC -->" +ex.toString());
        }

        return phcVO;
    }

    private void populateProxyFromPrePopMapping(PageActProxyVO pageActProxyVO, EdxLabInformationDto edxLabInformationDT)
            throws DataProcessingException {
        try {
            lookupService.fillPrePopMap();
            TreeMap<Object, Object> fromPrePopMap = (TreeMap<Object, Object>) OdseCache.fromPrePopFormMapping
                    .get(NEDSSConstant.LAB_FORM_CD);
            if (fromPrePopMap == null) {
                fromPrePopMap = new TreeMap<>();
            }
            Collection<ObservationContainer> obsCollection = edxLabInformationDT.getLabResultProxyContainer()
                    .getTheObservationContainerCollection();
            TreeMap<Object, Object> prePopMap = new TreeMap<Object, Object>();
            ObservationContainer obsVO = null;

            // Begin Dynamic Pre-pop mapping

            Iterator<ObservationContainer> ite = obsCollection.iterator();
            while (ite.hasNext()) {
                ObservationContainer obs = ite.next();
                if (obs.getTheObsValueNumericDtoCollection() != null
                        && obs.getTheObsValueNumericDtoCollection().size() > 0
                        && fromPrePopMap.containsKey(obs.getTheObservationDto().getCd())) {

                    List<ObsValueNumericDto> obsValueNumList = new ArrayList<>(obs.getTheObsValueNumericDtoCollection());
                    String value = obsValueNumList.get(0).getNumericUnitCd() == null
                            ?  obsValueNumList.get(0).getNumericValue1() + ""
                            :  obsValueNumList.get(0).getNumericValue1() + "^"
                            +  obsValueNumList.get(0).getNumericUnitCd();
                    prePopMap.put(obs.getTheObservationDto().getCd(), value);
                } else if (obs.getTheObsValueDateDtoCollection() != null
                        && obs.getTheObsValueDateDtoCollection().size() > 0
                        && fromPrePopMap.containsKey(obs.getTheObservationDto().getCd())) {
                    List<ObsValueDateDto> obsValueDateList = new ArrayList<>(obs.getTheObsValueDateDtoCollection());

                    String value = StringUtils.formatDate(obsValueDateList.get(0).getFromTime());
                    prePopMap.put(obs.getTheObservationDto().getCd(), value);
                } else if (obs.getTheObsValueCodedDtoCollection() != null
                        && obs.getTheObsValueCodedDtoCollection().size() > 0) {

                    List<ObsValueCodedDto> obsValueCodeList = new ArrayList<>(obs.getTheObsValueCodedDtoCollection());

                    String key = obs.getTheObservationDto().getCd() + "$" + obsValueCodeList.get(0).getCode();
                    if (fromPrePopMap.containsKey(key))
                    {
                        prePopMap.put(key, obsValueCodeList.get(0).getCode());
                    }
                    else if (fromPrePopMap.containsKey(obs.getTheObservationDto().getCd()))
                    {
                        prePopMap.put(obs.getTheObservationDto().getCd(), obsValueCodeList.get(0).getCode());
                    }
                } else if (obs.getTheObsValueTxtDtoCollection() != null && obs.getTheObsValueTxtDtoCollection().size() > 0
                        && fromPrePopMap.containsKey(obs.getTheObservationDto().getCd())) {
                    Iterator<ObsValueTxtDto> txtIte = obs.getTheObsValueTxtDtoCollection().iterator();
                    while (txtIte.hasNext()) {
                        ObsValueTxtDto obsValueTxtDT = (ObsValueTxtDto) txtIte.next();
                        if (obsValueTxtDT.getTxtTypeCd() == null || obsValueTxtDT.getTxtTypeCd().trim().equals("")
                                || obsValueTxtDT.getTxtTypeCd().equalsIgnoreCase("O")) {
                            prePopMap.put(obs.getTheObservationDto().getCd(), obsValueTxtDT.getValueTxt());
                            break;
                        }
                    }
                }
            }
            populateFromPrePopMapping(prePopMap, pageActProxyVO);

        } catch (Exception e) {
            throw new DataProcessingException("AutoInvestigationHandler-populateProxyFromPrePopMapping Exception raised" + e);
        }
    }

    private void populateFromPrePopMapping(TreeMap<Object, Object> prePopMap, PageActProxyVO pageActProxyVO)
            throws Exception {
        try {
            PublicHealthCaseDT phcDT = pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT();
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
            String investigationFormCd = programAreaVO.getInvestigationFormCd();

            Map<Object, Object> questionMap = (Map<Object, Object>) OdseCache.dmbMap.get(investigationFormCd);

            if (prePopMap == null || prePopMap.size() == 0)
                return;
            TreeMap<Object, Object> toPrePopMap = (TreeMap<Object, Object>) lookupService
                    .getToPrePopFormMapping(investigationFormCd);
            if (toPrePopMap != null && toPrePopMap.size() > 0) {
                Collection<Object> toPrePopColl = toPrePopMap.values();
                Map<Object, Object> answerMap = new HashMap<Object, Object>();
                if (toPrePopColl != null && toPrePopColl.size() > 0) {
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
                                dataLocation = quesMetadata.getDataLocation();
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
//                                    logger.error("Could not convert to date from value :" + prePopMap.get(mappingKey));
                                }
                            }

                            else if (toPrePopMappingDT.getToAnswerCode() != null)
                                value = toPrePopMappingDT.getToAnswerCode();
                            else
                                value = (String) prePopMap.get(mappingKey);

                            if (value != null && dataLocation != null
                                    && dataLocation.startsWith(RenderConstant.PUBLIC_HEALTH_CASE)) {
                                String columnName = dataLocation.substring(dataLocation.indexOf(".") + 1,
                                        dataLocation.length());
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
                }
                pageActProxyVO.getPageVO().setPamAnswerDTMap(answerMap);
            }
            else
            {
//                logger.debug("No pre-pop mapping for Code: "+investigationFormCd);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    private void createActEntityObject(ParticipationDto partDT, PageActProxyVO pageActProxyVO,
                                       PamProxyContainer pamActProxyVO, Collection<NbsActEntityDto> nbsActEntityDTColl, Collection<ParticipationDto> partColl ) throws DataProcessingException {

        partDT.setActClassCd(NEDSSConstant.CLASS_CD_CASE);
        if(pageActProxyVO!=null)
            partDT.setActUid(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid());
        else
            partDT.setActUid(pamActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid());
        //partDT.setTypeCd(typeCd.trim());
        //partDT.setTypeDescTxt(srtc.getDescForCode("PAR_TYPE", partDT.getTypeCd()));
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

        if(pageActProxyVO!=null){
            nbsActEntityDT.setAddTime(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getAddTime());
            nbsActEntityDT.setLastChgTime(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getLastChgTime());
            nbsActEntityDT.setLastChgUserId(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getLastChgUserId());
            nbsActEntityDT.setRecordStatusCd(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getRecordStatusCd());
            nbsActEntityDT.setAddUserId(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getAddUserId());
        }else{
            nbsActEntityDT.setAddTime(pamActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getAddTime());
            nbsActEntityDT.setLastChgTime(pamActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getLastChgTime());
            nbsActEntityDT.setLastChgUserId(pamActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getLastChgUserId());
            nbsActEntityDT.setRecordStatusCd(pamActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getRecordStatusCd());
            nbsActEntityDT.setAddUserId(pamActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getAddUserId());
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
