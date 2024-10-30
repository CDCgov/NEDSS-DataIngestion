package gov.cdc.dataprocessing.utilities.component.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.observation.*;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class ObservationRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(ObservationRepositoryUtil.class); // NOSONAR

    private final ObservationRepository observationRepository;
    private final ObservationReasonRepository observationReasonRepository;
    private final ActIdRepository actIdRepository;
    private final ObservationInterpRepository observationInterpRepository;
    private final ObsValueCodedRepository obsValueCodedRepository;
    private final ObsValueTxtRepository obsValueTxtRepository;
    private final ObsValueDateRepository obsValueDateRepository;
    private final ObsValueNumericRepository obsValueNumericRepository;
    private final ActLocatorParticipationRepository actLocatorParticipationRepository;
    private final ActRelationshipRepository actRelationshipRepository;
    private final ParticipationRepository participationRepository;
    private final EntityHelper entityHelper;
    private final IOdseIdGeneratorWCacheService odseIdGeneratorService;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;

    private final ActRepository actRepository;

    public ObservationRepositoryUtil(ObservationRepository observationRepository,
                                     ObservationReasonRepository observationReasonRepository,
                                     ActIdRepository actIdRepository,
                                     ObservationInterpRepository observationInterpRepository,
                                     ObsValueCodedRepository obsValueCodedRepository,
                                     ObsValueTxtRepository obsValueTxtRepository,
                                     ObsValueDateRepository obsValueDateRepository,
                                     ObsValueNumericRepository obsValueNumericRepository,
                                     ActLocatorParticipationRepository actLocatorParticipationRepository,
                                     ActRelationshipRepository actRelationshipRepository,
                                     ParticipationRepository participationRepository,
                                     EntityHelper entityHelper,
                                     IOdseIdGeneratorWCacheService odseIdGeneratorService, ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                                     ActRepository actRepository) {
        this.observationRepository = observationRepository;
        this.observationReasonRepository = observationReasonRepository;
        this.actIdRepository = actIdRepository;
        this.observationInterpRepository = observationInterpRepository;
        this.obsValueCodedRepository = obsValueCodedRepository;
        this.obsValueTxtRepository = obsValueTxtRepository;
        this.obsValueDateRepository = obsValueDateRepository;
        this.obsValueNumericRepository = obsValueNumericRepository;
        this.actLocatorParticipationRepository = actLocatorParticipationRepository;
        this.actRelationshipRepository = actRelationshipRepository;
        this.participationRepository = participationRepository;
        this.entityHelper = entityHelper;
        this.odseIdGeneratorService = odseIdGeneratorService;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.actRepository = actRepository;
    }


    public ObservationContainer loadObject(long obUID) throws DataProcessingException
    {
        ObservationContainer obVO;
        try{
            obVO = new ObservationContainer();

            /**
             *  Selects ObservationDto object
             */

            ObservationDto obDT = selectObservation(obUID);
            obVO.setTheObservationDto(obDT);
            /**
             * Selects ObservationReasonDto List
             */

            Collection<ObservationReasonDto> obReasonColl = selectObservationReasons(obUID);
            obVO.setTheObservationReasonDtoCollection(obReasonColl);

            /**
             * Selects ActityIdDT collection
             */

            Collection<ActIdDto> idColl = selectActivityIDs(obUID);
            obVO.setTheActIdDtoCollection(idColl);

            /**
             * Selects ObservationInterpDto collection
             */

            Collection<ObservationInterpDto> obInterpColl = selectObservationInterps(obUID);
            obVO.setTheObservationInterpDtoCollection(obInterpColl);

            /**
             * Selects ObsValueCodedDto collection
             */

            Collection<ObsValueCodedDto> obsValueCodedColl = selectObsValueCodeds(obUID);
            obVO.setTheObsValueCodedDtoCollection(obsValueCodedColl);

            /**
             * Selects ObsValueTxtDto collection
             */

            Collection<ObsValueTxtDto> obsValueTxtColl = selectObsValueTxts(obUID);
            obVO.setTheObsValueTxtDtoCollection(obsValueTxtColl);

            /**
             * Selects ObsValueDateDto collection
             */

            Collection<ObsValueDateDto> obsValueDateColl = selectObsValueDates(obUID);
            obVO.setTheObsValueDateDtoCollection(obsValueDateColl);

            /**
             * Selects ObsValueNumericDto collection
             */

            Collection<ObsValueNumericDto> obsValueNumericColl = selectObsValueNumerics(obUID);
            obVO.setTheObsValueNumericDtoCollection(obsValueNumericColl);

            /**
             * Selects ActivityLocatorParticipationDto collection
             */

            Collection<ActivityLocatorParticipationDto> activityLocatorParticipationColl = selectActivityLocatorParticipations(obUID);
            obVO.setTheActivityLocatorParticipationDtoCollection(activityLocatorParticipationColl);

            //Selects ActRelationshiopDTcollection
            Collection<ActRelationshipDto> actColl = actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(obUID);
            obVO.setTheActRelationshipDtoCollection(actColl);

            //SelectsParticipationDTCollection
            Collection<ParticipationDto> parColl = selectParticipationDTCollection(obUID);
            obVO.setTheParticipationDtoCollection(parColl);

            obVO.setItNew(false);
            obVO.setItDirty(false);
            return obVO;
        }catch(Exception ex){
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    @Transactional
    public Long saveObservation(ObservationContainer observationContainer) throws DataProcessingException {
        Long observationUid = -1L;


        try {
            Observation observation = null;

            Collection<ActivityLocatorParticipationDto> alpDTCol = observationContainer.getTheActivityLocatorParticipationDtoCollection();
            Collection<ActRelationshipDto> arDTCol = observationContainer.getTheActRelationshipDtoCollection();
            Collection<ParticipationDto> pDTCol = observationContainer.getTheParticipationDtoCollection();
            Collection<ActRelationshipDto> colAct;
            Collection<ParticipationDto> colParticipation;
            Collection<ActivityLocatorParticipationDto> colActLocatorParticipation;


            if (alpDTCol != null)
            {
                colActLocatorParticipation = entityHelper.iterateActivityParticipation(alpDTCol);
                observationContainer.setTheActivityLocatorParticipationDtoCollection(colActLocatorParticipation);
            }

            if (arDTCol != null)
            {
                colAct = entityHelper.iterateActRelationship(arDTCol);
                observationContainer.setTheActRelationshipDtoCollection(colAct);
            }

            if (pDTCol != null)
            {
                colParticipation = entityHelper.iteratePDTForParticipation(pDTCol);
                observationContainer.setTheParticipationDtoCollection(colParticipation);
            }

            if (observationContainer.isItNew())
            {
                //observation = home.create(observationContainer);
                observationUid = this.createNewObservation(observationContainer);
            }
            else
            {
                if (observationContainer.getTheObservationDto() != null) // make sure it is not null
                {
                    this.updateObservation(observationContainer);
                    observationUid = observationContainer.getTheObservationDto().getObservationUid();
                }
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return observationUid;

    }

    @Transactional
    public Long createNewObservation(ObservationContainer observationContainer) throws DataProcessingException {
        try {
            Long obsId = saveNewObservation(observationContainer.getTheObservationDto());
            observationContainer.getTheObservationDto().setItNew(false);
            observationContainer.getTheObservationDto().setItDirty(false);

            addObservationReasons(obsId, observationContainer.getTheObservationReasonDtoCollection());
            addActivityId(obsId, observationContainer.getTheActIdDtoCollection(), false);
            addObservationInterps(obsId, observationContainer.getTheObservationInterpDtoCollection());
            addObsValueCoded(obsId, observationContainer.getTheObsValueCodedDtoCollection());
            addObsValueTxts(obsId, observationContainer.getTheObsValueTxtDtoCollection());
            addObsValueDates(obsId, observationContainer.getTheObsValueDateDtoCollection());
            addObsValueNumeric(obsId, observationContainer.getTheObsValueNumericDtoCollection());
            addActivityLocatorParticipations(obsId, observationContainer.getTheActivityLocatorParticipationDtoCollection());
            return obsId;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    @Transactional
    public Long updateObservation(ObservationContainer observationContainer) throws DataProcessingException {
        Long uid;
        if (observationContainer.getTheObservationDto().getObservationUid() == null) {
            uid = saveNewObservation(observationContainer.getTheObservationDto());
            observationContainer.getTheObservationDto().setItNew(false);
            observationContainer.getTheObservationDto().setItDirty(false);
        } else {
            uid = saveObservation(observationContainer.getTheObservationDto());
            observationContainer.getTheObservationDto().setItNew(false);
            observationContainer.getTheObservationDto().setItDirty(false);
        }

        if (observationContainer.getTheObservationReasonDtoCollection() != null) {
            updateObservationReason(uid, observationContainer.getTheObservationReasonDtoCollection());
        }

        if (observationContainer.getTheActIdDtoCollection() != null) {
            addActivityId(uid, observationContainer.getTheActIdDtoCollection(), true);
        }

        if (observationContainer.getTheObservationInterpDtoCollection() != null) {
            updateObservationInterps(uid, observationContainer.getTheObservationInterpDtoCollection());
        }

        if (observationContainer.getTheObsValueCodedDtoCollection() != null) {
            updateObsValueCoded(uid, observationContainer.getTheObsValueCodedDtoCollection());
        }

        if (observationContainer.getTheObsValueTxtDtoCollection() != null) {
            updateObsValueTxts(uid, observationContainer.getTheObsValueTxtDtoCollection());
        }

        if (observationContainer.getTheObsValueDateDtoCollection() != null) {
            updateObsValueDates(uid, observationContainer.getTheObsValueDateDtoCollection());
        }

        if (observationContainer.getTheObsValueNumericDtoCollection() != null) {
            updateObsValueNumerics(uid, observationContainer.getTheObsValueNumericDtoCollection());
        }

        if (observationContainer.getTheActivityLocatorParticipationDtoCollection() != null) {
            addActivityLocatorParticipations(uid, observationContainer.getTheActivityLocatorParticipationDtoCollection());
        }

        return uid;
    }

    @Transactional
    public void saveActRelationship(ActRelationshipDto actRelationshipDto) {
        ActRelationship actRelationship = new ActRelationship(actRelationshipDto);


        if (actRelationshipDto.isItNew())
        {
            actRelationshipRepository.save(actRelationship);
        }
        else if (actRelationshipDto.isItDelete())
        {
            actRelationshipRepository.delete(actRelationship);
        }
        else if (actRelationshipDto.isItDirty() &&
                (actRelationshipDto.getTargetActUid() != null &&
                        actRelationshipDto.getSourceActUid() != null && actRelationshipDto.getTypeCd() != null)
        )
        {
            actRelationshipRepository.save(actRelationship);
        }


        actRelationshipRepository.save(actRelationship);
    }


    @Transactional
    public void setObservationInfo(ObservationDto observationDto) throws DataProcessingException {
        ObservationContainer observationVO = null;

        if (observationDto.getObservationUid() != null)
            observationVO = loadObject(observationDto.getObservationUid());

        if (observationVO != null)
            observationVO.setTheObservationDto(observationDto);

        if (observationVO == null)
        {
            observationVO = new ObservationContainer();
            observationVO.setTheObservationDto(observationDto);
        }

        observationVO.setTheObservationDto(observationDto);
        observationVO.setItDirty(true);
        this.saveObservation(observationVO);
    }
    @SuppressWarnings("java:S3776")

    public Collection<ObservationContainer> retrieveObservationQuestion(Long targetActUid) {

        ArrayList<ObservationContainer> theObservationQuestionColl = new ArrayList<> ();
        var observationQuestion = observationRepository.retrieveObservationQuestion(targetActUid);
        if (observationQuestion.isPresent()) {
            Long previousTargetActUid = null;
            Long previousObservationUid = null;
            ObservationContainer obsVO = null;
            ArrayList<ObsValueCodedDto> obsCodes = null;
            ArrayList<ObsValueDateDto> obsDates = null;
            ArrayList<ObsValueNumericDto> obsNumerics = null;
            ArrayList<ObsValueTxtDto> obsValueTxts = null;
            for (Observation_Question observation_question : observationQuestion.get()) {

                if (previousTargetActUid == null ||
                        !previousTargetActUid.equals(observation_question.getTargetActUid()))
                {
                    if (previousObservationUid == null ||
                            !previousObservationUid.equals(observation_question.getObservationUid())) {
                        obsVO = new ObservationContainer();
                        theObservationQuestionColl.add(obsVO);
                        obsCodes = null;
                        obsDates = null;
                        obsNumerics = null;
                        obsValueTxts = null;
                        previousObservationUid = observation_question.getObservationUid();
                        obsVO.getTheObservationDto().setObservationUid(observation_question.getObservationUid());
                        obsVO.getTheObservationDto().setVersionCtrlNbr(observation_question.getVersionCtrlNbr());
                        obsVO.getTheObservationDto().setSharedInd(observation_question.getSharedInd());
                        obsVO.getTheObservationDto().setCd(observation_question.getCd());
                        obsVO.getTheObservationDto().setCtrlCdDisplayForm(observation_question.getCtrlCdDisplayForm());
                        obsVO.getTheObservationDto().setLocalId(observation_question.getLocalId());
                        obsVO.getTheObservationDto().setCdDescTxt(observation_question.getCdDescTxt());
                        obsVO.getTheObservationDto().setCdSystemDescTxt(observation_question.getCdSystemDescTxt());
                        obsVO.getTheObservationDto().setCdSystemCd(observation_question.getCdSystemCd());
                        obsVO.getTheObservationDto().setCdVersion(observation_question.getCdVersion());
                        obsVO.getTheObservationDto().setItNew(false);
                        obsVO.getTheObservationDto().setItDirty(false);
                    }
                    if (observation_question.getObsCodeUid() != null) {
                        if (obsCodes == null) {
                            obsCodes = new ArrayList<>();
                            obsVO.setTheObsValueCodedDtoCollection(obsCodes);
                        }
                        ObsValueCodedDto obsCode = new ObsValueCodedDto();
                        obsCode.setObservationUid(observation_question.getObsCodeUid());
                        obsCode.setCode(observation_question.getCode());
                        obsCode.setCodeSystemDescTxt(observation_question.getCodeSystemDescTxt());
                        obsCode.setOriginalTxt(observation_question.getOriginalTxt());
                        obsCode.setItNew(false);
                        obsCode.setItDirty(false);
                        obsCodes.add(obsCode);
                    }
                    if (observation_question.getObsDateUid() != null) {
                        if (obsDates == null) {
                            obsDates = new ArrayList<>();
                            obsVO.setTheObsValueDateDtoCollection(obsDates);
                        }
                        ObsValueDateDto obsDate = new ObsValueDateDto();
                        obsDate.setObservationUid(observation_question.getObsDateUid());
                        obsDate.setFromTime(observation_question.getFromTime());
                        obsDate.setToTime(observation_question.getToTime());
                        obsDate.setDurationAmt(observation_question.getDurationAmt());
                        obsDate.setDurationUnitCd(observation_question.getDurationUnitCd());
                        obsDate.setObsValueDateSeq(observation_question.getObsValueDateSeq());
                        obsDate.setItNew(false);
                        obsDate.setItDirty(false);
                        obsDates.add(obsDate);
                    }
                    if (observation_question.getObsNumericUid() != null) {
                        if (obsNumerics == null) {
                            obsNumerics = new ArrayList<>();
                            obsVO.setTheObsValueNumericDtoCollection(obsNumerics);
                        }
                        ObsValueNumericDto obsNumeric = new ObsValueNumericDto();
                        obsNumeric.setObservationUid(observation_question.getObsNumericUid());
                        obsNumeric.setNumericScale1(observation_question.getNumericScale1());
                        obsNumeric.setNumericScale2(observation_question.getNumericScale2());
                        obsNumeric.setNumericValue1(observation_question.getNumericValue1());
                        obsNumeric.setNumericValue2(observation_question.getNumericValue2());
                        obsNumeric.setNumericUnitCd(observation_question.getNumericUnitCd());
                        obsNumeric.setObsValueNumericSeq(observation_question.getObsValueNumericSeq());
                        obsNumeric.setItNew(false);
                        obsNumeric.setItDirty(false);
                        obsNumerics.add(obsNumeric);
                    }
                    if (observation_question.getObsTxtUid() != null) {
                        if (obsValueTxts == null) {
                            obsValueTxts = new ArrayList<>();
                            obsVO.setTheObsValueTxtDtoCollection(obsValueTxts);
                        }
                        ObsValueTxtDto obsValueTxt = new ObsValueTxtDto();
                        obsValueTxt.setObservationUid(observation_question.getObsTxtUid());
                        obsValueTxt.setValueTxt(observation_question.getValueTxt());
                        obsValueTxt.setObsValueTxtSeq(observation_question.getObsValueTxtSeq());
                        obsValueTxt.setItNew(false);
                        obsValueTxt.setItDirty(false);
                        obsValueTxts.add(obsValueTxt);
                    }

                    previousTargetActUid = observation_question.getTargetActUid();
                    if (previousTargetActUid != null &&
                            previousTargetActUid.equals(observation_question.getObservationUid())) {
                        Collection<ActRelationshipDto> actColl = new ArrayList<>();
                        ActRelationshipDto ar = new ActRelationshipDto();
                        ar.setSourceActUid(observation_question.getSourceActUid());
                        ar.setTargetActUid(observation_question.getTargetActUid());
                        ar.setTypeCd(observation_question.getTypeCd());
                        ar.setItDirty(false);
                        actColl.add(ar);
                        obsVO.setTheActRelationshipDtoCollection(actColl);
                    }
                }
                else
                {
                    ObservationContainer innerObs =  theObservationQuestionColl.
                            get(theObservationQuestionColl.size() - 1);
                    Collection<ActRelationshipDto> actColl;
                    if ((actColl = innerObs.getTheActRelationshipDtoCollection()) == null) {
                        actColl = new ArrayList<>();
                    }
                    ActRelationshipDto ar = new ActRelationshipDto();
                    ar.setSourceActUid(observation_question.getSourceActUid());
                    ar.setTargetActUid(observation_question.getTargetActUid());
                    ar.setTypeCd(observation_question.getTypeCd());
                    ar.setRecordStatusCd(NEDSSConstant.ACTIVE);
                    ar.setItDirty(false);
                    actColl.add(ar);
                    innerObs.setTheActRelationshipDtoCollection(actColl);
                    theObservationQuestionColl.set(theObservationQuestionColl.size() - 1,
                            innerObs);
                }

            }
        }

        return theObservationQuestionColl;

    }

    private ObservationDto selectObservation(long obUID) throws  DataProcessingException {
        try {
            // QUERY OBS
            var result = observationRepository.findById(obUID);
            if (result.isPresent()) {
                ObservationDto item = new ObservationDto(result.get());
                item.setItNew(false);
                item.setItDirty(false);
                return  item;
            } else {
                throw new DataProcessingException("NO OBS FOUND");
            }

        } catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }

    }

    private Collection<ObservationReasonDto> selectObservationReasons(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObservationReason> observationReasons = observationReasonRepository.findRecordsById(aUID);
            Collection<ObservationReasonDto> dtCollection = new ArrayList<>();
            for (var observationReason : observationReasons) {
                ObservationReasonDto dt = new ObservationReasonDto(observationReason);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }
    }

    private Collection<ActIdDto> selectActivityIDs(long aUID) throws  DataProcessingException
    {
        try
        {
            var result  = actIdRepository.findRecordsById(aUID);
            Collection<ActIdDto> dtCollection = new ArrayList<>();
            if (result.isPresent()) {
                Collection<ActId> col = result.get();
                for (var item : col) {
                    ActIdDto dt = new ActIdDto(item);
                    dt.setItNew(false);
                    dt.setItDirty(false);
                    dtCollection.add(dt);
                }
            }

            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }

    }

    private Collection<ObservationInterpDto> selectObservationInterps(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObservationInterp> col = observationInterpRepository.findRecordsById(aUID);
            Collection<ObservationInterpDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObservationInterpDto dt = new ObservationInterpDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }
    }

    private Collection<ObsValueCodedDto> selectObsValueCodeds(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueCoded> col = obsValueCodedRepository.findRecordsById(aUID);
            Collection<ObsValueCodedDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueCodedDto dt = new ObsValueCodedDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }
    }

    private Collection<ObsValueTxtDto> selectObsValueTxts(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueTxt> col = obsValueTxtRepository.findRecordsById(aUID);
            Collection<ObsValueTxtDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueTxtDto dt = new ObsValueTxtDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }
    }

    private Collection<ObsValueDateDto> selectObsValueDates(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueDate> col = obsValueDateRepository.findRecordsById(aUID);
            Collection<ObsValueDateDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueDateDto dt = new ObsValueDateDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }
    }

    private Collection<ObsValueNumericDto> selectObsValueNumerics(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueNumeric> col = obsValueNumericRepository.findRecordsById(aUID);
            Collection<ObsValueNumericDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueNumericDto dt = new ObsValueNumericDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }
    }

    private Collection<ActivityLocatorParticipationDto> selectActivityLocatorParticipations(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ActLocatorParticipation> col = actLocatorParticipationRepository.findRecordsById(aUID);
            Collection<ActivityLocatorParticipationDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ActivityLocatorParticipationDto dt = new ActivityLocatorParticipationDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }
    }


    private Collection<ParticipationDto> selectParticipationDTCollection(long aUID) throws DataProcessingException
    {
        try
        {
            var col = participationRepository.findByActUid(aUID);
            Collection<ParticipationDto> dtCollection = new ArrayList<>();
            if (col.isPresent()) {
                for (var item : col.get()) {
                    ParticipationDto dt = new ParticipationDto(item);
                    dt.setItNew(false);
                    dt.setItDirty(false);
                    dtCollection.add(dt);
                }
            }

            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }
    }

    private Long saveNewObservation(ObservationDto observationDto) throws DataProcessingException {
        try {
            var uid = odseIdGeneratorService.getValidLocalUid(LocalIdClass.OBSERVATION, true);

            Act act = new Act();
            act.setActUid(uid.getGaTypeUid().getSeedValueNbr());
            act.setClassCode(NEDSSConstant.OBSERVATION_CLASS_CODE);
            act.setMoodCode(NEDSSConstant.EVENT_MOOD_CODE);

            actRepository.save(act);

            observationDto.setAddUserId(AuthUtil.authUser.getNedssEntryId());
            observationDto.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());

            Observation observation = new Observation(observationDto);
            observation.setVersionCtrlNbr(1);
            //Shared Ind is not Null, existing data are set to T hence set it as T here
            observation.setSharedInd("T");
            observation.setLocalId(uid.getClassTypeUid().getUidPrefixCd() + uid.getClassTypeUid().getSeedValueNbr() + uid.getClassTypeUid().getUidSuffixCd());
            observation.setObservationUid(uid.getGaTypeUid().getSeedValueNbr());

            observationRepository.save(observation);
            return uid.getGaTypeUid().getSeedValueNbr();
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private Long saveObservation(ObservationDto observationDto) {
        Observation observation = new Observation(observationDto);
        observationRepository.save(observation);
        return observation.getObservationUid();
    }

    // private void insertObservationReasons(ObservationContainer obVO) throws  NEDSSSystemException
    private void  addObservationReasons(Long obsUid, Collection<ObservationReasonDto> observationReasonDtoCollection) throws DataProcessingException {
        try {
            if (observationReasonDtoCollection != null) {
                ArrayList<ObservationReasonDto> arr = new ArrayList<>(observationReasonDtoCollection);
                for(var item: arr) {
                    item.setObservationUid(obsUid);
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    saveObservationReason(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void saveObservationReason(ObservationReasonDto item) {
        var data = new ObservationReason(item);
        observationReasonRepository.save(data);
    }

    private void updateObservationReason(Long obsUid, Collection<ObservationReasonDto> observationReasonDtoCollection) throws DataProcessingException {
        try {
            ArrayList<ObservationReasonDto> arr = new ArrayList<>(observationReasonDtoCollection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObservationReason(item);
                } else {
                    observationReasonRepository.delete(new ObservationReason(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void addActivityId(Long obsUid, Collection<ActIdDto> actIdDtoCollection, boolean updateApplied) throws DataProcessingException {
        if (actIdDtoCollection != null) {
            int maxSegId = 0;
            if (!updateApplied) {
                var res = actIdRepository.findRecordsById(obsUid);
                if (res.isPresent()) {
                    var existingAct = new ArrayList<>(res.get());
                    if (!existingAct.isEmpty()) {
                        maxSegId = existingAct.stream().mapToInt(ActId::getActIdSeq).max().orElseThrow(() -> new DataProcessingException("List is empty"));
                    }
                }
            }

            ArrayList<ActIdDto> arr = new ArrayList<>(actIdDtoCollection);
            for(var item: arr) {
                item.setItNew(false);
                item.setItDirty(false);
                item.setItDelete(false);
                item.setActUid(obsUid);
                if (!updateApplied) {
                    item.setActIdSeq(++maxSegId);
                }
                var reason = new ActId(item);
                actIdRepository.save(reason);
            }
        }


    }

    private void addObservationInterps(Long obsUid, Collection<ObservationInterpDto> observationInterpDtoCollection) throws DataProcessingException {
        try {
            if (observationInterpDtoCollection != null) {
                ArrayList<ObservationInterpDto> arr = new ArrayList<>(observationInterpDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObservationInterp(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void saveObservationInterp(ObservationInterpDto item) {
        var reason = new ObservationInterp(item);
        observationInterpRepository.save(reason);
    }

    private void updateObservationInterps(Long obsUid, Collection<ObservationInterpDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObservationInterpDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObservationInterp(item);
                } else {
                    observationInterpRepository.delete(new ObservationInterp(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void addObsValueCoded(Long obsUid, Collection<ObsValueCodedDto> obsValueCodedDtoCollection) throws DataProcessingException {
        try {
            if (obsValueCodedDtoCollection != null) {
                ArrayList<ObsValueCodedDto> arr = new ArrayList<>(obsValueCodedDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObsValueCoded(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void saveObsValueCoded(ObsValueCodedDto item) {
        var reason = new ObsValueCoded(item);
        obsValueCodedRepository.save(reason);
    }

    private void updateObsValueCoded(Long obsUid, Collection<ObsValueCodedDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueCodedDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObsValueCoded(item);

                } else {
                    obsValueCodedRepository.delete(new ObsValueCoded(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void addObsValueTxts(Long obsUid, Collection<ObsValueTxtDto> obsValueTxtDtoCollection) throws DataProcessingException {
        try {
            if (obsValueTxtDtoCollection != null)  {
                ArrayList<ObsValueTxtDto> arr = new ArrayList<>(obsValueTxtDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObsValueTxt(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }


    }

    private void saveObsValueTxt(ObsValueTxtDto item) {
        var reason = new ObsValueTxt(item);
        obsValueTxtRepository.save(reason);
    }

    private void updateObsValueTxts(Long obsUid, Collection<ObsValueTxtDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueTxtDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObsValueTxt(item);

                } else {
                    obsValueTxtRepository.delete(new ObsValueTxt(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void addObsValueDates(Long obsUid, Collection<ObsValueDateDto> obsValueDateDtoCollection) throws DataProcessingException {
        try {
            if (obsValueDateDtoCollection != null) {
                ArrayList<ObsValueDateDto> arr = new ArrayList<>(obsValueDateDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObsValueDate(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }


    }

    private void saveObsValueDate(ObsValueDateDto item) {
        var reason = new ObsValueDate(item);
        obsValueDateRepository.save(reason);
    }

    private void updateObsValueDates(Long obsUid, Collection<ObsValueDateDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueDateDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObsValueDate(item);

                } else {
                    obsValueDateRepository.delete(new ObsValueDate(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void addObsValueNumeric(Long obsUid, Collection<ObsValueNumericDto> obsValueNumericDtoCollection) throws DataProcessingException {
        try {
            if (obsValueNumericDtoCollection != null) {
                ArrayList<ObsValueNumericDto> arr = new ArrayList<>(obsValueNumericDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObsValueNumeric(item);
                }
            }
        } catch (Exception e) {
            throw  new DataProcessingException(e.getMessage(), e);
        }


    }

    private void saveObsValueNumeric(ObsValueNumericDto item)  {
        var reason = new ObsValueNumeric(item);
        obsValueNumericRepository.save(reason);
    }

    private void updateObsValueNumerics(Long obsUid, Collection<ObsValueNumericDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueNumericDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObsValueNumeric(item);

                } else {
                    obsValueNumericRepository.delete(new ObsValueNumeric(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    protected void addActivityLocatorParticipations(Long obsUid, Collection<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection) throws DataProcessingException {
        try {
            if (activityLocatorParticipationDtoCollection != null) {
                ArrayList<ActivityLocatorParticipationDto> arr = new ArrayList<>(activityLocatorParticipationDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setActUid(obsUid);
                    var reason = new ActLocatorParticipation(item);
                    actLocatorParticipationRepository.save(reason);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


}
