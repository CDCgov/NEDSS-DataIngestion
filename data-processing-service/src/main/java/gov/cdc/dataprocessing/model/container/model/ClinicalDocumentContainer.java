package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.ClinicalDocumentDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter

public class ClinicalDocumentContainer extends BaseContainer implements Serializable
{
    private static final long serialVersionUID = 1L;
    public ClinicalDocumentDto theClinicalDocumentDT = new ClinicalDocumentDto();
    public Collection<Object> theActivityLocatorParticipationDTCollection;
    public Collection<Object> theActIdDTCollection;
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theActRelationshipDTCollection;

}
