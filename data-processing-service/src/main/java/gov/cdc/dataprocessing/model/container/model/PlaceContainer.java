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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class PlaceContainer extends BaseContainer implements Serializable
{
    private static final long    serialVersionUID                          = 1L;
    protected PlaceDto thePlaceDT                                = new PlaceDto();
    protected Collection<Object> theEntityLocatorParticipationDTCollection = new ArrayList<Object>();
    protected Collection<Object> theEntityIdDTCollection                   = new ArrayList<Object>();
    protected Collection<Object> theParticipationDTCollection              = new ArrayList<Object>();
    protected Collection<Object> theRoleDTCollection;
    private String localIdentifier;

}
