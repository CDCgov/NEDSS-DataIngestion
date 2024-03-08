package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
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
                                     ParticipationRepository participationRepository) {
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

            Collection<ActIdDT> idColl = selectActivityIDs(obUID);
            obVO.setTheActIdDTCollection(idColl);

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

    private Collection<ActIdDT> selectActivityIDs(long aUID) throws  DataProcessingException
    {
        try
        {
            Collection<ActId> col = actIdRepository.findRecordsById(aUID);
            Collection<ActIdDT> dtCollection = new ArrayList<>();
            for (var item : col) {
                ActIdDT dt = new ActIdDT(item);
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
    }//end of selectObservationInterps()

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
    }//end of selectObsValueCodeds()

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
    }//end of selectObsValueTxts()

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
    }//end of selectObsValueDates()

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
    }//end of selectObsValueNumerics()

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
    }//end of selectActivityLocatorParticipations()


    //get collection of ActRelationship from ActRelationshipDAOImpl entered by John Park
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


    //get collection of Participation  from ParticipationDAOImpl entered by John Park
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


    
    
}
