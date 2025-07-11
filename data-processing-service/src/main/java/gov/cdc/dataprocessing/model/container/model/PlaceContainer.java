package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.PlaceDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter

public class PlaceContainer extends BaseContainer implements Serializable
{
    private static final long    serialVersionUID                          = 1L;
    protected PlaceDto thePlaceDT                                = new PlaceDto();
    protected Collection<Object> theEntityLocatorParticipationDTCollection = new ArrayList<>();
    protected Collection<Object> theEntityIdDTCollection                   = new ArrayList<>();
    protected Collection<Object> theParticipationDTCollection              = new ArrayList<>();
    protected Collection<Object> theRoleDTCollection;
    private String localIdentifier;

}
