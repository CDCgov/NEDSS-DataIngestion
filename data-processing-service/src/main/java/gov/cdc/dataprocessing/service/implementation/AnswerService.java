package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NbsActEntityDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NbsAnswerDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntityHist;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswerHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsActEntityHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsActEntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsAnswerHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsAnswerRepository;
import gov.cdc.dataprocessing.service.interfaces.IAnswerService;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
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

    public PageVO getNbsAnswerAndAssociation(Long uid) throws DataProcessingException {
        PageVO pageVO = new PageVO();
        try {

            Map<Object,Object> answerDTReturnMap = getPageAnswerDTMaps(uid);
            Map<Object, NbsAnswerDT> nbsAnswerMap =new HashMap<>();
            Map<Object, NbsAnswerDT> nbsRepeatingAnswerMap =new HashMap<>();
            if(answerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION)!=null){
                nbsAnswerMap=(HashMap<Object, NbsAnswerDT>)answerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION);
                logger.debug("AnswerRootDAOImpl nbsAnswerMap Size +"+nbsAnswerMap.size());
                logger.debug("AnswerRootDAOImpl nbsAnswerMap Values +"+nbsAnswerMap.toString());
            }
            if(answerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION)!=null){
                nbsRepeatingAnswerMap=(HashMap<Object, NbsAnswerDT>)answerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION);
                logger.debug("AnswerRootDAOImpl nbsRepeatingAnswerMap Size +"+nbsRepeatingAnswerMap.size());
                logger.debug("AnswerRootDAOImpl nbsRepeatingAnswerMap Values +"+nbsRepeatingAnswerMap.toString());
            }
            pageVO.setAnswerDTMap(nbsAnswerMap);
            pageVO.setPageRepeatingAnswerDTMap(nbsRepeatingAnswerMap);

            var result = nbsActEntityRepository.getNbsActEntitiesByActUid(uid);
            Collection<NbsActEntityDT> pageCaseEntityDTCollection= new ArrayList<>();
            if (result.isPresent()) {
                for(var item : result.get()) {
                    var elem = new NbsActEntityDT(item);
                    pageCaseEntityDTCollection.add(elem);
                }
            }
            pageVO.setActEntityDTCollection(pageCaseEntityDTCollection);

        } catch (Exception e) {
            throw new DataProcessingException("InterviewAnswerRootDAOImpl:answerCollection- could not be returned", e);
        }
        return pageVO;

    }

    public Map<Object, Object> getPageAnswerDTMaps(Long actUid) throws DataAccessException, DataProcessingException {
        ArrayList<NbsAnswerDT> pageAnswerDTCollection = new ArrayList<NbsAnswerDT>();
        Map<Object, Object> nbsReturnAnswerMap = new HashMap<Object, Object>();
        Map<Object, Object> nbsAnswerMap = new HashMap<Object, Object>();
        Map<Object, Object> nbsRepeatingAnswerMap = new HashMap<Object, Object>();


        var result = nbsAnswerRepository.getPageAnswerByActUid(actUid);
        if (result.isPresent()) {
            for(var item : result.get()){
                var elem = new NbsAnswerDT(item);
                pageAnswerDTCollection.add(elem);
            }

        }

        try
        {

            Iterator<NbsAnswerDT> it = pageAnswerDTCollection.iterator();
            Long nbsQuestionUid = 0L;
            Collection<NbsAnswerDT> coll = new ArrayList<NbsAnswerDT>();
            while (it.hasNext())
            {
                NbsAnswerDT pageAnsDT = (NbsAnswerDT) it.next();

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
                        coll = new ArrayList<NbsAnswerDT>();
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
                    coll = new ArrayList<NbsAnswerDT>();
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
    public void insertPageVO(PageVO pageVO,ObservationDT rootDTInterface) throws DataProcessingException{
        try {

            if(pageVO!=null && pageVO.getAnswerDTMap() !=null ) {
                Collection<NbsAnswerDT> answerDTColl =pageVO.getAnswerDTMap().values();
                if(answerDTColl!=null && answerDTColl.size()>0) {
                    storeAnswerDTCollection(answerDTColl, rootDTInterface);
                }
                if(pageVO!=null && pageVO.getPageRepeatingAnswerDTMap() !=null ) {
                    Collection<NbsAnswerDT> interviewRepeatingAnswerDTColl =pageVO.getPageRepeatingAnswerDTMap().values();
                    if(interviewRepeatingAnswerDTColl!=null && interviewRepeatingAnswerDTColl.size()>0) {
                        storeAnswerDTCollection(interviewRepeatingAnswerDTColl, rootDTInterface);
                    }
                }
            }
            storeActEntityDTCollection(pageVO.getActEntityDTCollection(), rootDTInterface);

        } catch (Exception e) {
            throw new DataProcessingException(e.toString());
        }
    }


    @Transactional
    public void storePageAnswer(PageVO pageVO, ObservationDT observationDT) throws DataProcessingException{
        try {
            delete(observationDT);
            if(pageVO!=null && pageVO.getAnswerDTMap()!=null && pageVO.getAnswerDTMap().values()!=null)
            {
                storeAnswerDTCollection(pageVO.getAnswerDTMap().values(), observationDT);
            }
            if(pageVO!=null && pageVO.getPageRepeatingAnswerDTMap()!=null && pageVO.getPageRepeatingAnswerDTMap().values()!=null)
            {
                storeAnswerDTCollection(pageVO.getPageRepeatingAnswerDTMap().values(), observationDT);
            }
            insertActEntityDTCollection(pageVO.getActEntityDTCollection(), observationDT);
        } catch (Exception e) {
            throw new DataProcessingException("InterviewAnswerRootDAOImpl:store- answerDTColl could not be stored", e);
        }
    }

    private void storeActEntityDTCollection(Collection<NbsActEntityDT> pamDTCollection, ObservationDT rootDTInterface) throws  DataProcessingException{
        try{
            if(pamDTCollection.size()>0){
                Iterator<NbsActEntityDT> it  = pamDTCollection.iterator();
                while(it.hasNext()){
                    NbsActEntityDT pamCaseEntityDT = (NbsActEntityDT)it.next();
                    if(pamCaseEntityDT.isItDelete()){
                        nbsActEntityRepository.deleteNbsEntityAct(pamCaseEntityDT.getNbsActEntityUid());
                    }
                    else if(pamCaseEntityDT.isItDirty() || pamCaseEntityDT.isItNew())
                    {
                        nbsActEntityRepository .save(new NbsActEntity(pamCaseEntityDT));
                    }
                }
            }
        }
        catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
    }


    private void insertActEntityDTCollection(Collection<NbsActEntityDT> actEntityDTCollection, ObservationDT observationDT) {
        if(actEntityDTCollection.size()>0){
            Iterator<NbsActEntityDT> it  = actEntityDTCollection.iterator();
            while(it.hasNext()){
                NbsActEntityDT pamCaseEntityDT = (NbsActEntityDT)it.next();
                nbsActEntityRepository.save(new NbsActEntity(pamCaseEntityDT));
            }
        }
    }

    private void storeAnswerDTCollection(Collection<NbsAnswerDT> answerDTColl, ObservationDT interfaceDT) throws DataProcessingException {
        try {
            if (answerDTColl != null){
                Iterator<NbsAnswerDT> it  = answerDTColl.iterator();
                while(it.hasNext()) {
                    NbsAnswerDT object = it.next();
                    NbsAnswerDT answerDT = object;
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

    private void delete(ObservationDT rootDTInterface) throws DataProcessingException{
        try {
            Collection<NbsAnswerDT> answerCollection = null;

            var result = nbsAnswerRepository.getPageAnswerByActUid(rootDTInterface.getObservationUid());
            if (result.isPresent()) {
                answerCollection = new ArrayList<>();
                for(var item : result.get()) {
                    answerCollection.add(new NbsAnswerDT(item));
                }
            }
            if(answerCollection!=null && answerCollection.size()>0) {
                insertAnswerHistoryDTCollection(answerCollection);
            }

            var actEntityResult = nbsActEntityRepository.getNbsActEntitiesByActUid(rootDTInterface.getObservationUid());
            Collection<NbsActEntityDT> actEntityCollection = null;
            if (actEntityResult.isPresent()) {
                actEntityCollection = new ArrayList<>();
                for(var item: actEntityResult.get()) {
                    actEntityCollection.add(new NbsActEntityDT(item));
                }
            }

            if(actEntityCollection!=null && actEntityCollection.size()>0) {
                insertPageEntityHistoryDTCollection(actEntityCollection, rootDTInterface);
            }
        } catch (Exception e) {
            throw new DataProcessingException("InterviewAnswerRootDAOImpl:answerCollection- could not be returned", e);
        }

    }

    private void insertAnswerHistoryDTCollection(Collection<NbsAnswerDT> oldAnswerDTCollection) throws DataProcessingException {
        try {
            if (oldAnswerDTCollection != null) {
                Iterator<NbsAnswerDT> it = oldAnswerDTCollection.iterator();
                while (it.hasNext()) {
                    Object obj=it.next();
                    if(obj!=null && obj instanceof ArrayList<?> && ((ArrayList<Object>)obj).size()>0) {
                        Iterator<NbsAnswerDT> iter = ((ArrayList<NbsAnswerDT>)obj).iterator();
                        while(iter.hasNext()){
                            NbsAnswerDT answerDT = iter.next();
                            nbsAnswerRepository.deleteNbsAnswer(answerDT.getNbsAnswerUid());
                            nbsAnswerHistRepository.save(new NbsAnswerHist(answerDT));
                        }
                    }
                    else if(obj!=null && obj instanceof NbsAnswerDT){
                        NbsAnswerDT answerDT = (NbsAnswerDT)obj;
                        nbsAnswerRepository.deleteNbsAnswer(answerDT.getNbsAnswerUid());
                        nbsAnswerHistRepository.save(new NbsAnswerHist(answerDT));
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
        }
    }

    private void insertPageEntityHistoryDTCollection(Collection<NbsActEntityDT> nbsCaseEntityDTColl,  ObservationDT oldrootDTInterface)
            throws DataProcessingException {
        try {

            if (nbsCaseEntityDTColl != null) {
                Iterator<NbsActEntityDT> it = nbsCaseEntityDTColl.iterator();
                while (it.hasNext()) {
                    NbsActEntityDT nbsActEntityDT = (NbsActEntityDT) it.next();
                    nbsActEntityRepository.deleteNbsEntityAct(nbsActEntityDT.getNbsActEntityUid());

                    var data = new NbsActEntityHist(nbsActEntityDT);
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

