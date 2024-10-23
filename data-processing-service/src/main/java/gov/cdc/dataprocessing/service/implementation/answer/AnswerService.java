package gov.cdc.dataprocessing.service.implementation.answer;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PageContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntityHist;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswerHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.NbsActEntityHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.NbsActEntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsAnswerHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsAnswerRepository;
import gov.cdc.dataprocessing.service.interfaces.answer.IAnswerService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AnswerService implements IAnswerService {
    private static final Logger logger = LoggerFactory.getLogger(AnswerService.class); //NOSONAR

    private final NbsAnswerRepository nbsAnswerRepository;
    private final NbsActEntityRepository nbsActEntityRepository;
    private final NbsAnswerHistRepository nbsAnswerHistRepository;
    private final NbsActEntityHistRepository nbsActEntityHistRepository;

    public AnswerService(NbsAnswerRepository nbsAnswerRepository,
                         NbsActEntityRepository nbsActEntityRepository,
                         NbsAnswerHistRepository nbsAnswerHistRepository,
                         NbsActEntityHistRepository nbsActEntityHistRepository) {
        this.nbsAnswerRepository = nbsAnswerRepository;
        this.nbsActEntityRepository = nbsActEntityRepository;
        this.nbsAnswerHistRepository = nbsAnswerHistRepository;
        this.nbsActEntityHistRepository = nbsActEntityHistRepository;
    }

    public PageContainer getNbsAnswerAndAssociation(Long uid) throws DataProcessingException {
        PageContainer pageContainer = new PageContainer();
        try {

            Map<Object,Object> answerDTReturnMap = getPageAnswerDTMaps(uid);
            Map<Object, NbsAnswerDto> nbsAnswerMap;
            Map<Object, Object> nbsRepeatingAnswerMap;
            nbsAnswerMap=(HashMap<Object, NbsAnswerDto>)answerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION);
            nbsRepeatingAnswerMap=(HashMap<Object, Object>)answerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION);

            pageContainer.setAnswerDTMap(nbsAnswerMap);
            pageContainer.setPageRepeatingAnswerDTMap(nbsRepeatingAnswerMap);

            var result = nbsActEntityRepository.getNbsActEntitiesByActUid(uid);
            Collection<NbsActEntityDto> pageCaseEntityDTCollection= new ArrayList<>();
            if (result.isPresent()) {
                for(var item : result.get()) {
                    var elem = new NbsActEntityDto(item);
                    pageCaseEntityDTCollection.add(elem);
                }
            }
            pageContainer.setActEntityDTCollection(pageCaseEntityDTCollection);

        } catch (Exception e) {
            throw new DataProcessingException("InterviewAnswerRootDAOImpl:answerCollection- could not be returned", e);
        }
        return pageContainer;

    }

    @SuppressWarnings("java:S3776")
    public Map<Object, Object> getPageAnswerDTMaps(Long actUid) {
        ArrayList<NbsAnswerDto> pageAnswerDTCollection = new ArrayList<>();
        Map<Object, Object> nbsReturnAnswerMap = new HashMap<>();
        Map<Object, Object> nbsAnswerMap = new HashMap<>();
        Map<Object, Object> nbsRepeatingAnswerMap = new HashMap<>();


        var result = nbsAnswerRepository.getPageAnswerByActUid(actUid);
        if (result.isPresent()) {
            for(var item : result.get()){
                var elem = new NbsAnswerDto(item);
                pageAnswerDTCollection.add(elem);
            }

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

            if (!it.hasNext() && coll.size() > 0)
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
            if (coll.size() > 0)
            {
                nbsAnswerMap.put(nbsQuestionUid, coll);
                coll = new ArrayList<>();
            }
            coll.add(pageAnsDT);
        }
        else
        {
            if (coll.size() > 0)
            {
                nbsAnswerMap.put(nbsQuestionUid, coll);
            }
            nbsAnswerMap.put(pageAnsDT.getNbsQuestionUid(), pageAnsDT);
            coll = new ArrayList<>(); //NOSONAR
        }
        nbsQuestionUid = pageAnsDT.getNbsQuestionUid();
        return nbsQuestionUid;
    }
    @Transactional
    public void insertPageVO(PageContainer pageContainer, ObservationDto rootDTInterface) throws DataProcessingException{
        if(pageContainer !=null && pageContainer.getAnswerDTMap() !=null ) {
            Collection<Object> answerDTColl = new ArrayList<>(pageContainer.getAnswerDTMap().values());
            if(answerDTColl.size()>0) {
                storeAnswerDTCollection(answerDTColl, rootDTInterface);
            }
            if(pageContainer.getPageRepeatingAnswerDTMap() != null) {
                Collection<Object> interviewRepeatingAnswerDTColl = pageContainer.getPageRepeatingAnswerDTMap().values();
                if(interviewRepeatingAnswerDTColl.size()>0) {
                    storeAnswerDTCollection(interviewRepeatingAnswerDTColl, rootDTInterface);
                }
            }
        }

        if (pageContainer == null) {
            pageContainer = new PageContainer();
            pageContainer.setActEntityDTCollection(new ArrayList<>());
        }
        storeActEntityDTCollection(pageContainer.getActEntityDTCollection(), rootDTInterface);
    }


    @Transactional
    public void storePageAnswer(PageContainer pageContainer, ObservationDto observationDto) throws DataProcessingException{
        try {
            delete(observationDto);
            if(pageContainer != null && pageContainer.getAnswerDTMap() != null)
            {
                storeAnswerDTCollection(new ArrayList<>( pageContainer.getAnswerDTMap().values()), observationDto);
            }
            if(pageContainer != null && pageContainer.getPageRepeatingAnswerDTMap() != null)
            {
                storeAnswerDTCollection(pageContainer.getPageRepeatingAnswerDTMap().values(), observationDto);
            }

            if (pageContainer != null) {
                insertActEntityDTCollection(pageContainer.getActEntityDTCollection(), observationDto);
            }
        } catch (Exception e) {
            throw new DataProcessingException("InterviewAnswerRootDAOImpl:store- answerDTColl could not be stored", e);
        }
    }

    public void storeActEntityDTCollectionWithPublicHealthCase(Collection<NbsActEntityDto> pamDTCollection, PublicHealthCaseDto rootDTInterface)
    {
        if(!pamDTCollection.isEmpty()){
            for (NbsActEntityDto pamCaseEntityDT : pamDTCollection) {
                if (pamCaseEntityDT.isItDelete()) {
                    nbsActEntityRepository.deleteNbsEntityAct(pamCaseEntityDT.getNbsActEntityUid());
                } else if (pamCaseEntityDT.isItDirty() || pamCaseEntityDT.isItNew()) {
                    var nbsActEntity = new NbsActEntity(pamCaseEntityDT);
                    nbsActEntity.setActUid(rootDTInterface.getPublicHealthCaseUid());
                    nbsActEntity.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                    nbsActEntity.setRecordStatusCd("OPEN");
                    nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
                    nbsActEntityRepository.save(nbsActEntity);
                }
            }
        }
    }


    void storeActEntityDTCollection(Collection<NbsActEntityDto> pamDTCollection, ObservationDto rootDTInterface) {
        if(!pamDTCollection.isEmpty()){
            for (NbsActEntityDto pamCaseEntityDT : pamDTCollection) {
                if (pamCaseEntityDT.isItDelete()) {
                    nbsActEntityRepository.deleteNbsEntityAct(pamCaseEntityDT.getNbsActEntityUid());
                } else if (pamCaseEntityDT.isItDirty() || pamCaseEntityDT.isItNew()) {
                    var nbsActEntity = new NbsActEntity(pamCaseEntityDT);
                    nbsActEntity.setActUid(rootDTInterface.getObservationUid());
                    nbsActEntity.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                    nbsActEntity.setRecordStatusCd("OPEN");
                    nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
                    nbsActEntityRepository.save(new NbsActEntity(pamCaseEntityDT));
                }
            }
        }
    }

    void insertActEntityDTCollection(Collection<NbsActEntityDto> actEntityDTCollection, ObservationDto observationDto) // NOSONAR
    {
        if(!actEntityDTCollection.isEmpty()){
            for (NbsActEntityDto pamCaseEntityDT : actEntityDTCollection) {
                nbsActEntityRepository.save(new NbsActEntity(pamCaseEntityDT));
            }
        }
    }

    @SuppressWarnings("java:S1172")
    protected void storeAnswerDTCollection(Collection<Object> answerDTColl, ObservationDto interfaceDT) throws DataProcessingException {
        try {
            if (answerDTColl != null){
                for (Object o : answerDTColl) {
                    NbsAnswerDto answerDT = (NbsAnswerDto) o;
                    if (answerDT.isItDirty() || answerDT.isItNew()) {
                        nbsAnswerRepository.save(new NbsAnswer(answerDT));
                    } else if (answerDT.isItDelete()) {
                        nbsAnswerRepository.deleteNbsAnswer(answerDT.getNbsAnswerUid());
                    }
                }
            }
        } catch(Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    protected void delete(ObservationDto rootDTInterface) throws DataProcessingException{
        try {
            Collection<Object> answerCollection = null;

            var result = nbsAnswerRepository.getPageAnswerByActUid(rootDTInterface.getObservationUid());
            if (result.isPresent()) {
                answerCollection = new ArrayList<>();
                for(var item : result.get()) {
                    answerCollection.add(new NbsAnswerDto(item));
                }
            }
            if(answerCollection!=null && !answerCollection.isEmpty()) {
                insertAnswerHistoryDTCollection(answerCollection);
            }

            var actEntityResult = nbsActEntityRepository.getNbsActEntitiesByActUid(rootDTInterface.getObservationUid());
            Collection<NbsActEntityDto> actEntityCollection = null;
            if (actEntityResult.isPresent()) {
                actEntityCollection = new ArrayList<>();
                for(var item: actEntityResult.get()) {
                    actEntityCollection.add(new NbsActEntityDto(item));
                }
            }

            if(actEntityCollection!=null && !actEntityCollection.isEmpty()) {
                insertPageEntityHistoryDTCollection(actEntityCollection, rootDTInterface);
            }
        } catch (Exception e) {
            throw new DataProcessingException("InterviewAnswerRootDAOImpl:answerCollection- could not be returned", e);
        }

    }

    protected void insertAnswerHistoryDTCollection(Collection<Object> oldAnswerDTCollection) throws DataProcessingException {
        try {
            if (oldAnswerDTCollection != null) {
                for (Object obj : oldAnswerDTCollection) {
                    if (obj instanceof ArrayList<?> && !((ArrayList<Object>) obj).isEmpty()) {
                        for (NbsAnswerDto answerDT : (ArrayList<NbsAnswerDto>) obj) {
                            nbsAnswerRepository.deleteNbsAnswer(answerDT.getNbsAnswerUid());
                            nbsAnswerHistRepository.save(new NbsAnswerHist(answerDT));
                        }
                    } else if (obj instanceof NbsAnswerDto) {
                        NbsAnswerDto answerDT = (NbsAnswerDto) obj;
                        nbsAnswerRepository.deleteNbsAnswer(answerDT.getNbsAnswerUid());
                        nbsAnswerHistRepository.save(new NbsAnswerHist(answerDT));
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    protected void insertPageEntityHistoryDTCollection(Collection<NbsActEntityDto> nbsCaseEntityDTColl, ObservationDto oldrootDTInterface)
            throws DataProcessingException {
        try {

            if (nbsCaseEntityDTColl != null) {
                for (NbsActEntityDto nbsActEntityDto : nbsCaseEntityDTColl) {
                    nbsActEntityRepository.deleteNbsEntityAct(nbsActEntityDto.getNbsActEntityUid());

                    var data = new NbsActEntityHist(nbsActEntityDto);
                    data.setLastChgTime(oldrootDTInterface.getLastChgTime());
                    data.setLastChgUserId(oldrootDTInterface.getLastChgUserId());
                    data.setRecordStatusCd(oldrootDTInterface.getRecordStatusCd());
                    data.setRecordStatusTime(oldrootDTInterface.getRecordStatusTime());
                    nbsActEntityHistRepository.save(data);
                }
            }
        } catch (Exception ex) // NO SONAR
        {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }
}

