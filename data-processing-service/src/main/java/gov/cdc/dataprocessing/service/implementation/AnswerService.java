package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NbsActEntityDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NbsAnswerDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageVO;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsActEntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsAnswerRepository;
import gov.cdc.dataprocessing.service.interfaces.IAnswerService;
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

    public AnswerService(NbsAnswerRepository nbsAnswerRepository,
                         NbsActEntityRepository nbsActEntityRepository) {
        this.nbsAnswerRepository = nbsAnswerRepository;
        this.nbsActEntityRepository = nbsActEntityRepository;
    }

    public PageVO getNbsAnswerAndAssociation(Long uid) throws DataProcessingException {
        PageVO pageVO = new PageVO();
        try {

            Map<Object,Object> answerDTReturnMap = getPageAnswerDTMaps(uid);
            Map<Object, Object> nbsAnswerMap =new HashMap<Object, Object>();
            Map<Object, Object> nbsRepeatingAnswerMap =new HashMap<Object, Object>();
            if(answerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION)!=null){
                nbsAnswerMap=(HashMap<Object, Object>)answerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION);
                logger.debug("AnswerRootDAOImpl nbsAnswerMap Size +"+nbsAnswerMap.size());
                logger.debug("AnswerRootDAOImpl nbsAnswerMap Values +"+nbsAnswerMap.toString());
            }
            if(answerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION)!=null){
                nbsRepeatingAnswerMap=(HashMap<Object, Object>)answerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION);
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

}
