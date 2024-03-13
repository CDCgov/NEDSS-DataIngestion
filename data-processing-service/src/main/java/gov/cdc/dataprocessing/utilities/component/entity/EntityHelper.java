package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActivityLocatorParticipationDT;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.utilities.component.PrepareAssocModelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Component
public class EntityHelper {
    private static final Logger logger = LoggerFactory.getLogger(EntityHelper.class);
    private  final PrepareAssocModelHelper prepareAssocModel;

    public EntityHelper(PrepareAssocModelHelper prepareAssocModel) {
        this.prepareAssocModel = prepareAssocModel;
    }

    /**
     * This private method is used to populate the system attributes on the
     * EntityLocatoryParticipation Collection<Object>
     *
     * @param dtCol
     *            Collection<Object>
     *            NBSSecurityObj object
     * @return Collection<Object> EntityLocatoryParticipation collection object
     *         populated with system attributes
     */
    public Collection<EntityLocatorParticipationDto> iterateELPDTForEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> dtCol) throws DataProcessingException {
        Collection<EntityLocatorParticipationDto> retCol = new ArrayList<>();
        Collection<EntityLocatorParticipationDto> collection = new ArrayList<>();
        Iterator<EntityLocatorParticipationDto> anIterator = null;
        collection = dtCol;
        logger.debug("Collection<Object> size before iteration in iterateELPDT " + collection.size());
        try {
            for (anIterator = collection.iterator(); anIterator.hasNext(); ) {
                EntityLocatorParticipationDto elpDT = (EntityLocatorParticipationDto) anIterator.next();
                EntityLocatorParticipationDto assocDTInterface = elpDT;
                logger.debug("Iterating EntityLocatorParticipationDT");
                elpDT = (EntityLocatorParticipationDto) prepareAssocModel.prepareAssocDTForEntityLocatorParticipation(assocDTInterface);
                logger.debug("Came back from PrepareVOUtils");
                retCol.add(elpDT);
            }
        } catch (Exception e) {
            logger.error("EntityControllerEJB.iterateELPDT: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
        logger.debug("Collection<Object> size after iteration in iterateELPDT " + retCol.size());
        return retCol;

    }


    /**
     * This private method is used to populate the system attributes on the
     * RoleDT Collection<Object>
     *
     * @param dtCol
     *            Collection<Object>
     *            NBSSecurityObj object
     * @return Collection<Object> RoleDT collection object populated with system
     *         attributes
     */
    public Collection<RoleDto> iterateRDT(Collection<RoleDto> dtCol) throws DataProcessingException {
        Collection<RoleDto> retCol = new ArrayList<>();
        Collection<RoleDto> collection = new ArrayList<>();
        Iterator<RoleDto> anIterator = null;
        collection = dtCol;
        logger.debug("Collection<Object> size before iteration in iterateRDT " + collection.size());
        if (collection != null) {
            try {
                for (anIterator = collection.iterator(); anIterator.hasNext();) {
                    RoleDto rDT = (RoleDto) anIterator.next();
                    if (rDT.isItDirty() || rDT.isItNew() || rDT.isItDelete()) {
                        logger.debug("EntityController:rdT.IsItDelete"
                                + rDT.isItDelete() + "rdt.IsItNew:"
                                + rDT.isItNew() + "rdt.IsItDirty:"
                                + rDT.isItDirty());
                        RoleDto assocDTInterface = rDT;
                        rDT = (RoleDto) prepareAssocModel.prepareAssocDTForRole(assocDTInterface);
                        retCol.add(rDT);
                    }
                }
            } catch (Exception e) {
                logger.error("EntityControllerEJB.iterateRDT: " + e.getMessage(), e);
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        logger.debug("Collection<Object> size after iteration in iterateRDT " + retCol.size());
        return retCol;

    }



    /**
     * This private method is used to populate the system attributes on the
     * ParticipationDT Collection<Object>
     *
     * @param dtCol
     *            Collection<Object>
     *            NBSSecurityObj object
     * @return Collection<Object> ParticipationDT collection object populated
     *         with system attributes
     */

    public Collection<ParticipationDT> iteratePDTForParticipation(Collection<ParticipationDT> dtCol) throws DataProcessingException {
        Collection<ParticipationDT> retCol = new ArrayList<>();
        Collection<ParticipationDT> collection;
        Iterator<ParticipationDT> anIterator;
        collection = dtCol;
        if (!collection.isEmpty()) {
            for (anIterator = collection.iterator(); anIterator.hasNext();) {
                ParticipationDT pDT = anIterator.next();
                if (pDT.isItDirty() || pDT.isItNew() || pDT.isItDelete()) {
                    ParticipationDT assocDTInterface = pDT;
                    pDT = prepareAssocModel.prepareAssocDTForParticipation(assocDTInterface);
                    retCol.add(pDT);
                }
            }
        }
        return retCol;
    }


    public Collection<ActivityLocatorParticipationDT> iterateActivityParticipation(Collection<ActivityLocatorParticipationDT> dtCol) throws DataProcessingException {

        Collection<ActivityLocatorParticipationDT> retCol = new ArrayList<> ();
        Collection<ActivityLocatorParticipationDT> collection;
        collection = dtCol;

        Iterator<ActivityLocatorParticipationDT> anIterator;

        if (collection != null)
        {
            for (anIterator = collection.iterator(); anIterator.hasNext();)
            {

                ActivityLocatorParticipationDT alpDT = anIterator.next();
                ActivityLocatorParticipationDT assocDTInterface = alpDT;
                alpDT = prepareAssocModel.prepareActivityLocatorParticipationDT(assocDTInterface);
                retCol.add(alpDT);
            }
        }

        return retCol;
    }

    public Collection<ActRelationshipDT> iterateActRelationship(Collection<ActRelationshipDT> dtCol) throws DataProcessingException {

        Collection<ActRelationshipDT> retCol = new ArrayList<> ();
        Collection<ActRelationshipDT> collection;
        Iterator<ActRelationshipDT> anIterator;
        collection = dtCol;
        if (collection != null)
        {
            for (anIterator = collection.iterator(); anIterator.hasNext();)
            {
                ActRelationshipDT arDT = anIterator.next();
                ActRelationshipDT assocDTInterface = arDT;
                if(arDT.isItDirty() || arDT.isItNew() || arDT.isItDelete())
                {
                    arDT = prepareAssocModel.prepareActRelationshipDT(assocDTInterface);
                    retCol.add(arDT);
                }
            }
        }

        return retCol;
    }

}
