package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.constant.NBSConstantUtil;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.lookup.LookupMappingDto;
import gov.cdc.dataprocessing.model.dto.lookup.PrePopMappingDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.NbsUiMetaData;
import gov.cdc.dataprocessing.repository.nbs.odse.model.WAQuestion;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.LookupMappingRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.NbsUiMetaDataRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.WAQuestionRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.service.interfaces.ILookupService;
import gov.cdc.dataprocessing.service.interfaces.other.ICatchingValueService;
import gov.cdc.dataprocessing.service.model.MetaAndWaCommonAttribute;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LookupService implements ILookupService {

    private final LookupMappingRepository lookupMappingRepository;
    private final NbsUiMetaDataRepository nbsUiMetaDataRepository;
    private final WAQuestionRepository waQuestionRepository;
    private final ICatchingValueService catchingValueService;

    public LookupService(LookupMappingRepository lookupMappingRepository,
                         NbsUiMetaDataRepository nbsUiMetaDataRepository,
                         WAQuestionRepository waQuestionRepository,
                         ICatchingValueService catchingValueService) {
        this.lookupMappingRepository = lookupMappingRepository;
        this.nbsUiMetaDataRepository = nbsUiMetaDataRepository;
        this.waQuestionRepository = waQuestionRepository;
        this.catchingValueService = catchingValueService;
    }

    public TreeMap<Object, Object> getToPrePopFormMapping(String formCd) throws DataProcessingException {
        TreeMap<Object, Object> returnMap = null;
        try {
            returnMap = (TreeMap<Object, Object>) OdseCache.toPrePopFormMapping.get(formCd);
            if (returnMap == null) {
                    Collection<LookupMappingDto> qColl = getPrePopMapping();
                    createPrePopToMap(qColl);
                }
                returnMap = (TreeMap<Object, Object>) OdseCache.toPrePopFormMapping.get(formCd);

            return returnMap;
        } catch (Exception ex) {
            throw new DataProcessingException("The to prepop caching failed for form Cd: " + formCd);
        }

    }

    public TreeMap<Object,Object>  getQuestionMap() {
        TreeMap<Object,Object> questionMap = null;

        if (OdseCache.map != null && OdseCache.map.size() > 0) {
            return (TreeMap<Object,Object>) OdseCache.map;

        }

        try {
                Collection<Object>  qColl = getPamQuestions();
                questionMap = createQuestionMap(qColl);

        } catch (Exception e) {
            e.printStackTrace();
        }

        OdseCache.map.putAll(questionMap);
        return questionMap;
    }


    // PG_Generic_V2_Investigation
    public TreeMap<Object,Object>  getDMBQuestionMapAfterPublish() {
        TreeMap<Object,Object> dmbQuestionMap = null;

        try {
            //TODO: MUST CACHING THESE TWO. these are queries that pull the entire table into memory
            var res =  nbsUiMetaDataRepository.findDmbQuestionMetaData();
            var res2 = waQuestionRepository.findGenericQuestionMetaData();
            Collection<MetaAndWaCommonAttribute>  metaQuestion = new ArrayList<>();
            if (res.isPresent()) {
                for(var item : res.get()) {
                    var commonAttribute = new MetaAndWaCommonAttribute(item);
                    if (commonAttribute.getQuestionIdentifier().equalsIgnoreCase("INV161")) {
                        System.out.println("TEST");
                    }
                    metaQuestion.add(commonAttribute);
                }
                if (res2.isPresent()) {
                    for(var item : res2.get()) {
                        var commonAttribute = new MetaAndWaCommonAttribute(item);
                        if (commonAttribute.getQuestionIdentifier().equalsIgnoreCase("INV161")) {
                            System.out.println("TEST");
                        }
                        metaQuestion.add(commonAttribute);
                    }
                }
            }


            dmbQuestionMap = createDMBQuestionMap(metaQuestion);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dmbQuestionMap != null)
        {
            OdseCache.dmbMap.putAll(dmbQuestionMap);
        }
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
        List<LookupMappingDto> lst = new ArrayList<>();
        if (res.isPresent()) {
            for(var item : res.get()) {
                LookupMappingDto mappingDto = new LookupMappingDto(item);
                lst.add(mappingDto);
            }
        }
        return lst;
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
                        if (qMetadata.getToFormCd() != null) {
                            OdseCache.toPrePopFormMapping.put(qMetadata.getToFormCd(), map[count]);
                        }
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


    private TreeMap<Object,Object> createDMBQuestionMap(Collection<MetaAndWaCommonAttribute>  coll) throws Exception{
        TreeMap<Object, Object> qCodeMap = new TreeMap<Object, Object>();
        int count =0;
        int loopcount=0;
        int sizecount=0;
        String currentFormCode="";
        String previousFormCode="";

        //For Demo Purpose CHOLERA Metadata
        TreeMap<Object,Object> qInvFormChlrMap = new TreeMap<Object,Object>();
        TreeMap<Object, Object>[] map ;
        map = new TreeMap[coll.size()];
        NbsQuestionMetadata qMetadata = null;
        try{
            if (coll.size() > 0) {
                for (MetaAndWaCommonAttribute metaAndWaCommonAttribute : coll) {
                    sizecount++;

                    if(metaAndWaCommonAttribute.getQuestionIdentifier().equalsIgnoreCase("INV161")) {
                        System.out.println("TEST");
                    }
                    if (metaAndWaCommonAttribute.getDataLocation() != null) {
                        System.out.println("TEST");
                    }
                    qMetadata = new NbsQuestionMetadata(metaAndWaCommonAttribute);
                    String dataType = qMetadata.getDataType();
                    List<CodeValueGeneral> aList = new ArrayList<>();
                    if (dataType != null && dataType.equals(NEDSSConstant.NBS_QUESTION_DATATYPE_CODED_VALUE)) {
                        aList = catchingValueService.getGeneralCodedValue(qMetadata.getCodeSetNm());
                        qMetadata.setAList(aList);
                    }

                    if (qMetadata.getInvestigationFormCd() != null) {

                        if (loopcount == 0)
                        {
                            previousFormCode = qMetadata.getInvestigationFormCd();
                            String questionId = qMetadata.getQuestionIdentifier() == null ? "" : qMetadata.getQuestionIdentifier();
                            String ldfPageId = qMetadata.getLdfPageId() == null ? "" : qMetadata.getLdfPageId();
                            String uiMetadataUid = qMetadata.getNbsUiMetadataUid() == null ? "" : qMetadata.getNbsUiMetadataUid().toString();
                            if (!questionId.equals("")) {
                                map[count] = new TreeMap<Object, Object>();
                                map[count].put(questionId, qMetadata);
                                loopcount++;
                            }

                        }
                        else
                        {
                            currentFormCode = qMetadata.getInvestigationFormCd();
                            if (currentFormCode.equals(previousFormCode))
                            {
                                String questionId = qMetadata.getQuestionIdentifier() == null ? "" : qMetadata.getQuestionIdentifier();
                                String ldfPageId = qMetadata.getLdfPageId() == null ? "" : qMetadata.getLdfPageId();
                                String uiMetadataUid = qMetadata.getNbsUiMetadataUid() == null ? "" : qMetadata.getNbsUiMetadataUid().toString();
                                if (!questionId.equals(""))
                                {
                                    map[count].put(questionId, qMetadata);
                                }
                            }
                            else
                            {
                                if(previousFormCode.equalsIgnoreCase("PG_Generic_V2_Investigation")) {
                                    System.out.println("TEST");
                                }
                                qCodeMap.put(previousFormCode, map[count]);
                                count = count + 1;
                                String questionId = qMetadata.getQuestionIdentifier() == null ? "" : qMetadata.getQuestionIdentifier();
                                String ldfPageId = qMetadata.getLdfPageId() == null ? "" : qMetadata.getLdfPageId();
                                String uiMetadataUid = qMetadata.getNbsUiMetadataUid() == null ? "" : qMetadata.getNbsUiMetadataUid().toString();
                                if (!questionId.equals(""))
                                {
                                    map[count] = new TreeMap<Object, Object>();
                                    map[count].put(questionId, qMetadata);
                                }

                            }
                            previousFormCode = currentFormCode;
                            loopcount++;
                        }

                    }
                    if (sizecount == coll.size()) {
                        if(qCodeMap.containsKey("PG_Generic_V2_Investigation")) {
                            System.out.println("TEST");
                        }
                        qCodeMap.put(qMetadata.getInvestigationFormCd(), map[count]);
                    }

                }

            }
        }
        catch(Exception ex){
            throw new DataProcessingException("The caching failed due to question label :" + qMetadata.getQuestionLabel()+" in form cd :"+ qMetadata.getInvestigationFormCd());
        }

        return qCodeMap;
    }


    private Collection<LookupMappingDto>  getPrePopMapping() throws DataProcessingException {

        try {
            Collection<LookupMappingDto>  prepopMapping = retrievePrePopMapping();
            return prepopMapping;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    private Collection<Object>  getPamQuestions() throws DataProcessingException {

        try {
            var res = nbsUiMetaDataRepository.findPamQuestionMetaData();
            Collection<Object>  questions = new ArrayList<>();
            if (res.isPresent()) {
                questions  = nbsUiMetaDataRepository.findPamQuestionMetaData().get();
            }
            return questions;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private TreeMap<Object,Object> createQuestionMap(Collection<Object>  coll) {
        TreeMap<Object,Object> qCodeMap = new TreeMap<Object,Object>();
        TreeMap<Object,Object> qInvFormRVCTMap = new TreeMap<Object,Object>();
        if (coll != null && coll.size() > 0) {
            Iterator<Object>  ite = coll.iterator();
            while (ite.hasNext()) {
                NbsQuestionMetadata qMetadata = (NbsQuestionMetadata) ite
                        .next();
                if (qMetadata.getInvestigationFormCd().equals(
                        NBSConstantUtil.INV_FORM_RVCT))
                    qInvFormRVCTMap.put(qMetadata.getQuestionIdentifier(),
                            qMetadata);
            }
            qCodeMap.put(NBSConstantUtil.INV_FORM_RVCT, qInvFormRVCTMap);
        }
        return qCodeMap;
    }


}

