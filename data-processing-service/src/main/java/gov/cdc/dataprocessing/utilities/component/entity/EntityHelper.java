package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
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
        Collection<EntityLocatorParticipationDto> collection;
        Iterator<EntityLocatorParticipationDto> anIterator;
        collection = dtCol;
        logger.debug("Collection<Object> size before iteration in iterateELPDT {}", collection.size());
        for (anIterator = collection.iterator(); anIterator.hasNext(); ) {
            EntityLocatorParticipationDto elpDT =  anIterator.next();
            EntityLocatorParticipationDto assocDTInterface = elpDT;
            logger.debug("Iterating EntityLocatorParticipationDT");
            elpDT =  prepareAssocModel.prepareAssocDTForEntityLocatorParticipation(assocDTInterface);
            logger.debug("Came back from PrepareVOUtils");
            retCol.add(elpDT);
        }

        logger.debug("Collection<Object> size after iteration in iterateELPDT {}", retCol.size());
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
        Collection<RoleDto> collection;
        Iterator<RoleDto> anIterator ;
        collection = dtCol;
        logger.debug("Collection<Object> size before iteration in iterateRDT {}", collection.size());
        for (anIterator = collection.iterator(); anIterator.hasNext(); ) {
            RoleDto rDT = anIterator.next();
            if (rDT.isItDirty() || rDT.isItNew() || rDT.isItDelete()) {
                logger.debug("EntityController:rdT.IsItDelete {} rdt.IsItNew: {} rdt.IsItDirty: {}", rDT.isItDelete(), rDT.isItNew(), rDT.isItDirty());
                RoleDto assocDTInterface = rDT;
                rDT = prepareAssocModel.prepareAssocDTForRole(assocDTInterface);
                retCol.add(rDT);
            }
        }

        logger.debug("Collection<Object> size after iteration in iterateRDT {}", retCol.size());
        return retCol;

    }



    /**
     * This private method is used to populate the system attributes on the
     * ParticipationDto Collection<Object>
     *
     * @param dtCol
     *            Collection<Object>
     *            NBSSecurityObj object
     * @return Collection<Object> ParticipationDto collection object populated
     *         with system attributes
     */

    public Collection<ParticipationDto> iteratePDTForParticipation(Collection<ParticipationDto> dtCol) throws DataProcessingException {
        Collection<ParticipationDto> retCol = new ArrayList<>();
        Collection<ParticipationDto> collection;
        Iterator<ParticipationDto> anIterator;
        collection = dtCol;
        if (!collection.isEmpty()) {
            for (anIterator = collection.iterator(); anIterator.hasNext();) {
                ParticipationDto pDT = anIterator.next();
                if (pDT.isItDirty() || pDT.isItNew() || pDT.isItDelete()) {
                    ParticipationDto assocDTInterface = pDT;
                    pDT = prepareAssocModel.prepareAssocDTForParticipation(assocDTInterface);
                    retCol.add(pDT);
                }
            }
        }
        return retCol;
    }


    public Collection<ActivityLocatorParticipationDto> iterateActivityParticipation(Collection<ActivityLocatorParticipationDto> dtCol) throws DataProcessingException {

        Collection<ActivityLocatorParticipationDto> retCol = new ArrayList<> ();
        Collection<ActivityLocatorParticipationDto> collection;
        collection = dtCol;

        Iterator<ActivityLocatorParticipationDto> anIterator;

        if (collection != null)
        {
            for (anIterator = collection.iterator(); anIterator.hasNext();)
            {

                ActivityLocatorParticipationDto alpDT = anIterator.next();
                ActivityLocatorParticipationDto assocDTInterface = alpDT;
                alpDT = prepareAssocModel.prepareActivityLocatorParticipationDT(assocDTInterface);
                retCol.add(alpDT);
            }
        }

        return retCol;
    }

    public Collection<ActRelationshipDto> iterateActRelationship(Collection<ActRelationshipDto> dtCol) throws DataProcessingException {

        Collection<ActRelationshipDto> retCol = new ArrayList<> ();
        Collection<ActRelationshipDto> collection;
        Iterator<ActRelationshipDto> anIterator;
        collection = dtCol;
        if (collection != null)
        {
            for (anIterator = collection.iterator(); anIterator.hasNext();)
            {
                ActRelationshipDto arDT = anIterator.next();
                ActRelationshipDto assocDTInterface = arDT;
                if(arDT.isItDirty() || arDT.isItNew() || arDT.isItDelete())
                {
                    arDT = prepareAssocModel.prepareActRelationshipDT(assocDTInterface);
                    retCol.add(arDT);
                }
            }
        }

        return retCol;
    }

    public Collection<ActivityLocatorParticipationDto> iterateALPDTActivityLocatorParticipation(Collection<ActivityLocatorParticipationDto> dtCol) throws DataProcessingException {

        Collection<ActivityLocatorParticipationDto> retCol = new ArrayList<> ();
        Collection<ActivityLocatorParticipationDto> collection;
        collection = dtCol;

        Iterator<ActivityLocatorParticipationDto> anIterator;

        if (collection != null)
        {
            for (anIterator = collection.iterator(); anIterator.hasNext();)
            {

                ActivityLocatorParticipationDto alpDT = anIterator.next();
                alpDT = prepareAssocModel.prepareAssocDTForActivityLocatorParticipation(alpDT);
                retCol.add(alpDT);
            }
        }

        return retCol;
    }


    public Collection<ActRelationshipDto> iterateARDTActRelationship(Collection<ActRelationshipDto> dtCol) throws DataProcessingException {

        Collection<ActRelationshipDto> retCol = new ArrayList<> ();
        Collection<ActRelationshipDto> collection;
        Iterator<ActRelationshipDto> anIterator;
        collection = dtCol;
        if (collection != null)
        {
            for (anIterator = collection.iterator(); anIterator.hasNext();)
            {
                ActRelationshipDto arDT = anIterator.next();
                if(arDT.isItDirty() || arDT.isItNew() || arDT.isItDelete())
                {
                    arDT = prepareAssocModel.prepareAssocDTForActRelationship(arDT);
                    retCol.add(arDT);
                }
            }
        }

        return retCol;
    }

}
