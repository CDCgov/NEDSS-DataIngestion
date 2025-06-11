package gov.cdc.dataprocessing.service.implementation.answer;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PageContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsActJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsAnswerJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntityHist;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswerHist;
import gov.cdc.dataprocessing.service.interfaces.answer.IAnswerService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class AnswerService implements IAnswerService {
    private final NbsActJdbcRepository nbsActJdbcRepository;
    private final NbsAnswerJdbcRepository answerJdbcRepository;

    @Value("${service.timezone}")
    private String tz = "UTC";
    public AnswerService(
            NbsActJdbcRepository nbsActJdbcRepository,
            NbsAnswerJdbcRepository answerJdbcRepository) {
        this.nbsActJdbcRepository = nbsActJdbcRepository;
        this.answerJdbcRepository = answerJdbcRepository;
    }

    public PageContainer getNbsAnswerAndAssociation(Long uid) {
        PageContainer pageContainer = new PageContainer();
        Map<Object,Object> answerDTReturnMap = getPageAnswerDTMaps(uid);
        Map<Object, NbsAnswerDto> nbsAnswerMap;
        Map<Object, Object> nbsRepeatingAnswerMap;
        nbsAnswerMap=(HashMap<Object, NbsAnswerDto>)answerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION);
        nbsRepeatingAnswerMap=(HashMap<Object, Object>)answerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION);

        pageContainer.setAnswerDTMap(nbsAnswerMap);
        pageContainer.setPageRepeatingAnswerDTMap(nbsRepeatingAnswerMap);

        var result = nbsActJdbcRepository.getNbsActEntitiesByActUid(uid);
        Collection<NbsActEntityDto> pageCaseEntityDTCollection= new ArrayList<>();
        for(var item : result) {
            var elem = new NbsActEntityDto(item);
            pageCaseEntityDTCollection.add(elem);
        }

        pageContainer.setActEntityDTCollection(pageCaseEntityDTCollection);
        return pageContainer;

    }

    @SuppressWarnings("java:S3776")
    public Map<Object, Object> getPageAnswerDTMaps(Long actUid) {
        ArrayList<NbsAnswerDto> pageAnswerDTCollection = new ArrayList<>();
        Map<Object, Object> nbsReturnAnswerMap = new HashMap<>();
        Map<Object, Object> nbsAnswerMap = new HashMap<>();
        Map<Object, Object> nbsRepeatingAnswerMap = new HashMap<>();


        var result = answerJdbcRepository.findByActUid(actUid);
        for(var item : result){
            var elem = new NbsAnswerDto(item);
            pageAnswerDTCollection.add(elem);
        }


        Iterator<NbsAnswerDto> it = pageAnswerDTCollection.iterator();
        Long nbsQuestionUid = 0L;
        Collection<NbsAnswerDto> coll = new ArrayList<>();
        while (it.hasNext())
        {
            NbsAnswerDto pageAnsDT = it.next();

            nbsQuestionUid = processingPageAnswerMap(pageAnsDT,
                     nbsRepeatingAnswerMap,
                     nbsQuestionUid, coll,
                     nbsAnswerMap);

            if (!it.hasNext() && !coll.isEmpty())
            {
                nbsAnswerMap.put(pageAnsDT.getNbsQuestionUid(), coll);
            }
        }

        nbsReturnAnswerMap.put(NEDSSConstant.NON_REPEATING_QUESTION, nbsAnswerMap);
        nbsReturnAnswerMap.put(NEDSSConstant.REPEATING_QUESTION, nbsRepeatingAnswerMap);

        return nbsReturnAnswerMap;
    }

    protected Long processingPageAnswerMap(NbsAnswerDto pageAnsDT,  Map<Object, Object> nbsRepeatingAnswerMap,
                                           Long nbsQuestionUid, Collection<NbsAnswerDto> coll,
                                           Map<Object, Object> nbsAnswerMap) {
        if (pageAnsDT.getAnswerGroupSeqNbr() != null && pageAnsDT.getAnswerGroupSeqNbr() > -1)
        {
            if (nbsRepeatingAnswerMap.get(pageAnsDT.getNbsQuestionUid()) == null)
            {
                Collection<NbsAnswerDto> collection = new ArrayList<>();
                collection.add(pageAnsDT);
                nbsRepeatingAnswerMap.put(pageAnsDT.getNbsQuestionUid(), collection);
            }
            else
            {
                Collection<NbsAnswerDto>  collection = (Collection<NbsAnswerDto> ) nbsRepeatingAnswerMap.get(pageAnsDT.getNbsQuestionUid());
                collection.add(pageAnsDT);
                nbsRepeatingAnswerMap.put(pageAnsDT.getNbsQuestionUid(), collection);
            }
        }
        else if ((pageAnsDT.getNbsQuestionUid().compareTo(nbsQuestionUid) == 0)
                && pageAnsDT.getSeqNbr() != null && pageAnsDT.getSeqNbr() > 0)
        {
            coll.add(pageAnsDT);
        }
        else if (pageAnsDT.getSeqNbr() != null && pageAnsDT.getSeqNbr() > 0)
        {
            if (!coll.isEmpty())
            {
                nbsAnswerMap.put(nbsQuestionUid, coll);
                coll = new ArrayList<>();
            }
            coll.add(pageAnsDT);
        }
        else
        {
            if (!coll.isEmpty())
            {
                nbsAnswerMap.put(nbsQuestionUid, coll);
            }
            nbsAnswerMap.put(pageAnsDT.getNbsQuestionUid(), pageAnsDT);
        }
        nbsQuestionUid = pageAnsDT.getNbsQuestionUid();
        return nbsQuestionUid;
    }


    public void insertPageVO(PageContainer pageContainer, ObservationDto rootDTInterface) {
        if(pageContainer !=null && pageContainer.getAnswerDTMap() !=null ) {
            Collection<Object> answerDTColl = new ArrayList<>(pageContainer.getAnswerDTMap().values());
            if(!answerDTColl.isEmpty()) {
                storeAnswerDTCollection(answerDTColl);
            }
            if(pageContainer.getPageRepeatingAnswerDTMap() != null) {
                Collection<Object> interviewRepeatingAnswerDTColl = pageContainer.getPageRepeatingAnswerDTMap().values();
                if(!interviewRepeatingAnswerDTColl.isEmpty()) {
                    storeAnswerDTCollection(interviewRepeatingAnswerDTColl);
                }
            }
        }

        if (pageContainer == null) {
            pageContainer = new PageContainer();
            pageContainer.setActEntityDTCollection(new ArrayList<>());
        }
        storeActEntityDTCollection(pageContainer.getActEntityDTCollection(), rootDTInterface);
    }


    public void storePageAnswer(PageContainer pageContainer, ObservationDto observationDto) {
        delete(observationDto);
        if(pageContainer != null && pageContainer.getAnswerDTMap() != null)
        {
            storeAnswerDTCollection(new ArrayList<>( pageContainer.getAnswerDTMap().values()));
        }
        if(pageContainer != null && pageContainer.getPageRepeatingAnswerDTMap() != null)
        {
            storeAnswerDTCollection(pageContainer.getPageRepeatingAnswerDTMap().values());
        }

        if (pageContainer != null) {
            insertActEntityDTCollection(pageContainer.getActEntityDTCollection());
        }
    }

    public void storeActEntityDTCollectionWithPublicHealthCase(Collection<NbsActEntityDto> pamDTCollection, PublicHealthCaseDto rootDTInterface)
    {
        if(!pamDTCollection.isEmpty()){
            for (NbsActEntityDto pamCaseEntityDT : pamDTCollection) {
                if (pamCaseEntityDT.isItDelete()) {
                    nbsActJdbcRepository.deleteNbsEntityAct(pamCaseEntityDT.getNbsActEntityUid());
                } else if (pamCaseEntityDT.isItDirty() || pamCaseEntityDT.isItNew()) {
                    var nbsActEntity = new NbsActEntity(pamCaseEntityDT);
                    nbsActEntity.setActUid(rootDTInterface.getPublicHealthCaseUid());
                    nbsActEntity.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                    nbsActEntity.setRecordStatusCd("OPEN");
                    nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp(tz));
                    nbsActJdbcRepository.mergeNbsActEntity(nbsActEntity);
                }
            }
        }
    }


    void storeActEntityDTCollection(Collection<NbsActEntityDto> pamDTCollection, ObservationDto rootDTInterface) {
        if(!pamDTCollection.isEmpty()){
            for (NbsActEntityDto pamCaseEntityDT : pamDTCollection) {
                if (pamCaseEntityDT.isItDelete()) {
                    nbsActJdbcRepository.deleteNbsEntityAct(pamCaseEntityDT.getNbsActEntityUid());
                } else if (pamCaseEntityDT.isItDirty() || pamCaseEntityDT.isItNew()) {
                    var nbsActEntity = new NbsActEntity(pamCaseEntityDT);
                    nbsActEntity.setActUid(rootDTInterface.getObservationUid());
                    nbsActEntity.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                    nbsActEntity.setRecordStatusCd("OPEN");
                    nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp(tz));
                    nbsActJdbcRepository.mergeNbsActEntity(new NbsActEntity(pamCaseEntityDT));
                }
            }
        }
    }

    void insertActEntityDTCollection(Collection<NbsActEntityDto> actEntityDTCollection) // NOSONAR
    {
        if(!actEntityDTCollection.isEmpty()){
            for (NbsActEntityDto pamCaseEntityDT : actEntityDTCollection) {
                nbsActJdbcRepository.mergeNbsActEntity(new NbsActEntity(pamCaseEntityDT));
            }
        }
    }

    @SuppressWarnings("java:S1172")
    protected void storeAnswerDTCollection(Collection<Object> answerDTColl) {
        if (answerDTColl != null){
            for (Object o : answerDTColl) {
                NbsAnswerDto answerDT = (NbsAnswerDto) o;
                if (answerDT.isItDirty() || answerDT.isItNew()) {
                    answerJdbcRepository.mergeNbsAnswer(new NbsAnswer(answerDT));
                } else if (answerDT.isItDelete()) {
                    answerJdbcRepository.deleteByNbsAnswerUid(answerDT.getNbsAnswerUid());
                }
            }
        }
    }

    protected void delete(ObservationDto rootDTInterface) {
        Collection<Object> answerCollection;

        var result = answerJdbcRepository.findByActUid(rootDTInterface.getObservationUid());
        answerCollection = new ArrayList<>();
        for(var item : result) {
            answerCollection.add(new NbsAnswerDto(item));
        }

        if(!answerCollection.isEmpty()) {
            insertAnswerHistoryDTCollection(answerCollection);
        }

        var actEntityResult = nbsActJdbcRepository.getNbsActEntitiesByActUid(rootDTInterface.getObservationUid());
        Collection<NbsActEntityDto> actEntityCollection;
        actEntityCollection = new ArrayList<>();
        for(var item: actEntityResult) {
            actEntityCollection.add(new NbsActEntityDto(item));
        }

        if(!actEntityCollection.isEmpty()) {
            insertPageEntityHistoryDTCollection(actEntityCollection, rootDTInterface);
        }
    }


    protected void insertAnswerHistoryDTCollection(Collection<Object> oldAnswerDTCollection)  {
        if (oldAnswerDTCollection != null) {
            for (Object obj : oldAnswerDTCollection) {
                if (obj instanceof ArrayList<?> && !((ArrayList<Object>) obj).isEmpty()) {
                    for (NbsAnswerDto answerDT : (ArrayList<NbsAnswerDto>) obj) {
                        answerJdbcRepository.deleteByNbsAnswerUid(answerDT.getNbsAnswerUid());
                        answerJdbcRepository.mergeNbsAnswerHist(new NbsAnswerHist(answerDT));
                    }
                } else if (obj instanceof NbsAnswerDto answerDT) {
                    answerJdbcRepository.deleteByNbsAnswerUid(answerDT.getNbsAnswerUid());
                    answerJdbcRepository.mergeNbsAnswerHist(new NbsAnswerHist(answerDT));
                }
            }
        }
    }

    protected void insertPageEntityHistoryDTCollection(Collection<NbsActEntityDto> nbsCaseEntityDTColl, ObservationDto oldrootDTInterface)
    {
        if (nbsCaseEntityDTColl != null) {
            for (NbsActEntityDto nbsActEntityDto : nbsCaseEntityDTColl) {
                nbsActJdbcRepository.deleteNbsEntityAct(nbsActEntityDto.getNbsActEntityUid());

                var data = new NbsActEntityHist(nbsActEntityDto);
                data.setLastChgTime(oldrootDTInterface.getLastChgTime());
                data.setLastChgUserId(oldrootDTInterface.getLastChgUserId());
                data.setRecordStatusCd(oldrootDTInterface.getRecordStatusCd());
                data.setRecordStatusTime(oldrootDTInterface.getRecordStatusTime());
                nbsActJdbcRepository.mergeNbsActEntityHist(data);
            }
        }
    }
}

