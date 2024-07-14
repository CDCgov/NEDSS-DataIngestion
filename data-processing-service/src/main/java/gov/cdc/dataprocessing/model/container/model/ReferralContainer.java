package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.ReferralDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@SuppressWarnings("all")
public class ReferralContainer extends BaseContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    // private boolean itDirty = false;
    // private boolean itNew = true;
    // private boolean itDelete = false;

    public ReferralDto theReferralDT = new ReferralDto();
    public Collection<Object> theActivityLocatorParticipationDTCollection;
    public Collection<Object> theActIdDTCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theActRelationshipDTCollection;
}
