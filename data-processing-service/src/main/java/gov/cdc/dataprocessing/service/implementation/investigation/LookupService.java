package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.constant.NBSConstantUtil;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lookup.LookupMappingDto;
import gov.cdc.dataprocessing.model.dto.lookup.PrePopMappingDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.WAQuestionRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.lookup.LookupMappingRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsUiMetaDataRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.service.model.lookup_data.MetaAndWaCommonAttribute;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public class LookupService implements ILookupService {

    private final LookupMappingRepository lookupMappingRepository;
    private final NbsUiMetaDataRepository nbsUiMetaDataRepository;
    private final WAQuestionRepository waQuestionRepository;
    private final ICatchingValueService catchingValueService;

    private static final String EXCEPTION_APPENDING_MSG = " in form cd :";

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
        TreeMap<Object, Object> returnMap;
        returnMap = (TreeMap<Object, Object>) OdseCache.toPrePopFormMapping.get(formCd);
        if (returnMap == null) {
                Collection<LookupMappingDto> qColl = getPrePopMapping();
                createPrePopToMap(qColl);
        }
        returnMap = (TreeMap<Object, Object>) OdseCache.toPrePopFormMapping.get(formCd);

        return returnMap;
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

        if (questionMap != null) {
            OdseCache.map.putAll(questionMap);
        }
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
                    metaQuestion.add(commonAttribute);
                }
                if (res2.isPresent()) {
                    for(var item : res2.get()) {
                        var commonAttribute = new MetaAndWaCommonAttribute(item);
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

    @SuppressWarnings("java:S3776")
    private static void createPrePopFromMap(Collection<LookupMappingDto> coll) throws Exception {
        int count = 0;
        int loopcount = 0;
        int sizecount = 0;
        String currentFormCode;
        String previousFormCode = "";

        TreeMap<Object, Object>[] map = new TreeMap[coll.size()];
        PrePopMappingDto qMetadata = null;
        try {
            if (coll.size() > 0) {
                for (LookupMappingDto lookupMappingDto : coll) {
                    sizecount++;
                    qMetadata = new PrePopMappingDto(lookupMappingDto);

                    if (qMetadata.getFromFormCd() != null) {

                        if (loopcount == 0) {
                            previousFormCode = qMetadata.getFromFormCd();
                            String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                    : qMetadata.getFromQuestionIdentifier();
                            String fromAns = qMetadata.getFromAnswerCode();
                            map[count] = new TreeMap<>();
                            if (!fromQuestionId.equals("")) {
                                if (fromAns != null) {
                                    fromQuestionId = fromQuestionId + "$" + fromAns;
                                    map[count].put(fromQuestionId, qMetadata);
                                }
                                PrePopMappingDto qMetadata1 = (PrePopMappingDto) qMetadata.deepCopy();
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
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto) qMetadata.deepCopy();
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
                                    map[count] = new TreeMap<>();
                                    if (fromAns != null) {
                                        fromQuestionId = fromQuestionId + "$" + fromAns;
                                        map[count].put(fromQuestionId, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto) qMetadata.deepCopy();
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
                    + qMetadata.getFromQuestionIdentifier() + EXCEPTION_APPENDING_MSG + qMetadata.getFromFormCd());
        }

    }

    @SuppressWarnings("java:S3776")
    private static void createPrePopToMap(Collection<LookupMappingDto> coll) throws DataProcessingException {
        int count = 0;
        int loopcount = 0;
        int sizecount = 0;
        String currentFormCode;
        String previousFormCode = "";

        TreeMap<Object, Object>[] map = new TreeMap[coll.size()];
        PrePopMappingDto qMetadata = null;
        try {
            if (coll.size() > 0) {
                for (LookupMappingDto lookupMappingDto : coll) {
                    sizecount++;
                    qMetadata = new PrePopMappingDto(lookupMappingDto);

                    if (qMetadata.getToFormCd() != null) {

                        if (loopcount == 0) {
                            previousFormCode = qMetadata.getToFormCd();
                            String toQuestionId = qMetadata.getToQuestionIdentifier() == null ? ""
                                    : qMetadata.getToQuestionIdentifier();
                            String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                    : qMetadata.getFromQuestionIdentifier();
                            toQuestionId = toQuestionId + '^' + fromQuestionId;
                            String fromAns = qMetadata.getFromAnswerCode();
                            String toQuestionIdWithAns;
                            map[count] = new TreeMap<>();
                            if (!toQuestionId.equals("")) {
                                if (fromAns != null) {
                                    toQuestionIdWithAns = toQuestionId + "$" + fromAns;
                                    map[count].put(toQuestionIdWithAns, qMetadata);
                                }
                                PrePopMappingDto qMetadata1 = (PrePopMappingDto) qMetadata.deepCopy();
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
                                String toQuestionIdWithAns;
                                toQuestionId = toQuestionId + '^' + fromQuestionId;
                                if (!toQuestionId.equals("")) {
                                    if (fromAns != null) {
                                        toQuestionIdWithAns = toQuestionId + "$" + fromAns;
                                        map[count].put(toQuestionIdWithAns, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto) qMetadata.deepCopy();
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
                                toQuestionId = toQuestionId + '^' + fromQuestionId;
                                String toQuestionIdWithAns;
                                map[count] = new TreeMap<>();
                                if (!toQuestionId.equals("")) {
                                    if (fromAns != null) {
                                        toQuestionIdWithAns = toQuestionId + "$" + fromAns;
                                        map[count].put(toQuestionIdWithAns, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto) qMetadata.deepCopy();
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
                    + qMetadata.getToQuestionIdentifier() + EXCEPTION_APPENDING_MSG + qMetadata.getToFormCd());
        }

    }


    @SuppressWarnings("java:S3776")
    private TreeMap<Object,Object> createDMBQuestionMap(Collection<MetaAndWaCommonAttribute>  coll) throws Exception{
        TreeMap<Object, Object> qCodeMap = new TreeMap<>();
        int count =0;
        int loopcount=0;
        int sizecount=0;
        String currentFormCode;
        String previousFormCode="";

        //For Demo Purpose CHOLERA Metadata
        TreeMap<Object, Object>[] map ;
        map = new TreeMap[coll.size()];
        NbsQuestionMetadata qMetadata = null;
        try{
            if (coll.size() > 0) {
                for (MetaAndWaCommonAttribute metaAndWaCommonAttribute : coll) {
                    sizecount++;
                    qMetadata = new NbsQuestionMetadata(metaAndWaCommonAttribute);
                    String dataType = qMetadata.getDataType();
                    List<CodeValueGeneral> aList;
                    if (dataType != null && dataType.equals(NEDSSConstant.NBS_QUESTION_DATATYPE_CODED_VALUE)) {
                        aList = catchingValueService.getGeneralCodedValue(qMetadata.getCodeSetNm());
                        qMetadata.setAList(aList);
                    }

                    if (qMetadata.getInvestigationFormCd() != null) {

                        if (loopcount == 0)
                        {
                            previousFormCode = qMetadata.getInvestigationFormCd();
                            String questionId = qMetadata.getQuestionIdentifier() == null ? "" : qMetadata.getQuestionIdentifier();
                            if (!questionId.equals("")) {
                                map[count] = new TreeMap<>();
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
                                if (!questionId.equals(""))
                                {
                                    map[count].put(questionId, qMetadata);
                                }
                            }
                            else
                            {
                                qCodeMap.put(previousFormCode, map[count]);
                                count = count + 1;
                                String questionId = qMetadata.getQuestionIdentifier() == null ? "" : qMetadata.getQuestionIdentifier();
                                if (!questionId.equals(""))
                                {
                                    map[count] = new TreeMap<>();
                                    map[count].put(questionId, qMetadata);
                                }

                            }
                            previousFormCode = currentFormCode;
                            loopcount++;
                        }

                    }
                    if (sizecount == coll.size()) {
                        qCodeMap.put(qMetadata.getInvestigationFormCd(), map[count]);
                    }

                }

            }
        }
        catch(Exception ex){
            throw new DataProcessingException("The caching failed due to question label :" + qMetadata.getQuestionLabel()+ EXCEPTION_APPENDING_MSG + qMetadata.getInvestigationFormCd());
        }

        return qCodeMap;
    }


    protected Collection<LookupMappingDto>  getPrePopMapping() throws DataProcessingException {

        try {
            return retrievePrePopMapping();
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    private Collection<Object>  getPamQuestions() {
        var res = nbsUiMetaDataRepository.findPamQuestionMetaData();
        Collection<Object>  questions = new ArrayList<>();
        if (res.isPresent()) {
            questions  = res.get();
        }
        return questions;
    }

    private TreeMap<Object,Object> createQuestionMap(Collection<Object>  coll) {
        TreeMap<Object,Object> qCodeMap = new TreeMap<>();
        TreeMap<Object,Object> qInvFormRVCTMap = new TreeMap<>();
        if (coll != null && coll.size() > 0) {
            for (Object o : coll) {
                NbsQuestionMetadata qMetadata = (NbsQuestionMetadata) o;
                if (qMetadata.getInvestigationFormCd().equals(NBSConstantUtil.INV_FORM_RVCT))
                {
                    qInvFormRVCTMap.put(qMetadata.getQuestionIdentifier(), qMetadata);
                }
            }
            qCodeMap.put(NBSConstantUtil.INV_FORM_RVCT, qInvFormRVCTMap);
        }
        return qCodeMap;
    }


}

