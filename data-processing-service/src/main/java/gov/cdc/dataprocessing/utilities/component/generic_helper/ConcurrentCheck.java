package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.constant.elr.DataTables;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import org.springframework.stereotype.Component;

@Component

public class ConcurrentCheck {


    public ConcurrentCheck() {
        // UNIT TEST
    }
    @SuppressWarnings("java:S3776")

    public boolean dataConcurrenceCheck(RootDtoInterface theRootDTInterface, String tableName, Integer existingVersion)
    {
        if(tableName.equalsIgnoreCase("Person"))
        {
            if(theRootDTInterface.getVersionCtrlNbr() == null)
            {
                ((PersonDto)theRootDTInterface).setVersionCtrlNbr(1);
            }
            if(existingVersion == null || existingVersion.equals(theRootDTInterface.getVersionCtrlNbr()))
            {
                return true;
            }else{
                PersonDto newPersonDT= (PersonDto)theRootDTInterface;

                if(existingVersion.equals(theRootDTInterface.getVersionCtrlNbr() - 1) && newPersonDT.isReentrant()) {
                    return true;
                }

            }
        }
        if(tableName.equalsIgnoreCase(DataTables.ORGANIZATION_TABLE))
        {
           // OrganizationDto organizationDT  = organizationRepositoryUtil.loadObject(theRootDTInterface.getUid(), null).getTheOrganizationDto();
            if(theRootDTInterface.getVersionCtrlNbr() == null)
            {
                ((OrganizationDto)theRootDTInterface).setVersionCtrlNbr(1);
            }
            if(existingVersion.equals(theRootDTInterface.getVersionCtrlNbr()))
            {
                return true;
            }
        }
        if(tableName.equalsIgnoreCase("Observation"))
        {
            //ObservationDto observationDT  = observationRepositoryUtil.loadObject(theRootDTInterface.getUid()).getTheObservationDto();
            if(theRootDTInterface.getVersionCtrlNbr() == null)
            {
                ((ObservationDto)theRootDTInterface).setVersionCtrlNbr(1);
            }
            if(existingVersion.equals(theRootDTInterface.getVersionCtrlNbr()))
            {
                return true;
            }
        }
        return false;

    }

}
