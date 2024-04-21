package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.DropDownCodeDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.*;
import gov.cdc.dataprocessing.model.dto.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.implementation.PageService;
import gov.cdc.dataprocessing.service.interfaces.IInvestigationService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

@Component
public class AssociatedInvestigationUtil {
    private static final Logger logger = LoggerFactory.getLogger(AssociatedInvestigationUtil.class);

    private final CustomRepository customRepository;
    private final PageService pageService;
    private final IInvestigationService investigationService;

    public AssociatedInvestigationUtil(CustomRepository customRepository,
                                       PageService pageService,
                                       IInvestigationService investigationService) {
        this.customRepository = customRepository;
        this.pageService = pageService;
        this.investigationService = investigationService;
    }

    public void updatForConInfectionId(PageActProxyVO pageActProxyVO, Long mprUid, Long currentPhclUid) throws DataProcessingException {
        try{
            updateForConInfectionId(pageActProxyVO, null, mprUid,  null, currentPhclUid, null, null);
        }catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    /**
     * @param pageActProxyVO:  PageActProxyVO that will update the other investigations that are part of co-infection group
     * @param mprUid: MPR UId for the cases tied to co-infection group
     * @param currentPhclUid: PHC_UID tied to pageActProxyVO
     * @param coinfectionSummaryVOCollection - Used for Merge Investigation
     * @param coinfectionIdToUpdate - coinfectionId Used for Merge Investigation
     */
    public void updateForConInfectionId(PageActProxyVO pageActProxyVO, PageActProxyVO supersededProxyVO, Long mprUid,
                                        Map<Object, Object> coInSupersededEpliLinkIdMap, Long currentPhclUid, 
                                        Collection<Object> coinfectionSummaryVOCollection, String coinfectionIdToUpdate) 
            throws DataProcessingException {
        try {
            String coninfectionId=pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCoinfectionId();
            if(coinfectionSummaryVOCollection==null)
                coinfectionSummaryVOCollection = getInvListForCoInfectionId(mprUid,coninfectionId);


            PageActProxyVO pageActProxyCopyVO = (PageActProxyVO)pageActProxyVO.deepCopy();
            Map<Object, Object> answermapMap =pageActProxyCopyVO.getPageVO().getPamAnswerDTMap();

            Map<Object, Object> repeatingAnswermapMap =pageActProxyCopyVO.getPageVO().getPageRepeatingAnswerDTMap();


            String investigationFormCd = CachedDropDowns.getConditionCdAndInvFormCd().get(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCd()).toString();

            Map<Object, Object> mapFromQuestions = new HashMap<Object,Object>();
            Collection<Object> nbsQuestionUidCollection = getCoinfectionQuestionListForFormCd(investigationFormCd);
            Map<Object,Object> updatedValuesMap = new HashMap<Object, Object>();

            Map<Object,Object> updateValueInOtherTablesMap = new HashMap<Object, Object>(); // Map is to update values in other table then NBS_CASE_Answer

            if(nbsQuestionUidCollection!=null) {
                Iterator<Object> iterator = nbsQuestionUidCollection.iterator();
                while(iterator.hasNext()) {
                    DropDownCodeDto dropDownCodeDT= (DropDownCodeDto)iterator.next();
                    mapFromQuestions.put(dropDownCodeDT.getKey(), dropDownCodeDT);

                    if(dropDownCodeDT.getAltValue()!=null && (dropDownCodeDT.getAltValue().contains("CASE_MANAGEMENT.")
                            || dropDownCodeDT.getAltValue().contains("PERSON.")
                            || dropDownCodeDT.getAltValue().contains("PUBLIC_HEALTH_CASE."))){
                        updateValueInOtherTablesMap.put(dropDownCodeDT.getKey(), dropDownCodeDT.getAltValue());
                    }else {
                        if(answermapMap.get(dropDownCodeDT.getKey())!=null) {
                            updatedValuesMap.put(dropDownCodeDT.getKey(), answermapMap.get(dropDownCodeDT.getKey()));
                        } else if(answermapMap.get(dropDownCodeDT.getLongKey())!=null) {
                            updatedValuesMap.put(dropDownCodeDT.getKey(), answermapMap.get(dropDownCodeDT.getLongKey()));
                        } else if((repeatingAnswermapMap.get(dropDownCodeDT.getLongKey()+"")!=null || repeatingAnswermapMap.get(dropDownCodeDT.getLongKey())!=null)
                                && updatedValuesMap.get(dropDownCodeDT.getLongKey())==null){
                            ArrayList list = (ArrayList)repeatingAnswermapMap.get(dropDownCodeDT.getLongKey().toString());
                            if(list == null)
                                list = (ArrayList)repeatingAnswermapMap.get(dropDownCodeDT.getLongKey());

                            if(list!=null && list.size()>0)
                                updatedValuesMap.put(dropDownCodeDT.getKey(), list);
                        }
                        else {
                            //if(dropDownCodeDT.getIntValue()==null) {
                            dropDownCodeDT.setValue( NEDSSConstant.NO_BATCH_ENTRY);
                            updatedValuesMap.put(dropDownCodeDT.getKey(), dropDownCodeDT);
                            //	}else {
                            //		updatedValuesMap.put(dropDownCodeDT.getKey(), dropDownCodeDT.getIntValue());
                            //	}
                        }
                    }
                }

            }
            if(coinfectionSummaryVOCollection!=null && coinfectionSummaryVOCollection.size()>0) {
                Iterator<Object> coinfectionsummIterator =coinfectionSummaryVOCollection.iterator();
                while(coinfectionsummIterator.hasNext()) {
                    CoinfectionSummaryContainer coninfectionSummaryVO= (CoinfectionSummaryContainer)coinfectionsummIterator.next();
                    if(coninfectionSummaryVO.getPublicHealthCaseUid().compareTo(currentPhclUid)!=0){
                        if(coinfectionIdToUpdate!=null){//Merge Case investigation scenario
                            updateCoInfectionInvest(updatedValuesMap, mapFromQuestions,pageActProxyVO, pageActProxyVO.getPublicHealthCaseVO(),
                                    supersededProxyVO.getPublicHealthCaseVO(), coInSupersededEpliLinkIdMap,
                                    coninfectionSummaryVO, coinfectionIdToUpdate, updateValueInOtherTablesMap);
                            /**Update for closed/open cases that are part of any co-infection groups */
                        }

                        else{
                            updateCoInfectionInvest(updatedValuesMap, mapFromQuestions,pageActProxyVO,  pageActProxyVO.getPublicHealthCaseVO(),
                                    null, null,
                                    coninfectionSummaryVO, null, updateValueInOtherTablesMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new  DataProcessingException(e.getMessage(), e);
        }
    }


    private ArrayList<Object> getInvListForCoInfectionId(Long mprUid,String coInfectionId) throws DataProcessingException {
        ArrayList<Object> coinfectionInvList = new ArrayList<Object>();
        coinfectionInvList = customRepository.getInvListForCoInfectionId(mprUid, coInfectionId);
        return coinfectionInvList;
    }


    private  void updateCoInfectionInvest(Map<Object, Object> mappedCoInfectionQuestions,Map<Object, Object>  fromMapQuestions, 
                                          PageActProxyVO pageActProxyVO ,PublicHealthCaseVO publicHealthCaseVO,
                                          PublicHealthCaseVO supersededPublicHealthCaseVO, 
                                          Map<Object, Object> coInSupersededEpliLinkIdMap, 
                                          CoinfectionSummaryContainer coninfectionSummaryVO, 
                                          String coinfectionIdToUpdate, 
                                          Map<Object, Object> updateValueInOtherTablesMap) 
            throws DataProcessingException {
        Long publicHealthCaseUid =null;
        try {
            String investigationFormCd = CachedDropDowns.getConditionCdAndInvFormCd().get(coninfectionSummaryVO.getConditionCd()).toString();
            Collection<Object> toNbsQuestionUidCollection = getCoinfectionQuestionListForFormCd(investigationFormCd);
            publicHealthCaseUid=coninfectionSummaryVO.getPublicHealthCaseUid();
            java.util.Date dateTime = new java.util.Date();
            Timestamp lastChgTime = new Timestamp(dateTime.getTime());
            Long lastChgUserId= Long.valueOf(AuthUtil.authUser.getUserId());
            PageActProxyVO proxyVO = (PageActProxyVO) investigationService.getPageProxyVO(NEDSSConstant.CASE, publicHealthCaseUid);
            if (!proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getInvestigationStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_OPEN)){
            }
            else{
                BasePamContainer pageVO = proxyVO.getPageVO();
                if(pageVO.getPamAnswerDTMap()!=null && toNbsQuestionUidCollection!=null) {
                    Iterator<Object> nbsQuestionIterator = toNbsQuestionUidCollection.iterator();
                    String currentToQuestionKey = "";
                    while(nbsQuestionIterator.hasNext( )) {
                        try {
                            DropDownCodeDto toDropDownCodeDT= (DropDownCodeDto)nbsQuestionIterator.next();
                            currentToQuestionKey = toDropDownCodeDT.getKey();
                            if(fromMapQuestions.get(toDropDownCodeDT.getKey())==null){
                                logger.warn("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is missing in the current investigation" );

                                continue;
                            }else {
                                DropDownCodeDto fromDropDownCodeDT = (DropDownCodeDto)fromMapQuestions.get(toDropDownCodeDT.getKey());
                                /*if(fromDropDownCodeDT.getIntValue()!=null && toDropDownCodeDT.getIntValue()==null){
									logger.warn("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
									logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
									logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is a batch question in the current investigation, however the question is not a batch question in the coinfection investigation. Hence ignored" );
									continue;
								}else*/
                                if(fromDropDownCodeDT.getIntValue()!=null && fromDropDownCodeDT.getIntValue().equals(NEDSSConstant.NO_BATCH_ENTRY)  && toDropDownCodeDT.getIntValue()!=null){
                                    logger.warn("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                    logger.warn("From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                    logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                    logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvestThe mapped question is a single select question in the current investigation, however the question is a batch question in the coinfection investigation. Hence ignored" );
                                    continue;
                                }else {

                                    logger.debug("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                    logger.debug("From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                    logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                    logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is being updated" );
                                    if(mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey())!=null && toDropDownCodeDT.getLongKey()!=null) {
                                        //	 if(pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) instanceof NbsCaseAnswerDT) {
                                        if(toDropDownCodeDT.getIntValue()==null) {
                                            Object object = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                            if(object !=null && object instanceof DropDownCodeDto && (((DropDownCodeDto)object).getValue().equalsIgnoreCase(NEDSSConstant.NO_BATCH_ENTRY))){
                                                //&& object.toString().equalsIgnoreCase(NEDSSConstant.DEL)) {
                                                Object thisObj = pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                if(thisObj!=null && thisObj instanceof NbsCaseAnswerDto) {
                                                    NbsCaseAnswerDto nbsCaseAnswerDT=(NbsCaseAnswerDto)pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                    nbsCaseAnswerDT.setItDelete(true);
                                                    nbsCaseAnswerDT.setItNew(false);
                                                    nbsCaseAnswerDT.setItDirty(false);
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), nbsCaseAnswerDT);
                                                } else if (thisObj != null && thisObj  instanceof ArrayList) { //multiSelect
                                                    ArrayList<?> aDTList = (ArrayList<?>) thisObj;
                                                    for (Object ansDT : aDTList)
                                                    {
                                                        if (ansDT instanceof NbsCaseAnswerDto) {
                                                            NbsCaseAnswerDto nbsCaseAnswerDT=(NbsCaseAnswerDto)ansDT;
                                                            nbsCaseAnswerDT.setItDelete(true);
                                                            nbsCaseAnswerDT.setItNew(false);
                                                            nbsCaseAnswerDT.setItDirty(false);
                                                        }
                                                    }
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), aDTList); //multiSelect ArrayList
                                                } //multiSel
                                            }
                                            else if(pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey())==null) {
                                                Object thisObj = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                if (thisObj !=null && thisObj instanceof NbsCaseAnswerDto) {
                                                    NbsCaseAnswerDto fromNbsCaseAnswerDT=(NbsCaseAnswerDto)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                    //NbsCaseAnswerDT nbsCaseAnswerDT=(NbsCaseAnswerDT)pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                    fromNbsCaseAnswerDT.setAnswerTxt(fromNbsCaseAnswerDT.getAnswerTxt());
                                                    fromNbsCaseAnswerDT.setActUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                                                    fromNbsCaseAnswerDT.setItDelete(false);
                                                    fromNbsCaseAnswerDT.setItNew(true);
                                                    fromNbsCaseAnswerDT.setItDirty(false);
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), fromNbsCaseAnswerDT);
                                                } else if (thisObj != null && thisObj  instanceof ArrayList) { //multiSelect
                                                    ArrayList<?> aDTList = (ArrayList<?>) thisObj;
                                                    for (Object ansDT : aDTList)
                                                    {
                                                        if (ansDT instanceof NbsCaseAnswerDto) {
                                                            NbsCaseAnswerDto fromNbsCaseAnswerDT=(NbsCaseAnswerDto)ansDT;
                                                            //fromNbsCaseAnswerDT.setAnswerTxt(fromNbsCaseAnswerDT.getAnswerTxt());
                                                            fromNbsCaseAnswerDT.setActUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                                                            fromNbsCaseAnswerDT.setItDelete(false);
                                                            fromNbsCaseAnswerDT.setItNew(true);
                                                            fromNbsCaseAnswerDT.setItDirty(false);
                                                        }
                                                    }
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), aDTList); //multi select arrayList
                                                }
                                            }else if(pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey())!=null) {
                                                Object thisObj = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                if(thisObj !=null && thisObj instanceof NbsCaseAnswerDto) {
                                                    NbsCaseAnswerDto fromNbsCaseAnswerDT=(NbsCaseAnswerDto)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                    NbsCaseAnswerDto toNbsCaseAnswerDT=(NbsCaseAnswerDto)pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                    toNbsCaseAnswerDT.setAnswerTxt(fromNbsCaseAnswerDT.getAnswerTxt());
                                                    toNbsCaseAnswerDT.setItDelete(false);
                                                    toNbsCaseAnswerDT.setItNew(false);
                                                    toNbsCaseAnswerDT.setItDirty(true);
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), toNbsCaseAnswerDT);
                                                } else if (thisObj != null && thisObj  instanceof ArrayList) { //multiSelect upd
                                                    ArrayList<?> aFromDTList = (ArrayList<?>) mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                    ArrayList<NbsCaseAnswerDto> aToDTList = (ArrayList<NbsCaseAnswerDto>) pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                    if (aToDTList == null)
                                                        aToDTList = new ArrayList<NbsCaseAnswerDto>();
                                                    int theLastSeq = 0;
                                                    for (Object fromAnsDT : aFromDTList) {
                                                        if (fromAnsDT instanceof NbsCaseAnswerDto) {
                                                            NbsCaseAnswerDto fromNbsCaseAnswerDT=(NbsCaseAnswerDto)fromAnsDT;
                                                            boolean isNotThere = true;//update seq or add new or del old
                                                            for (Object toAnsDT : aToDTList) {
                                                                NbsCaseAnswerDto toNbsCaseAnswerDT=(NbsCaseAnswerDto)toAnsDT;
                                                                if (toNbsCaseAnswerDT.getSeqNbr().intValue() == fromNbsCaseAnswerDT.getSeqNbr().intValue()) {
                                                                    isNotThere = false;
                                                                    toNbsCaseAnswerDT.setAnswerTxt(fromNbsCaseAnswerDT.getAnswerTxt());
                                                                    toNbsCaseAnswerDT.setItDelete(false);
                                                                    toNbsCaseAnswerDT.setItNew(false);
                                                                    toNbsCaseAnswerDT.setItDirty(true);
                                                                }
                                                            }
                                                            if (isNotThere) {
                                                                NbsCaseAnswerDto newCaseAnswerDT = fromNbsCaseAnswerDT;
                                                                newCaseAnswerDT.setActUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                                                                newCaseAnswerDT.setItDelete(false);
                                                                newCaseAnswerDT.setItNew(true);
                                                                newCaseAnswerDT.setItDirty(false);
                                                                newCaseAnswerDT.setSeqNbr(fromNbsCaseAnswerDT.getSeqNbr().intValue());
                                                                aToDTList.add(newCaseAnswerDT);
                                                            }
                                                            if (fromNbsCaseAnswerDT.getSeqNbr().intValue() > theLastSeq)
                                                                theLastSeq = fromNbsCaseAnswerDT.getSeqNbr().intValue();
                                                        }
                                                    } //fromAnsDT iter
                                                    //check if any are past the last sequence number and need to be deleted
                                                    for (Object toAnsDT : aToDTList) {
                                                        NbsCaseAnswerDto toNbsCaseAnswerDT=(NbsCaseAnswerDto)toAnsDT;
                                                        if (toNbsCaseAnswerDT.getSeqNbr().intValue() > theLastSeq) {
                                                            toNbsCaseAnswerDT.setItDelete(true);
                                                            toNbsCaseAnswerDT.setItNew(false);
                                                            toNbsCaseAnswerDT.setItDirty(false);
                                                        }
                                                    }

                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), aToDTList);
                                                } //multisel upd
                                            }

                                        }else if(toDropDownCodeDT.getIntValue()!=null && toDropDownCodeDT.getIntValue().intValue()>0) {
                                            Object objectRef = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                            if(objectRef !=null && objectRef instanceof DropDownCodeDto && ((DropDownCodeDto)objectRef).getValue().equalsIgnoreCase(NEDSSConstant.NO_BATCH_ENTRY)){
                                                ArrayList<NbsCaseAnswerDto> list=(ArrayList<NbsCaseAnswerDto>)pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                //pageVO.getPageRepeatingAnswerDTMap().remove(toDropDownCodeDT.getLongKey());
                                                //pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey().toString(), null);


                                                //ArrayList<?> list=(ArrayList<?>)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                list=changeStatus(list, proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid(),false, false,true,lastChgUserId,lastChgTime);
                                                pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey(), list);

                                            }
                                            else if(pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey())==null) {

                                                ArrayList<NbsCaseAnswerDto> list=(ArrayList<NbsCaseAnswerDto>)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                list=changeStatus(list, proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid(),true, false,false,lastChgUserId,lastChgTime);

                                                pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey(), list);

                                            }else if(pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey())!=null) {
                                                ArrayList<NbsCaseAnswerDto> deleteList=(ArrayList<NbsCaseAnswerDto>)pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey());
                                                deleteList=changeStatus(deleteList, proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid(),false, false,true,lastChgUserId,lastChgTime);

                                                ArrayList<NbsCaseAnswerDto> list=(ArrayList<NbsCaseAnswerDto>)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                list=changeStatus(list, proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid(),true, false,false,lastChgUserId,lastChgTime);

                                                deleteList.addAll(list);

                                                pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey(), deleteList);
                                            }
                                        }else {
                                            logger.error("\n\nPLEASE check!!!TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                            logger.error("PLEASE check!!!From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                            logger.error("PLEASE check!!!AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                            logger.error("PLEASE check!!!AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is being updated" );

                                        /*NbsCaseAnswerDT currentNbsCaseAnswerDT=(NbsCaseAnswerDT)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
										currentNbsCaseAnswerDT.setAddTime(lastChgTime);
										currentNbsCaseAnswerDT.setAddUserId(lastChgUserId);
										currentNbsCaseAnswerDT.setItNew(true);
										currentNbsCaseAnswerDT.setActUid(publicHealthCaseUid);
										pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getKey(), currentNbsCaseAnswerDT);*/
                                        }

                                    }else if(mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey())==null && toDropDownCodeDT.getLongKey()!=null) {
                                        logger.debug("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                        logger.debug("From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                        logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                        logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:current investigation does not have that question, however the coninfection PHC case has question. Hence ignored" );

                                        continue;
                                    }else if(mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey())!=null && toDropDownCodeDT.getLongKey()==null) {
                                        logger.debug("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                        logger.debug("From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                        logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                        logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:current investigation does have that question, however the coninfection PHC case does not has the same question. Hence ignored" );

                                        continue;
                                    }

                                }

                            }
                        }catch (Exception e) {
                            String errorMessage ="Error processing co-infection question " +currentToQuestionKey + " " + e.getCause()+ e.getMessage();

                        }

                    }

                }
            }
            /**
             * Merge Investigation case issue where the superseded investigation should not allowed to update!!!
             * 1. Only cases that are not Merge Investigation are allowed to proceed
             * 2. Only cases that are Merge Investigation that are not superseded are allowed to proceed
             * 3. Even Closed cases that are part of co-infection are NOW allowed to proceed with updated co-infection id(https://nbsteamdev.atlassian.net/browse/ND-9114
             * 		Description Losing investigation's Coinfection is not assigned the correct Co-Infection Id when status = Closed)
             *
             */
            //Set the winning investigation's coinfectionId to losing investigation's related co-infection investigations.
            if(coinfectionIdToUpdate!=null){
                String survivingEpiLinkId = publicHealthCaseVO.getTheCaseManagementDT().getEpiLinkId();
                String supersededEpiLinkId = supersededPublicHealthCaseVO.getTheCaseManagementDT().getEpiLinkId();

                if(coInSupersededEpliLinkIdMap.get(proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid()) !=null) {
                    proxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().setEpiLinkId(survivingEpiLinkId);
                }
                proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setCoinfectionId(coinfectionIdToUpdate);
                proxyVO.setMergeCase(true);
            }

            // Updates coinfection question's values in tables other than NBS_Case_Answer
            updateCoInfectionInvestForOtherTables(proxyVO, updateValueInOtherTablesMap, pageActProxyVO, publicHealthCaseVO);

            if(coinfectionIdToUpdate==null
                    || (supersededPublicHealthCaseVO!= null
                    && publicHealthCaseUid.compareTo(supersededPublicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid())!=0))
            {
                updatePageProxyVOInterface(proxyVO,lastChgTime,lastChgUserId);
                Long phcUid = pageService.setPageActProxyVO( proxyVO);
                logger.debug("updateCoInfectionInvest method call completed for coinfectionIdToUpdate:"+ coinfectionIdToUpdate);
            }

        }catch(Exception e) {

            throw new DataProcessingException(e.toString() ,e);
        }
    }

    private ArrayList<NbsCaseAnswerDto> changeStatus(ArrayList<NbsCaseAnswerDto> list,Long publicHealthCaseUid,
                                                     boolean itNew, boolean itDirty, boolean itDelete,Long lastChgUserId, Timestamp lastChgTime){
        if(list!=null) {
            Iterator<NbsCaseAnswerDto> iterator= list.iterator();
            while(iterator.hasNext()) {
                NbsCaseAnswerDto caseAnswerDT =  (NbsCaseAnswerDto)iterator.next();
                caseAnswerDT.setLastChgUserId(lastChgUserId);
                caseAnswerDT.setLastChgTime(lastChgTime);
                caseAnswerDT.setActUid(publicHealthCaseUid);
                caseAnswerDT.setItNew(itNew);
                caseAnswerDT.setItDirty(itDirty);
                caseAnswerDT.setItDelete(itDelete);
            }
        }
        return list;
    }


    private void updatePageProxyVOInterface(PageActProxyVO proxyActVO,Timestamp lastChgTime, Long lastChgUserId) throws DataProcessingException {
        try {
            proxyActVO.setRenterant(true);


            proxyActVO.setItDirty(true);
            proxyActVO.getPublicHealthCaseVO().setItDirty(true);
            proxyActVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setLastChgTime(lastChgTime);
            proxyActVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setLastChgUserId((lastChgUserId));
            proxyActVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setItDirty(true);

            if (proxyActVO.getThePersonContainerCollection() != null) {
                for (Iterator<PersonContainer> anIterator = proxyActVO.getThePersonContainerCollection().iterator(); anIterator.hasNext();) {
                    PersonContainer personVO= (PersonContainer)anIterator.next();
                    if (personVO.getThePersonDto().getCd().equals(NEDSSConstant.PAT)) {
                        personVO.getThePersonDto().setLastChgTime(lastChgTime);
                        personVO.getThePersonDto().setLastChgUserId(lastChgUserId);
                        personVO.getThePersonDto().setItDirty(true);
                        personVO.getThePersonDto().setItNew(false);

                    }
                }

                if (proxyActVO.getPageVO() != null) {
                    Map<Object, Object> map = proxyActVO.getPageVO().getPamAnswerDTMap();
                    if(map!=null) {
                        updateNbsCaseAnswerInterfaceValues(map, lastChgTime, lastChgUserId);
                    }
                    Map<Object, Object> repeatingMap = proxyActVO.getPageVO().getPageRepeatingAnswerDTMap();
                    if(repeatingMap!=null) {
                        updateNbsCaseAnswerInterfaceValues(repeatingMap, lastChgTime, lastChgUserId);
                    }
                    if(proxyActVO.getPageVO().getActEntityDTCollection()!=null) {
                        Iterator<NbsActEntityDto> iterator = proxyActVO.getPageVO().getActEntityDTCollection().iterator();
                        while(iterator.hasNext()) {
                            NbsActEntityDto actEntityDT= (NbsActEntityDto)iterator.next();
                            actEntityDT.setLastChgTime(lastChgTime);
                            actEntityDT.setLastChgUserId(lastChgUserId);
                            actEntityDT.setItDirty(true);
                            actEntityDT.setItNew(false);
                        }

                    }
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.toString());
        }
    }


    private Map<Object, Object> updateNbsCaseAnswerInterfaceValues(
            Map<Object, Object> map, Timestamp lastChgTime, Long lastChgUserId) throws DataProcessingException {
        Map<Object, Object> returnMap = new HashMap();

        try {
            Iterator<Object> iterator = map.keySet().iterator();
            while(iterator.hasNext()) {
                Object key = iterator.next();
                Object object = map.get(key);
                if(object instanceof NbsCaseAnswerDto) {
                    NbsCaseAnswerDto caseAnswerDT = (NbsCaseAnswerDto)object;
                    caseAnswerDT.setLastChgTime(lastChgTime);
                    caseAnswerDT.setLastChgUserId(lastChgUserId);
                    //caseAnswerDT.setItDirty(true);
                    //caseAnswerDT.setItNew(false);
                    if(!caseAnswerDT.isItDelete() && !caseAnswerDT.isItDirty() && !caseAnswerDT.isItNew()) {
                        caseAnswerDT.setItDirty(true);
                        caseAnswerDT.setItNew(false);
                    }
                    returnMap.put(key, caseAnswerDT);
                }else if(object instanceof ArrayList) {
                    @SuppressWarnings("unchecked")
                    ArrayList<Object>  list =(ArrayList<Object>)object;
                    ArrayList<NbsAnswerDto> returnList= new ArrayList<>();
                    Iterator<Object> listIterator = list.iterator();
                    while(listIterator.hasNext()) {
                        NbsCaseAnswerDto caseAnswerDT = (NbsCaseAnswerDto)listIterator.next();
                        caseAnswerDT.setLastChgTime(lastChgTime);
                        caseAnswerDT.setLastChgUserId(lastChgUserId);
                        //caseAnswerDT.setItDirty(false);
                        //caseAnswerDT.setItNew(true);
                        returnList.add(caseAnswerDT);
                    }
                    returnMap.put(key, returnList);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.toString());
        }
        return returnMap;
    }


    /**
     *
     * Updates coinfection question's values in tables other than NBS_Case_Answer
     *
     * @param pageActProxyVOofCoinfection
     * @param updateValueInOtherTablesMap
     * @param pageActProxyVO
     * @param publicHealthCaseVO
     */
    private  void updateCoInfectionInvestForOtherTables(PageActProxyVO pageActProxyVOofCoinfection,
                                                        Map<Object, Object> updateValueInOtherTablesMap,
                                                        PageActProxyVO pageActProxyVO ,
                                                        PublicHealthCaseVO publicHealthCaseVO) throws DataProcessingException {
        try {
            for (Object key : updateValueInOtherTablesMap.keySet()) {
                String dbLocation = (String) updateValueInOtherTablesMap.get(key);
                if(dbLocation!=null && dbLocation.contains("PERSON.")){
                    //Commented out as its tries to update MPR concurrently within same transaction.
                    // First for current investigation's patient and then coinfection investigation's patient.
                }else if(dbLocation!=null && dbLocation.contains("CASE_MANAGEMENT.")){
                    //TODO: INVESTIGATE THIS
//                    String columnName = dbLocation.substring(dbLocation.indexOf(".")+1,dbLocation.length());
//                    String getterMethod = DynamicBeanBinding.getGetterName(columnName);
//
//                    if(getterMethod!=null){
//                        String value = DynamicBeanBinding.getValueForMethod(publicHealthCaseVO.getTheCaseManagementDT(),getterMethod,publicHealthCaseVO.getTheCaseManagementDT().getClass().getName());
//
//                        if(value!=null){
//                            DynamicBeanBinding.populateBean(pageActProxyVOofCoinfection.getPublicHealthCaseVO().getTheCaseManagementDT(), columnName, value);
//
//                            pageActProxyVOofCoinfection.getPublicHealthCaseVO().getTheCaseManagementDT().setItDelete(true);
//                            pageActProxyVOofCoinfection.getPublicHealthCaseVO().getTheCaseManagementDT().setItNew(false);
//                        }
//                    }else{
//                        logger.debug("getterMethod does not found from columnName: "+columnName +", not updating coinfection questions.");
//                    }
                }
            }

        }catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
    }





}
