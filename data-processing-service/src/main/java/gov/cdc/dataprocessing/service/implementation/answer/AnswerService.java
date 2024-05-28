package gov.cdc.dataprocessing.service.implementation.answer;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.container.PageContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntityHist;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswerHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsActEntityHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsActEntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsAnswerHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsAnswerRepository;
import gov.cdc.dataprocessing.service.interfaces.answer.IAnswerService;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnswerService implements IAnswerService {
    private static final Logger logger = LoggerFactory.getLogger(AnswerService.class);

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
            Map<Object, NbsAnswerDto> nbsAnswerMap =new HashMap<>();
            Map<Object, Object> nbsRepeatingAnswerMap =new HashMap<>();
            if(answerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION)!=null){
                nbsAnswerMap=(HashMap<Object, NbsAnswerDto>)answerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION);
                logger.debug("AnswerRootDAOImpl nbsAnswerMap Size +"+nbsAnswerMap.size());
                logger.debug("AnswerRootDAOImpl nbsAnswerMap Values +"+nbsAnswerMap.toString());
            }
            if(answerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION)!=null){
                nbsRepeatingAnswerMap=(HashMap<Object, Object>)answerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION);
                logger.debug("AnswerRootDAOImpl nbsRepeatingAnswerMap Size +"+nbsRepeatingAnswerMap.size());
                logger.debug("AnswerRootDAOImpl nbsRepeatingAnswerMap Values +"+nbsRepeatingAnswerMap.toString());
            }
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

    public Map<Object, Object> getPageAnswerDTMaps(Long actUid) throws DataAccessException, DataProcessingException {
        ArrayList<NbsAnswerDto> pageAnswerDTCollection = new ArrayList<NbsAnswerDto>();
        Map<Object, Object> nbsReturnAnswerMap = new HashMap<Object, Object>();
        Map<Object, Object> nbsAnswerMap = new HashMap<Object, Object>();
        Map<Object, Object> nbsRepeatingAnswerMap = new HashMap<Object, Object>();


        var result = nbsAnswerRepository.getPageAnswerByActUid(actUid);
        if (result.isPresent()) {
            for(var item : result.get()){
                var elem = new NbsAnswerDto(item);
                pageAnswerDTCollection.add(elem);
            }

        }

        try
        {

            Iterator<NbsAnswerDto> it = pageAnswerDTCollection.iterator();
            Long nbsQuestionUid = 0L;
            Collection<NbsAnswerDto> coll = new ArrayList<NbsAnswerDto>();
            while (it.hasNext())
            {
                NbsAnswerDto pageAnsDT = (NbsAnswerDto) it.next();

                if (pageAnsDT.getAnswerGroupSeqNbr() != null && pageAnsDT.getAnswerGroupSeqNbr() > -1)
                {
                    if (nbsRepeatingAnswerMap.get(pageAnsDT.getNbsQuestionUid()) == null)
                    {
                        Collection collection = new ArrayList();
                        collection.add(pageAnsDT);
                        nbsRepeatingAnswerMap.put(pageAnsDT.getNbsQuestionUid(), collection);
                    }
                    else
                    {
                        Collection collection = (Collection) nbsRepeatingAnswerMap.get(pageAnsDT.getNbsQuestionUid());
                        collection.add(pageAnsDT);
                        nbsRepeatingAnswerMap.put(pageAnsDT.getNbsQuestionUid(), collection);
                    }
                }
                else if ((pageAnsDT.getNbsQuestionUid().compareTo(nbsQuestionUid) == 0)
                        && pageAnsDT.getSeqNbr() != null && pageAnsDT.getSeqNbr().intValue() > 0)
                {
                    coll.add(pageAnsDT);
                }
                else if (pageAnsDT.getSeqNbr() != null && pageAnsDT.getSeqNbr().intValue() > 0)
                {
                    if (coll.size() > 0)
                    {
                        nbsAnswerMap.put(nbsQuestionUid, coll);
                        coll = new ArrayList<NbsAnswerDto>();
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
                    coll = new ArrayList<NbsAnswerDto>();
                }
                nbsQuestionUid = pageAnsDT.getNbsQuestionUid();
                if (!it.hasNext() && coll.size() > 0)
                {
                    nbsAnswerMap.put(pageAnsDT.getNbsQuestionUid(), coll);
                }
            }
        }
        catch (Exception ex)
        {
            throw new DataProcessingException(ex.toString());
        }
        nbsReturnAnswerMap.put(NEDSSConstant.NON_REPEATING_QUESTION, nbsAnswerMap);
        nbsReturnAnswerMap.put(NEDSSConstant.REPEATING_QUESTION, nbsRepeatingAnswerMap);

        return nbsReturnAnswerMap;
    }

    @Transactional
    public void insertPageVO(PageContainer pageContainer, ObservationDto rootDTInterface) throws DataProcessingException{
        try {

            if(pageContainer !=null && pageContainer.getAnswerDTMap() !=null ) {
                Collection<Object> answerDTColl = new ArrayList<>(pageContainer.getAnswerDTMap().values());
                if(answerDTColl!=null && answerDTColl.size()>0) {
                    storeAnswerDTCollection(answerDTColl, rootDTInterface);
                }
                if(pageContainer !=null && pageContainer.getPageRepeatingAnswerDTMap() !=null ) {
                    Collection<Object> interviewRepeatingAnswerDTColl = pageContainer.getPageRepeatingAnswerDTMap().values();
                    if(interviewRepeatingAnswerDTColl!=null && interviewRepeatingAnswerDTColl.size()>0) {
                        storeAnswerDTCollection(interviewRepeatingAnswerDTColl, rootDTInterface);
                    }
                }
            }

            if (pageContainer == null) {
                pageContainer = new PageContainer();
                pageContainer.setActEntityDTCollection(new ArrayList<>());
            }
            storeActEntityDTCollection(pageContainer.getActEntityDTCollection(), rootDTInterface);

        } catch (Exception e) {
            throw new DataProcessingException(e.toString());
        }
    }


    @Transactional
    public void storePageAnswer(PageContainer pageContainer, ObservationDto observationDto) throws DataProcessingException{
        try {
            delete(observationDto);
            if(pageContainer !=null && pageContainer.getAnswerDTMap()!=null && pageContainer.getAnswerDTMap().values()!=null)
            {
                storeAnswerDTCollection(new ArrayList<>( pageContainer.getAnswerDTMap().values()), observationDto);
            }
            if(pageContainer !=null && pageContainer.getPageRepeatingAnswerDTMap()!=null && pageContainer.getPageRepeatingAnswerDTMap().values()!=null)
            {
                storeAnswerDTCollection(pageContainer.getPageRepeatingAnswerDTMap().values(), observationDto);
            }
            insertActEntityDTCollection(pageContainer.getActEntityDTCollection(), observationDto);
        } catch (Exception e) {
            throw new DataProcessingException("InterviewAnswerRootDAOImpl:store- answerDTColl could not be stored", e);
        }
    }

    private void storeActEntityDTCollection(Collection<NbsActEntityDto> pamDTCollection, ObservationDto rootDTInterface) throws  DataProcessingException{
        try{
            if(pamDTCollection.size()>0){
                Iterator<NbsActEntityDto> it  = pamDTCollection.iterator();
                while(it.hasNext()){
                    NbsActEntityDto pamCaseEntityDT = (NbsActEntityDto)it.next();
                    if(pamCaseEntityDT.isItDelete()){
                        nbsActEntityRepository.deleteNbsEntityAct(pamCaseEntityDT.getNbsActEntityUid());
                    }
                    else if(pamCaseEntityDT.isItDirty() || pamCaseEntityDT.isItNew())
                    {
                        var nbsActEntity = new NbsActEntity(pamCaseEntityDT);
                        nbsActEntity.setActUid(rootDTInterface.getObservationUid());
                        nbsActEntity.setLastChgUserId(2121L);
                        nbsActEntity.setRecordStatusCd("OPEN");
                        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
                        nbsActEntityRepository .save(new NbsActEntity(pamCaseEntityDT));
                    }
                }
            }
        }
        catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
    }

    public void storeActEntityDTCollectionWithPublicHealthCase(Collection<NbsActEntityDto> pamDTCollection, PublicHealthCaseDT rootDTInterface)
            throws  DataProcessingException{
        try{
            if(pamDTCollection.size()>0){
                Iterator<NbsActEntityDto> it  = pamDTCollection.iterator();
                while(it.hasNext()){
                    NbsActEntityDto pamCaseEntityDT = (NbsActEntityDto)it.next();
                    if(pamCaseEntityDT.isItDelete()){
                        nbsActEntityRepository.deleteNbsEntityAct(pamCaseEntityDT.getNbsActEntityUid());
                    }
                    else if(pamCaseEntityDT.isItDirty() || pamCaseEntityDT.isItNew())
                    {
                        var nbsActEntity = new NbsActEntity(pamCaseEntityDT);
                        nbsActEntity.setActUid(rootDTInterface.getPublicHealthCaseUid());
                        nbsActEntity.setLastChgUserId(2121L);
                        nbsActEntity.setRecordStatusCd("OPEN");
                        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
                        nbsActEntityRepository .save(nbsActEntity);
                    }
                }
            }
        }
        catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
    }



    private void insertActEntityDTCollection(Collection<NbsActEntityDto> actEntityDTCollection, ObservationDto observationDto) {
        if(actEntityDTCollection.size()>0){
            Iterator<NbsActEntityDto> it  = actEntityDTCollection.iterator();
            while(it.hasNext()){
                NbsActEntityDto pamCaseEntityDT = (NbsActEntityDto)it.next();
                nbsActEntityRepository.save(new NbsActEntity(pamCaseEntityDT));
            }
        }
    }

    private void storeAnswerDTCollection(Collection<Object> answerDTColl, ObservationDto interfaceDT) throws DataProcessingException {
        try {
            if (answerDTColl != null){
                Iterator<Object> it  = answerDTColl.iterator();
                while(it.hasNext()) {
                    NbsAnswerDto object = (NbsAnswerDto) it.next();
                    NbsAnswerDto answerDT = object;
                    if(answerDT.isItDirty() || answerDT.isItNew()){
                        nbsAnswerRepository.save(new NbsAnswer(answerDT));
                    } else if(answerDT.isItDelete()) {
                        nbsAnswerRepository.deleteNbsAnswer(answerDT.getNbsAnswerUid());
                    }
                }
            }
        } catch(Exception ex) {
            throw new DataProcessingException(ex.toString());
        }
    }

    private void delete(ObservationDto rootDTInterface) throws DataProcessingException{
        try {
            Collection<NbsAnswerDto> answerCollection = null;

            var result = nbsAnswerRepository.getPageAnswerByActUid(rootDTInterface.getObservationUid());
            if (result.isPresent()) {
                answerCollection = new ArrayList<>();
                for(var item : result.get()) {
                    answerCollection.add(new NbsAnswerDto(item));
                }
            }
            if(answerCollection!=null && answerCollection.size()>0) {
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

            if(actEntityCollection!=null && actEntityCollection.size()>0) {
                insertPageEntityHistoryDTCollection(actEntityCollection, rootDTInterface);
            }
        } catch (Exception e) {
            throw new DataProcessingException("InterviewAnswerRootDAOImpl:answerCollection- could not be returned", e);
        }

    }

    private void insertAnswerHistoryDTCollection(Collection<NbsAnswerDto> oldAnswerDTCollection) throws DataProcessingException {
        try {
            if (oldAnswerDTCollection != null) {
                Iterator<NbsAnswerDto> it = oldAnswerDTCollection.iterator();
                while (it.hasNext()) {
                    Object obj=it.next();
                    if(obj!=null && obj instanceof ArrayList<?> && ((ArrayList<Object>)obj).size()>0) {
                        Iterator<NbsAnswerDto> iter = ((ArrayList<NbsAnswerDto>)obj).iterator();
                        while(iter.hasNext()){
                            NbsAnswerDto answerDT = iter.next();
                            nbsAnswerRepository.deleteNbsAnswer(answerDT.getNbsAnswerUid());
                            nbsAnswerHistRepository.save(new NbsAnswerHist(answerDT));
                        }
                    }
                    else if(obj!=null && obj instanceof NbsAnswerDto){
                        NbsAnswerDto answerDT = (NbsAnswerDto)obj;
                        nbsAnswerRepository.deleteNbsAnswer(answerDT.getNbsAnswerUid());
                        nbsAnswerHistRepository.save(new NbsAnswerHist(answerDT));
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
        }
    }

    private void insertPageEntityHistoryDTCollection(Collection<NbsActEntityDto> nbsCaseEntityDTColl, ObservationDto oldrootDTInterface)
            throws DataProcessingException {
        try {

            if (nbsCaseEntityDTColl != null) {
                Iterator<NbsActEntityDto> it = nbsCaseEntityDTColl.iterator();
                while (it.hasNext()) {
                    NbsActEntityDto nbsActEntityDto = (NbsActEntityDto) it.next();
                    nbsActEntityRepository.deleteNbsEntityAct(nbsActEntityDto.getNbsActEntityUid());

                    var data = new NbsActEntityHist(nbsActEntityDto);
                    data.setLastChgTime(oldrootDTInterface.getLastChgTime());
                    data.setLastChgUserId(oldrootDTInterface.getLastChgUserId());
                    data.setRecordStatusCd(oldrootDTInterface.getRecordStatusCd());
                    data.setRecordStatusTime(oldrootDTInterface.getRecordStatusTime());
                    nbsActEntityHistRepository.save(data);
                }
            }
        } catch (Exception ex) {
            logger.error("NbsActEntityHistoryDAO.insertPamEntityHistoryDTCollection inser method failed"+ex.getMessage(), ex);
            throw new DataProcessingException(ex.toString());
        }
    }
}

