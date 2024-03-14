package gov.cdc.dataprocessing.utilities.component.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.implementation.core.OdseIdGeneratorService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ObservationRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(ObservationRepositoryUtil.class);

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
    private final OdseIdGeneratorService odseIdGeneratorService;

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
                                     OdseIdGeneratorService odseIdGeneratorService, ActRepository actRepository) {
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
        this.actRepository = actRepository;
    }


    public ObservationVO loadObject(long obUID) throws DataProcessingException
    {
        ObservationVO obVO;
        try{
            obVO = new ObservationVO();

            /**
             *  Selects ObservationDT object
             */

            ObservationDT obDT = selectObservation(obUID);
            obVO.setTheObservationDT(obDT);
            /**
             * Selects ObservationReasonDT List
             */

            Collection<ObservationReasonDT> obReasonColl = selectObservationReasons(obUID);
            obVO.setTheObservationReasonDTCollection(obReasonColl);

            /**
             * Selects ActityIdDT collection
             */

            Collection<ActIdDto> idColl = selectActivityIDs(obUID);
            obVO.setTheActIdDtoCollection(idColl);

            /**
             * Selects ObservationInterpDT collection
             */

            Collection<ObservationInterpDT> obInterpColl = selectObservationInterps(obUID);
            obVO.setTheObservationInterpDTCollection(obInterpColl);

            /**
             * Selects ObsValueCodedDT collection
             */

            Collection<ObsValueCodedDT> obsValueCodedColl = selectObsValueCodeds(obUID);
            obVO.setTheObsValueCodedDTCollection(obsValueCodedColl);

            /**
             * Selects ObsValueTxtDT collection
             */

            Collection<ObsValueTxtDT> obsValueTxtColl = selectObsValueTxts(obUID);
            obVO.setTheObsValueTxtDTCollection(obsValueTxtColl);

            /**
             * Selects ObsValueDateDT collection
             */

            Collection<ObsValueDateDT> obsValueDateColl = selectObsValueDates(obUID);
            obVO.setTheObsValueDateDTCollection(obsValueDateColl);

            /**
             * Selects ObsValueNumericDT collection
             */

            Collection<ObsValueNumericDT> obsValueNumericColl = selectObsValueNumerics(obUID);
            obVO.setTheObsValueNumericDTCollection(obsValueNumericColl);

            /**
             * Selects ActivityLocatorParticipationDT collection
             */

            Collection<ActivityLocatorParticipationDT> activityLocatorParticipationColl = selectActivityLocatorParticipations(obUID);
            obVO.setTheActivityLocatorParticipationDTCollection(activityLocatorParticipationColl);

            //Selects ActRelationshiopDTcollection
            Collection<ActRelationshipDT> actColl = selectActRelationshipDTCollection(obUID);
            obVO.setTheActRelationshipDTCollection(actColl);

            //SelectsParticipationDTCollection
            Collection<ParticipationDT> parColl = selectParticipationDTCollection(obUID);
            obVO.setTheParticipationDTCollection(parColl);

            obVO.setItNew(false);
            obVO.setItDirty(false);
            return obVO;
        }catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
    }

    @Transactional
    public Long saveObservation(ObservationVO observationVO) throws DataProcessingException {
        Long observationUid = -1L;


        try {
            Observation observation = null;

            Collection<ActivityLocatorParticipationDT> alpDTCol = observationVO.getTheActivityLocatorParticipationDTCollection();
            Collection<ActRelationshipDT> arDTCol = observationVO.getTheActRelationshipDTCollection();
            Collection<ParticipationDT> pDTCol = observationVO.getTheParticipationDTCollection();
            Collection<ActRelationshipDT> colAct = null;
            Collection<ParticipationDT> colParticipation = null;
            Collection<ActivityLocatorParticipationDT> colActLocatorParticipation = null;


            if (alpDTCol != null)
            {
                colActLocatorParticipation = entityHelper.iterateActivityParticipation(alpDTCol);
                observationVO.setTheActivityLocatorParticipationDTCollection(colActLocatorParticipation);
            }

            if (arDTCol != null)
            {
                colAct = entityHelper.iterateActRelationship(arDTCol);
                observationVO.setTheActRelationshipDTCollection(colAct);
            }

            if (pDTCol != null)
            {
                colParticipation = entityHelper.iteratePDTForParticipation(pDTCol);
                observationVO.setTheParticipationDTCollection(colParticipation);
            }

            if (observationVO.isItNew())
            {
                //observation = home.create(observationVO);
                var obsUid =  createNewObservation(observationVO);
                observationUid = obsUid;
            }
            else
            {
                if (observationVO.getTheObservationDT() != null) // make sure it is not null
                {
                    updateObservation(observationVO);
                    observationUid = observationVO.getTheObservationDT().getObservationUid();
                }
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return observationUid;

    }

    @Transactional
    public Long createNewObservation(ObservationVO observationVO) throws DataProcessingException {
        try {
            Long obsId = saveNewObservation(observationVO.getTheObservationDT());
            observationVO.getTheObservationDT().setItNew(false);
            observationVO.getTheObservationDT().setItDirty(false);

            addObservationReasons(obsId, observationVO.getTheObservationReasonDTCollection());
            addActivityId(obsId, observationVO.getTheActIdDtoCollection(), false);
            addObservationInterps(obsId, observationVO.getTheObservationInterpDTCollection());
            addObsValueCoded(obsId, observationVO.getTheObsValueCodedDTCollection());
            addObsValueTxts(obsId, observationVO.getTheObsValueTxtDTCollection());
            addObsValueDates(obsId, observationVO.getTheObsValueDateDTCollection());
            addObsValueNumeric(obsId, observationVO.getTheObsValueNumericDTCollection());
            addActivityLocatorParticipations(obsId, observationVO.getTheActivityLocatorParticipationDTCollection());
            return obsId;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    @Transactional
    public Long updateObservation(ObservationVO observationVO) throws DataProcessingException {
        Long uid = -1L;
        if (observationVO.getTheObservationDT().getObservationUid() == null) {
            uid = saveNewObservation(observationVO.getTheObservationDT());
            observationVO.getTheObservationDT().setItNew(false);
            observationVO.getTheObservationDT().setItDirty(false);
        } else {
            uid = saveObservation(observationVO.getTheObservationDT());
            observationVO.getTheObservationDT().setItNew(false);
            observationVO.getTheObservationDT().setItDirty(false);
        }

        updateObservationReason(uid, observationVO.getTheObservationReasonDTCollection());
        addActivityId(uid, observationVO.getTheActIdDtoCollection(), true);
        updateObservationInterps(uid, observationVO.getTheObservationInterpDTCollection());
        updateObsValueCoded(uid, observationVO.getTheObsValueCodedDTCollection());
        updateObsValueTxts(uid, observationVO.getTheObsValueTxtDTCollection());
        updateObsValueDates(uid, observationVO.getTheObsValueDateDTCollection());
        updateObsValueNumerics(uid, observationVO.getTheObsValueNumericDTCollection());
        addActivityLocatorParticipations(uid, observationVO.getTheActivityLocatorParticipationDTCollection());
        return uid;
    }

    private ObservationDT selectObservation(long obUID) throws  DataProcessingException {
        try {
            // QUERY OBS
            var result = observationRepository.findById(obUID);
            if (result.isPresent()) {
                ObservationDT item = new ObservationDT(result.get());
                item.setItNew(false);
                item.setItDirty(false);
                return  item;
            } else {
                throw new DataProcessingException("NO OBS FOUND");
            }

        } catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
        }

    }

    private Collection<ObservationReasonDT> selectObservationReasons(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObservationReason> observationReasons = observationReasonRepository.findRecordsById(aUID);
            Collection<ObservationReasonDT> dtCollection = new ArrayList<>();
            for (var observationReason : observationReasons) {
                ObservationReasonDT dt = new ObservationReasonDT(observationReason);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
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
            throw new DataProcessingException(ndapex.toString());
        }

    }

    private Collection<ObservationInterpDT> selectObservationInterps(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObservationInterp> col = observationInterpRepository.findRecordsById(aUID);
            Collection<ObservationInterpDT> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObservationInterpDT dt = new ObservationInterpDT(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ObsValueCodedDT> selectObsValueCodeds(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueCoded> col = obsValueCodedRepository.findRecordsById(aUID);
            Collection<ObsValueCodedDT> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueCodedDT dt = new ObsValueCodedDT(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ObsValueTxtDT> selectObsValueTxts(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueTxt> col = obsValueTxtRepository.findRecordsById(aUID);
            Collection<ObsValueTxtDT> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueTxtDT dt = new ObsValueTxtDT(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ObsValueDateDT> selectObsValueDates(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueDate> col = obsValueDateRepository.findRecordsById(aUID);
            Collection<ObsValueDateDT> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueDateDT dt = new ObsValueDateDT(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ObsValueNumericDT> selectObsValueNumerics(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueNumeric> col = obsValueNumericRepository.findRecordsById(aUID);
            Collection<ObsValueNumericDT> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueNumericDT dt = new ObsValueNumericDT(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ActivityLocatorParticipationDT> selectActivityLocatorParticipations(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ActLocatorParticipation> col = actLocatorParticipationRepository.findRecordsById(aUID);
            Collection<ActivityLocatorParticipationDT> dtCollection = new ArrayList<>();
            for (var item : col) {
                ActivityLocatorParticipationDT dt = new ActivityLocatorParticipationDT(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ActRelationshipDT> selectActRelationshipDTCollection(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ActRelationship> col = actRelationshipRepository.findRecordsById(aUID);
            Collection<ActRelationshipDT> dtCollection = new ArrayList<>();
            for (var item : col) {
                ActRelationshipDT dt = new ActRelationshipDT(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ParticipationDT> selectParticipationDTCollection(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<Participation> col = participationRepository.findRecordsById(aUID);
            Collection<ParticipationDT> dtCollection = new ArrayList<>();
            for (var item : col) {
                ParticipationDT dt = new ParticipationDT(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Long saveNewObservation(ObservationDT observationDT) throws DataProcessingException {
        try {
            var uid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.OBSERVATION);

            Act act = new Act();
            act.setActUid(uid.getSeedValueNbr());
            act.setClassCode(NEDSSConstant.OBSERVATION_CLASS_CODE);
            act.setMoodCode(NEDSSConstant.EVENT_MOOD_CODE);

            actRepository.save(act);

            //TODO EVALUATE
            // Local uid
            observationDT.setSharedInd("T");
            Observation observation = new Observation(observationDT);
            observation.setVersionCtrlNbr(1);
            observation.setLocalId(uid.getUidPrefixCd() + uid.getSeedValueNbr() + uid.getUidSuffixCd());
            observation.setObservationUid(uid.getSeedValueNbr());

            observationRepository.save(observation);
            return uid.getSeedValueNbr();
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private Long saveObservation(ObservationDT observationDT) {
        Observation observation = new Observation(observationDT);
        observationRepository.save(observation);
        return observation.getObservationUid();
    }

    // private void insertObservationReasons(ObservationVO obVO) throws  NEDSSSystemException
    private void  addObservationReasons(Long obsUid, Collection<ObservationReasonDT> observationReasonDTCollection) throws DataProcessingException {
        try {
            if (observationReasonDTCollection != null) {
                ArrayList<ObservationReasonDT> arr = new ArrayList<>(observationReasonDTCollection);
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

    private void saveObservationReason(ObservationReasonDT item) {
        var data = new ObservationReason(item);
        observationReasonRepository.save(data);
    }

    private void updateObservationReason(Long obsUid, Collection<ObservationReasonDT> observationReasonDTCollection) throws DataProcessingException {
        try {
            ArrayList<ObservationReasonDT> arr = new ArrayList<>(observationReasonDTCollection);
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

    private void addObservationInterps(Long obsUid, Collection<ObservationInterpDT> observationInterpDTCollection) throws DataProcessingException {
        try {
            if (observationInterpDTCollection != null) {
                ArrayList<ObservationInterpDT> arr = new ArrayList<>(observationInterpDTCollection);
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

    private void saveObservationInterp(ObservationInterpDT item) {
        var reason = new ObservationInterp(item);
        observationInterpRepository.save(reason);
    }

    private void updateObservationInterps(Long obsUid, Collection<ObservationInterpDT> collection) throws DataProcessingException {
        try {
            ArrayList<ObservationInterpDT> arr = new ArrayList<>(collection);
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

    private void addObsValueCoded(Long obsUid, Collection<ObsValueCodedDT> obsValueCodedDTCollection) throws DataProcessingException {
        try {
            if (obsValueCodedDTCollection != null) {
                ArrayList<ObsValueCodedDT> arr = new ArrayList<>(obsValueCodedDTCollection);
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

    private void saveObsValueCoded(ObsValueCodedDT item) {
        var reason = new ObsValueCoded(item);
        obsValueCodedRepository.save(reason);
    }

    private void updateObsValueCoded(Long obsUid, Collection<ObsValueCodedDT> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueCodedDT> arr = new ArrayList<>(collection);
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

    private void addObsValueTxts(Long obsUid, Collection<ObsValueTxtDT> obsValueTxtDTCollection) throws DataProcessingException {
        try {
            if (obsValueTxtDTCollection != null)  {
                ArrayList<ObsValueTxtDT> arr = new ArrayList<>(obsValueTxtDTCollection);
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

    private void saveObsValueTxt(ObsValueTxtDT item) {
        var reason = new ObsValueTxt(item);
        obsValueTxtRepository.save(reason);
    }

    private void updateObsValueTxts(Long obsUid, Collection<ObsValueTxtDT> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueTxtDT> arr = new ArrayList<>(collection);
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

    private void addObsValueDates(Long obsUid, Collection<ObsValueDateDT> obsValueDateDTCollection) throws DataProcessingException {
        try {
            if (obsValueDateDTCollection != null) {
                ArrayList<ObsValueDateDT> arr = new ArrayList<>(obsValueDateDTCollection);
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

    private void saveObsValueDate(ObsValueDateDT item) {
        var reason = new ObsValueDate(item);
        obsValueDateRepository.save(reason);
    }

    private void updateObsValueDates(Long obsUid, Collection<ObsValueDateDT> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueDateDT> arr = new ArrayList<>(collection);
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

    private void addObsValueNumeric(Long obsUid, Collection<ObsValueNumericDT> obsValueNumericDTCollection) throws DataProcessingException {
        try {
            if (obsValueNumericDTCollection != null) {
                ArrayList<ObsValueNumericDT> arr = new ArrayList<>(obsValueNumericDTCollection);
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

    private void saveObsValueNumeric(ObsValueNumericDT item)  {
        var reason = new ObsValueNumeric(item);
        obsValueNumericRepository.save(reason);
    }

    private void updateObsValueNumerics(Long obsUid, Collection<ObsValueNumericDT> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueNumericDT> arr = new ArrayList<>(collection);
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

    private void addActivityLocatorParticipations(Long obsUid, Collection<ActivityLocatorParticipationDT> activityLocatorParticipationDTCollection) throws DataProcessingException {
        try {
            if (activityLocatorParticipationDTCollection != null) {
                ArrayList<ActivityLocatorParticipationDT> arr = new ArrayList<>(activityLocatorParticipationDTCollection);
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
