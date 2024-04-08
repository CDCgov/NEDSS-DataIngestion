package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lookup.LookupMappingDto;
import gov.cdc.dataprocessing.model.dto.lookup.PrePopMappingDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.LookupMappingRepository;
import gov.cdc.dataprocessing.service.interfaces.ILookupService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

@Service
public class LookupService implements ILookupService {

    private final LookupMappingRepository lookupMappingRepository;

    public LookupService(LookupMappingRepository lookupMappingRepository) {
        this.lookupMappingRepository = lookupMappingRepository;
    }

    // TODO: LOOK INTO THIS
    public TreeMap<Object, Object> getToPrePopFormMapping(String formCd) throws DataProcessingException {
        TreeMap<Object, Object> returnMap = null;
//        try {
//            returnMap = (TreeMap<Object, Object>) OdseCache.toPrePopFormMapping.get(formCd);
//            if (returnMap == null) {
//                    Collection<Object> qColl = getQuestionMapEJBRef().getPrePopMapping();
//                    createPrePopToMap(qColl);
//                }
//                returnMap = (TreeMap<Object, Object>) toPrePopFormMapping.get(formCd);
//            }
//            return returnMap;
//        } catch (Exception ex) {
//            throw new DataProcessingException("The to prepop caching failed for form Cd: " + formCd);
//        }

        return returnMap;
    }

    public TreeMap<Object,Object>  getQuestionMap() {
        TreeMap<Object,Object> questionMap = null;

        //TODO: Look into this
//        if (OdseCache.map != null && OdseCache.size() > 0) {
//            return (TreeMap<Object,Object>) OdseCache.map;
//
//        }
//
//        try {
//            if (getQuestionMapEJBRef() != null){
//                Collection<Object>  qColl = 	getQuestionMapEJBRef().getPamQuestions();
//                questionMap = createQuestionMap(qColl);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        map.putAll(questionMap);
        return questionMap;
    }


    //TODO: Look into this
    public TreeMap<Object,Object>  getDMBQuestionMapAfterPublish() {
        TreeMap<Object,Object> dmbQuestionMap = null;

//        try {
//            if (getQuestionMapEJBRef() != null){
//                Collection<Object>  qColl = 	getQuestionMapEJBRef().getDMBQuestions();
//                dmbQuestionMap = createDMBQuestionMap(qColl);
//            }
//        } catch (Exception e) {
//            logger.error("Exception in getDMBQuestionMapAfterPublish() " + e.getMessage());
//            e.printStackTrace();
//        }
//        if(dmbQuestionMap != null)
//            dmbMap.putAll(dmbQuestionMap);
        return dmbQuestionMap;
    }


    public void fillPrePopMap() {

        if (OdseCache.fromPrePopFormMapping == null || OdseCache.fromPrePopFormMapping.size() == 0) {
            try {
                    Collection<LookupMappingDto> qColl = retrievePrePopMapping();
                    createPrePopFromMap(qColl);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (OdseCache.toPrePopFormMapping == null || OdseCache.toPrePopFormMapping.size() == 0) {
            try {
                    Collection<LookupMappingDto> qColl = retrievePrePopMapping();
                    createPrePopToMap(qColl);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }


    private Collection<LookupMappingDto> retrievePrePopMapping () {
        var res = lookupMappingRepository.getLookupMappings();
        return res.orElseGet(ArrayList::new);
    }

    private static void createPrePopFromMap(Collection<LookupMappingDto> coll) throws Exception {
        int count = 0;
        int loopcount = 0;
        int sizecount = 0;
        String currentFormCode = "";
        String previousFormCode = "";

        TreeMap<Object, Object>[] map = new TreeMap[coll.size()];
        PrePopMappingDto qMetadata = null;
        try {
            if (coll != null && coll.size() > 0) {
                Iterator<LookupMappingDto> ite = coll.iterator();
                while (ite.hasNext()) {
                    sizecount++;
                    qMetadata = new PrePopMappingDto(ite.next());

                    if (qMetadata.getFromFormCd() != null) {

                        if (loopcount == 0) {
                            previousFormCode = qMetadata.getFromFormCd();
                            String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                    : qMetadata.getFromQuestionIdentifier();
                            String fromAns = qMetadata.getFromAnswerCode();
                            map[count] = new TreeMap<Object, Object>();
                            if (!fromQuestionId.equals("")) {
                                if (fromAns != null) {
                                    fromQuestionId = fromQuestionId + "$" + fromAns;
                                    map[count].put(fromQuestionId, qMetadata);
                                }
                                PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                qMetadata1.setFromAnswerCode(null);
                                map[count].put(qMetadata.getFromQuestionIdentifier(), qMetadata1);
                                loopcount++;
                            }

                        } else {
                            currentFormCode = qMetadata.getFromFormCd();
                            if (currentFormCode.equals(previousFormCode)) {
                                String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                        : qMetadata.getFromQuestionIdentifier();
                                String fromAns = qMetadata.getFromAnswerCode();
                                if (!fromQuestionId.equals("")) {
                                    if (fromAns != null) {
                                        fromQuestionId = fromQuestionId + "$" + fromAns;
                                        map[count].put(fromQuestionId, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                    qMetadata1.setFromAnswerCode(null);
                                    map[count].put(qMetadata.getFromQuestionIdentifier(), qMetadata1);
                                }

                            } else {
                                OdseCache.fromPrePopFormMapping.put(previousFormCode, map[count]);
                                count = count + 1;
                                String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                        : qMetadata.getFromQuestionIdentifier();
                                String fromAns = qMetadata.getFromAnswerCode();
                                if (!fromQuestionId.equals("")) {
                                    map[count] = new TreeMap<Object, Object>();
                                    if (fromAns != null) {
                                        fromQuestionId = fromQuestionId + "$" + fromAns;
                                        map[count].put(fromQuestionId, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                    qMetadata1.setFromAnswerCode(null);
                                    map[count].put(qMetadata.getFromQuestionIdentifier(), qMetadata1);
                                }

                            }
                            previousFormCode = currentFormCode;
                            loopcount++;
                        }

                    }
                    if (sizecount == coll.size()) {
                        OdseCache.fromPrePopFormMapping.put(qMetadata.getFromFormCd(), map[count]);
                    }

                }

            }
        } catch (Exception ex) {
            throw new DataProcessingException("The from prepop caching failed due to question label :"
                    + qMetadata.getFromQuestionIdentifier() + " in form cd :" + qMetadata.getFromFormCd());
        }

    }

    private static void createPrePopToMap(Collection<LookupMappingDto> coll) throws Exception {
        int count = 0;
        int loopcount = 0;
        int sizecount = 0;
        String currentFormCode = "";
        String previousFormCode = "";

        TreeMap<Object, Object>[] map = new TreeMap[coll.size()];
        PrePopMappingDto qMetadata = null;
        try {
            if (coll != null && coll.size() > 0) {
                Iterator<LookupMappingDto> ite = coll.iterator();
                while (ite.hasNext()) {
                    sizecount++;
                    qMetadata = new PrePopMappingDto(ite.next());

                    if (qMetadata.getToFormCd() != null) {

                        if (loopcount == 0) {
                            previousFormCode = qMetadata.getToFormCd();
                            String toQuestionId = qMetadata.getToQuestionIdentifier() == null ? ""
                                    : qMetadata.getToQuestionIdentifier();
                            String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                    : qMetadata.getFromQuestionIdentifier();
                            toQuestionId = toQuestionId+'^'+fromQuestionId;
                            String fromAns = qMetadata.getFromAnswerCode();
                            String toQuestionIdWithAns = null;
                            map[count] = new TreeMap<Object, Object>();
                            if (!toQuestionId.equals("")) {
                                if (fromAns != null) {
                                    toQuestionIdWithAns = toQuestionId + "$" + fromAns;
                                    map[count].put(toQuestionIdWithAns, qMetadata);
                                }
                                PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                qMetadata1.setToAnswerCode(null);
                                map[count].put(toQuestionId, qMetadata1);
                                loopcount++;
                            }

                        } else {
                            currentFormCode = qMetadata.getToFormCd();
                            if (currentFormCode.equals(previousFormCode)) {
                                String toQuestionId = qMetadata.getToQuestionIdentifier() == null ? ""
                                        : qMetadata.getToQuestionIdentifier();
                                String fromAns = qMetadata.getFromAnswerCode();
                                String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                        : qMetadata.getFromQuestionIdentifier();
                                String toQuestionIdWithAns = null;
                                toQuestionId = toQuestionId+'^'+fromQuestionId;
                                if (!toQuestionId.equals("")) {
                                    if (fromAns != null) {
                                        toQuestionIdWithAns = toQuestionId + "$" + fromAns;
                                        map[count].put(toQuestionIdWithAns, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                    qMetadata1.setToAnswerCode(null);
                                    map[count].put(toQuestionId, qMetadata1);
                                }

                            } else {
                                OdseCache.toPrePopFormMapping.put(previousFormCode, map[count]);
                                count = count + 1;
                                String toQuestionId = qMetadata.getToQuestionIdentifier() == null ? ""
                                        : qMetadata.getToQuestionIdentifier();
                                String fromAns = qMetadata.getFromAnswerCode();
                                String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                        : qMetadata.getFromQuestionIdentifier();
                                toQuestionId = toQuestionId+'^'+fromQuestionId;
                                String toQuestionIdWithAns = null;
                                map[count] = new TreeMap<Object, Object>();
                                if (!toQuestionId.equals("")) {
                                    if (fromAns != null) {
                                        toQuestionIdWithAns = toQuestionId + "$" + fromAns;
                                        map[count].put(toQuestionIdWithAns, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                    qMetadata1.setToAnswerCode(null);
                                    map[count].put(toQuestionId, qMetadata1);
                                }

                            }
                            previousFormCode = currentFormCode;
                            loopcount++;
                        }

                    }
                    if (sizecount == coll.size()) {
                        OdseCache.toPrePopFormMapping.put(qMetadata.getToFormCd(), map[count]);
                    }

                }

            }
        } catch (Exception ex) {
            throw new DataProcessingException("The to prepop caching failed due to question Identifier :"
                    + qMetadata.getToQuestionIdentifier() + " in form cd :" + qMetadata.getToFormCd());
        }

    }


//    public QuestionMap getQuestionMapEJBRef() throws Exception {
//        if (qMap == null) {
//            NedssUtils nu = new NedssUtils();
//            Object objref = nu.lookupBean(JNDINames.QUESTION_MAP_EJB);
//            if (objref != null) {
//                QuestionMapHome home = (QuestionMapHome) PortableRemoteObject
//                        .narrow(objref, QuestionMapHome.class);
//                qMap = home.create();
//            }
//        }
//        return qMap;
//    }

}
