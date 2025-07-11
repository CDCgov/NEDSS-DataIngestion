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
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

import static gov.cdc.dataprocessing.constant.DpConstant.OPERATION_CREATE;
import static gov.cdc.dataprocessing.constant.DpConstant.OPERATION_UPDATE;

@Component

public class ObservationRepositoryUtil {

    private final EntityHelper entityHelper;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    private final ObservationJdbcRepository observationJdbcRepository;
    private final ActJdbcRepository actJdbcRepository;
    private final ActRelationshipJdbcRepository actRelationshipJdbcRepository;
    private final ActIdJdbcRepository actIdJdbcRepository;
    private final ActLocatorParticipationJdbcRepository actLocatorParticipationJdbcRepository;
    private final ParticipationJdbcRepository participationJdbcRepository;
    private final UidPoolManager uidPoolManager;
    public ObservationRepositoryUtil(
                                     EntityHelper entityHelper,
                                     ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                                     ObservationJdbcRepository observationJdbcRepository,
                                     ActJdbcRepository actJdbcRepository,
                                     ActRelationshipJdbcRepository actRelationshipJdbcRepository,
                                     ActIdJdbcRepository actIdJdbcRepository,
                                     ActLocatorParticipationJdbcRepository actLocatorParticipationJdbcRepository,
                                     ParticipationJdbcRepository participationJdbcRepository,
                                     @Lazy UidPoolManager uidPoolManager) {
        this.entityHelper = entityHelper;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.observationJdbcRepository = observationJdbcRepository;
        this.actJdbcRepository = actJdbcRepository;
        this.actRelationshipJdbcRepository = actRelationshipJdbcRepository;
        this.actIdJdbcRepository = actIdJdbcRepository;
        this.actLocatorParticipationJdbcRepository = actLocatorParticipationJdbcRepository;
        this.participationJdbcRepository = participationJdbcRepository;
        this.uidPoolManager = uidPoolManager;
    }


    public ObservationContainer loadObject(long obUID) throws DataProcessingException
    {
        ObservationContainer obVO;
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

    }

    public Long saveObservation(ObservationContainer observationContainer) throws DataProcessingException {
        Long observationUid = -1L;


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


        return observationUid;

    }

    public Long createNewObservation(ObservationContainer observationContainer) throws DataProcessingException {
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
        addActivityLocatorParticipations(obsId, observationContainer.getTheActivityLocatorParticipationDtoCollection(), OPERATION_CREATE);
        return obsId;
    }

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
            addActivityLocatorParticipations(uid, observationContainer.getTheActivityLocatorParticipationDtoCollection(), OPERATION_UPDATE);
        }

        return uid;
    }

    public void saveActRelationship(ActRelationshipDto actRelationshipDto) {
        ActRelationship actRelationship = new ActRelationship(actRelationshipDto);


        if (actRelationshipDto.isItNew())
        {
            actRelationshipJdbcRepository.insertActRelationship(actRelationship);
        }
        else if (actRelationshipDto.isItDelete())
        {
            actRelationshipJdbcRepository.deleteActRelationship(actRelationship);
        }
        else if (actRelationshipDto.isItDirty() &&
                (actRelationshipDto.getTargetActUid() != null &&
                        actRelationshipDto.getSourceActUid() != null && actRelationshipDto.getTypeCd() != null)
        )
        {
            actRelationshipJdbcRepository.updateActRelationship(actRelationship);
        }
    }


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
    @SuppressWarnings({"java:S3776","java:S6541"})
    public Collection<ObservationContainer> retrieveObservationQuestion(Long targetActUid) {

        ArrayList<ObservationContainer> theObservationQuestionColl = new ArrayList<> ();
        var observationQuestion = observationJdbcRepository.retrieveObservationQuestion(targetActUid);
        if (observationQuestion != null && !observationQuestion.isEmpty()) {
            Long previousTargetActUid = null;
            Long previousObservationUid = null;
            ObservationContainer obsVO = null;
            ArrayList<ObsValueCodedDto> obsCodes = null;
            ArrayList<ObsValueDateDto> obsDates = null;
            ArrayList<ObsValueNumericDto> obsNumerics = null;
            ArrayList<ObsValueTxtDto> obsValueTxts = null;
            for (Observation_Question observation_question : observationQuestion) {

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
                            getLast();
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
        // QUERY OBS
        var result = observationJdbcRepository.findObservationByUid(obUID);
        if (result != null) {
            ObservationDto item = new ObservationDto(result);
            item.setItNew(false);
            item.setItDirty(false);
            return  item;
        } else {
            throw new DataProcessingException("NO OBS FOUND");
        }
    }

    protected Collection<ObservationReasonDto> selectObservationReasons(long aUID)
    {
        Collection<ObservationReason> observationReasons = observationJdbcRepository.findByObservationReasons(aUID);
        Collection<ObservationReasonDto> dtCollection = new ArrayList<>();
        for (var observationReason : observationReasons) {
            ObservationReasonDto dt = new ObservationReasonDto(observationReason);
            dt.setItNew(false);
            dt.setItDirty(false);
            dtCollection.add(dt);
        }
        return dtCollection;
    }

    protected Collection<ActIdDto> selectActivityIDs(long aUID)
    {

        var result  = actIdJdbcRepository.findRecordsByActUid(aUID);
        Collection<ActIdDto> dtCollection = new ArrayList<>();
        if (result != null && !result.isEmpty()) {
            for (var item : result) {
                ActIdDto dt = new ActIdDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
        }

        return dtCollection;


    }

    protected Collection<ObservationInterpDto> selectObservationInterps(long aUID)
    {
        Collection<ObservationInterp> col = observationJdbcRepository.findByObservationInterp(aUID);
        Collection<ObservationInterpDto> dtCollection = new ArrayList<>();
        if (col != null && !col.isEmpty()) {
            for (var item : col) {
                ObservationInterpDto dt = new ObservationInterpDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
        }

        return dtCollection;
    }

    protected Collection<ObsValueCodedDto> selectObsValueCodeds(long aUID)
    {
        Collection<ObsValueCoded> col = observationJdbcRepository.findByObservationCodedUid(aUID);
        Collection<ObsValueCodedDto> dtCollection = new ArrayList<>();
        if (col != null && !col.isEmpty()) {
            for (var item : col) {
                ObsValueCodedDto dt = new ObsValueCodedDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
        }
        return dtCollection;
    }

    protected Collection<ObsValueTxtDto> selectObsValueTxts(long aUID)
    {
        Collection<ObsValueTxt> col = observationJdbcRepository.findByObservationTxtUid(aUID);
        Collection<ObsValueTxtDto> dtCollection = new ArrayList<>();
        if (col != null && !col.isEmpty()) {
            for (var item : col) {
                ObsValueTxtDto dt = new ObsValueTxtDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
        }
        return dtCollection;
    }

    protected Collection<ObsValueDateDto> selectObsValueDates(long aUID)
    {
        Collection<ObsValueDate> col = observationJdbcRepository.findByObservationDateUid(aUID);
        Collection<ObsValueDateDto> dtCollection = new ArrayList<>();
        if (col != null && !col.isEmpty()) {
            for (var item : col) {
                ObsValueDateDto dt = new ObsValueDateDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
        }

        return dtCollection;
    }

    protected Collection<ObsValueNumericDto> selectObsValueNumerics(long aUID)
    {
        Collection<ObsValueNumeric> col = observationJdbcRepository.findByObservationNumericUid(aUID);
        Collection<ObsValueNumericDto> dtCollection = new ArrayList<>();
        if (col != null && !col.isEmpty()) {
            for (var item : col) {
                ObsValueNumericDto dt = new ObsValueNumericDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
        }
        return dtCollection;
    }

    protected Collection<ActivityLocatorParticipationDto> selectActivityLocatorParticipations(long aUID)
    {
        Collection<ActLocatorParticipation> col = actLocatorParticipationJdbcRepository.findByActUid(aUID);
        Collection<ActivityLocatorParticipationDto> dtCollection = new ArrayList<>();
        if (col != null && !col.isEmpty()) {
            for (var item : col) {
                ActivityLocatorParticipationDto dt = new ActivityLocatorParticipationDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
        }
        return dtCollection;
    }


    protected Collection<ParticipationDto> selectParticipationDTCollection(long aUID)
    {
        var col = participationJdbcRepository.findByActUid(aUID);
        Collection<ParticipationDto> dtCollection = new ArrayList<>();
        if (col != null && !col.isEmpty()) {
            for (var item : col) {
                ParticipationDto dt = new ParticipationDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
        }

        return dtCollection;
    }

    protected Long saveNewObservation(ObservationDto observationDto) throws DataProcessingException {
        var uid = uidPoolManager.getNextUid(LocalIdClass.OBSERVATION, true);
        Act act = new Act();
        act.setActUid(uid.getGaTypeUid().getSeedValueNbr());
        act.setClassCode(NEDSSConstant.OBSERVATION_CLASS_CODE);
        act.setMoodCode(NEDSSConstant.EVENT_MOOD_CODE);

        actJdbcRepository.insertAct(act);

        observationDto.setAddUserId(AuthUtil.authUser.getNedssEntryId());
        observationDto.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());

        Observation observation = new Observation(observationDto);
        observation.setVersionCtrlNbr(1);
        //Shared Ind is not Null, existing data are set to T hence set it as T here
        observation.setSharedInd("T");
        observation.setLocalId(uid.getClassTypeUid().getUidPrefixCd() + uid.getClassTypeUid().getSeedValueNbr() + uid.getClassTypeUid().getUidSuffixCd());
        observation.setObservationUid(uid.getGaTypeUid().getSeedValueNbr());

        observationJdbcRepository.insertObservation(observation);
        return uid.getGaTypeUid().getSeedValueNbr();
    }

    private Long saveObservation(ObservationDto observationDto) {
        Observation observation = new Observation(observationDto);
        observationJdbcRepository.updateObservation(observation);
        return observation.getObservationUid();
    }

    protected void  addObservationReasons(Long obsUid, Collection<ObservationReasonDto> observationReasonDtoCollection)   {
        if (observationReasonDtoCollection != null) {
            ArrayList<ObservationReasonDto> arr = new ArrayList<>(observationReasonDtoCollection);
            for(var item: arr) {
                item.setObservationUid(obsUid);
                item.setItNew(false);
                item.setItDirty(false);
                item.setItDelete(false);
                saveObservationReason(item, OPERATION_CREATE);
            }
        }
    }

    protected void saveObservationReason(ObservationReasonDto item, String operation) {
        var data = new ObservationReason(item);
        if (operation.equalsIgnoreCase(OPERATION_CREATE)) {
            observationJdbcRepository.insertObservationReason(data);
        } else if (operation.equalsIgnoreCase(OPERATION_UPDATE)) {
            observationJdbcRepository.updateObservationReason(data);
        }
    }

    protected void updateObservationReason(Long obsUid, Collection<ObservationReasonDto> observationReasonDtoCollection)   {
        ArrayList<ObservationReasonDto> arr = new ArrayList<>(observationReasonDtoCollection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObservationReason(item, OPERATION_UPDATE);
            } else {
                observationJdbcRepository.deleteObservationReason(new ObservationReason(item));
            }
        }
    }

    protected void addActivityId(Long obsUid, Collection<ActIdDto> actIdDtoCollection, boolean updateApplied) throws DataProcessingException {
        if (actIdDtoCollection == null || actIdDtoCollection.isEmpty()) {
            return;
        }

        int maxSegId = updateApplied ? 0 : getMaxSegId(obsUid);

        for (ActIdDto item : actIdDtoCollection) {
            prepareActIdDto(item, obsUid, updateApplied, ++maxSegId);
            actIdJdbcRepository.mergeActId(new ActId(item));
        }
    }

    protected int getMaxSegId(Long obsUid) throws DataProcessingException {
        var res = actIdJdbcRepository.findRecordsByActUid(obsUid);
        if (res == null || res.isEmpty()) {
            return 0;
        }

        return res.stream()
                .mapToInt(ActId::getActIdSeq)
                .max()
                .orElseThrow(() -> new DataProcessingException("List is empty"));
    }

    private void prepareActIdDto(ActIdDto item, Long obsUid, boolean updateApplied, int actIdSeq) {
        item.setItNew(false);
        item.setItDirty(false);
        item.setItDelete(false);
        item.setActUid(obsUid);
        if (!updateApplied) {
            item.setActIdSeq(actIdSeq);
        }
    }


    protected void addObservationInterps(Long obsUid, Collection<ObservationInterpDto> observationInterpDtoCollection)   {
        if (observationInterpDtoCollection != null) {
            ArrayList<ObservationInterpDto> arr = new ArrayList<>(observationInterpDtoCollection);
            for(var item: arr) {
                item.setItNew(false);
                item.setItDirty(false);
                item.setItDelete(false);
                item.setObservationUid(obsUid);
                saveObservationInterp(item, OPERATION_CREATE);
            }
        }
    }

    protected void saveObservationInterp(ObservationInterpDto item, String operation) {
        var reason = new ObservationInterp(item);
        if (operation.equalsIgnoreCase(OPERATION_CREATE)) {
            observationJdbcRepository.insertObservationInterp(reason);
        } else if (operation.equalsIgnoreCase(OPERATION_UPDATE)) {
            observationJdbcRepository.updateObservationInterp(reason);
        }
    }

    protected void updateObservationInterps(Long obsUid, Collection<ObservationInterpDto> collection)   {
        ArrayList<ObservationInterpDto> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObservationInterp(item, OPERATION_UPDATE);
            } else {
                observationJdbcRepository.deleteObservationInterp(new ObservationInterp(item));
            }
        }
    }

    protected void addObsValueCoded(Long obsUid, Collection<ObsValueCodedDto> obsValueCodedDtoCollection)   {
        if (obsValueCodedDtoCollection != null) {
            ArrayList<ObsValueCodedDto> arr = new ArrayList<>(obsValueCodedDtoCollection);
            for(var item: arr) {
                item.setItNew(false);
                item.setItDirty(false);
                item.setItDelete(false);
                item.setObservationUid(obsUid);
                saveObsValueCoded(item, OPERATION_CREATE);
            }
        }

    }

    protected void saveObsValueCoded(ObsValueCodedDto item, String operation) {
        var reason = new ObsValueCoded(item);
        if (operation.equalsIgnoreCase(OPERATION_CREATE)) {
            observationJdbcRepository.insertObsValueCoded(reason);
        } else if (operation.equalsIgnoreCase(OPERATION_UPDATE)) {
            observationJdbcRepository.updateObsValueCoded(reason);
        }
    }

    protected void updateObsValueCoded(Long obsUid, Collection<ObsValueCodedDto> collection)   {
        ArrayList<ObsValueCodedDto> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObsValueCoded(item, OPERATION_UPDATE);

            } else {
                observationJdbcRepository.deleteObsValueCoded(new ObsValueCoded(item));
            }
        }

    }

    protected void addObsValueTxts(Long obsUid, Collection<ObsValueTxtDto> obsValueTxtDtoCollection)   {
        if (obsValueTxtDtoCollection != null)  {
            ArrayList<ObsValueTxtDto> arr = new ArrayList<>(obsValueTxtDtoCollection);
            for(var item: arr) {
                item.setItNew(false);
                item.setItDirty(false);
                item.setItDelete(false);
                item.setObservationUid(obsUid);
                saveObsValueTxt(item, OPERATION_CREATE);
            }
        }
    }

    protected void saveObsValueTxt(ObsValueTxtDto item, String operation) {
        var reason = new ObsValueTxt(item);
        if (operation.equalsIgnoreCase(OPERATION_CREATE)) {
            observationJdbcRepository.insertObsValueTxt(reason);
        } else if (operation.equalsIgnoreCase(OPERATION_UPDATE)) {
            observationJdbcRepository.updateObsValueTxt(reason);
        }
    }

    protected void updateObsValueTxts(Long obsUid, Collection<ObsValueTxtDto> collection)   {
        ArrayList<ObsValueTxtDto> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObsValueTxt(item, OPERATION_UPDATE);

            } else {
                observationJdbcRepository.deleteObsValueTxt(new ObsValueTxt(item));
            }
        }
    }

    protected void addObsValueDates(Long obsUid, Collection<ObsValueDateDto> obsValueDateDtoCollection)   {
        if (obsValueDateDtoCollection != null) {
            ArrayList<ObsValueDateDto> arr = new ArrayList<>(obsValueDateDtoCollection);
            for(var item: arr) {
                item.setItNew(false);
                item.setItDirty(false);
                item.setItDelete(false);
                item.setObservationUid(obsUid);
                saveObsValueDate(item, OPERATION_CREATE);
            }
        }
    }

    protected void saveObsValueDate(ObsValueDateDto item, String operation) {
        var reason = new ObsValueDate(item);
        if (operation.equalsIgnoreCase(OPERATION_CREATE)) {
            observationJdbcRepository.insertObsValueDate(reason);
        } else if (operation.equalsIgnoreCase(OPERATION_UPDATE)) {
            observationJdbcRepository.updateObsValueDate(reason);
        }
    }

    protected void updateObsValueDates(Long obsUid, Collection<ObsValueDateDto> collection)   {
        ArrayList<ObsValueDateDto> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObsValueDate(item, OPERATION_UPDATE);

            } else {
                observationJdbcRepository.deleteObsValueDate(new ObsValueDate(item));
            }
        }
    }

    protected void addObsValueNumeric(Long obsUid, Collection<ObsValueNumericDto> obsValueNumericDtoCollection)   {
        if (obsValueNumericDtoCollection != null) {
            ArrayList<ObsValueNumericDto> arr = new ArrayList<>(obsValueNumericDtoCollection);
            for(var item: arr) {
                item.setItNew(false);
                item.setItDirty(false);
                item.setItDelete(false);
                item.setObservationUid(obsUid);
                saveObsValueNumeric(item, OPERATION_CREATE);
            }
        }
    }

    protected void saveObsValueNumeric(ObsValueNumericDto item, String operation)  {
        var reason = new ObsValueNumeric(item);
        if (operation.equalsIgnoreCase(OPERATION_CREATE)) {
            observationJdbcRepository.insertObsValueNumeric(reason);
        } else if (operation.equalsIgnoreCase(OPERATION_UPDATE)) {
            observationJdbcRepository.updateObsValueNumeric(reason);
        }
    }

    protected void updateObsValueNumerics(Long obsUid, Collection<ObsValueNumericDto> collection)   {
        ArrayList<ObsValueNumericDto> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObsValueNumeric(item, OPERATION_UPDATE);

            } else {
                observationJdbcRepository.deleteObsValueNumeric(new ObsValueNumeric(item));
            }
        }
    }

    protected void addActivityLocatorParticipations(Long obsUid, Collection<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection,
                                                    String operation)   {
        if (activityLocatorParticipationDtoCollection != null) {
            ArrayList<ActivityLocatorParticipationDto> arr = new ArrayList<>(activityLocatorParticipationDtoCollection);
            for(var item: arr) {
                item.setItNew(false);
                item.setItDirty(false);
                item.setItDelete(false);
                item.setActUid(obsUid);
                var reason = new ActLocatorParticipation(item);
                if (operation.equalsIgnoreCase(OPERATION_CREATE)) {
                    actLocatorParticipationJdbcRepository.insertActLocatorParticipation(reason);
                } else if (operation.equalsIgnoreCase(OPERATION_UPDATE)) {
                    actLocatorParticipationJdbcRepository.updateActLocatorParticipation(reason);
                }
            }
        }
    }


}
