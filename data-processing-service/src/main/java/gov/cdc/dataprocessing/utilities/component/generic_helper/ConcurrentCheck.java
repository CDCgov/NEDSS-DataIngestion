package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.constant.elr.DataTables;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import org.springframework.stereotype.Component;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class ConcurrentCheck {


    public ConcurrentCheck() {
        // UNIT TEST
    }
    @SuppressWarnings("java:S3776")

    public boolean dataConcurrenceCheck(RootDtoInterface theRootDTInterface, String tableName, Integer existingVersion) throws DataProcessingException
    {

        try
        {
            if(tableName.equalsIgnoreCase("Person"))
            {
               // PersonDto personDT  = patientRepositoryUtil.loadPerson(theRootDTInterface.getUid()).getThePersonDto();
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
//            if(tableName.equalsIgnoreCase("Notification"))
//            {
//                NotificationDAOImpl nDao = new NotificationDAOImpl();
//                NotificationDT notificationDT  = (NotificationDT)nDao.loadObject(theRootDTInterface.getUid().longValue());
//                logger.debug("notificationDT!!!!" +notificationDT);
//                logger.debug("theRootDTInterface!!!!" +theRootDTInterface);
//                logger.debug("notificationDT version control number :" + notificationDT.getVersionCtrlNbr());
//                logger.debug("theRootDTInterface.notificationDT version control number :" + theRootDTInterface.getVersionCtrlNbr());
//                if(theRootDTInterface.getVersionCtrlNbr() == null)
//                    ((NotificationDT)theRootDTInterface).setVersionCtrlNbr(new Integer(1));
//                if(notificationDT.getVersionCtrlNbr().equals(theRootDTInterface.getVersionCtrlNbr()))
//                {
//                    return true;
//                }
//            }
//            if(tableName.equalsIgnoreCase("Public_Health_Case"))
//            {
//                PublicHealthCaseDAOImpl pDao = new PublicHealthCaseDAOImpl();
//                PublicHealthCaseDto phcDT  = (PublicHealthCaseDto)pDao.loadObject(theRootDTInterface.getUid().longValue());
//                if(theRootDTInterface.getVersionCtrlNbr() == null)
//                    ((PublicHealthCaseDto)theRootDTInterface).setVersionCtrlNbr(new Integer(1));
//                PublicHealthCaseDto newPhcDT= (PublicHealthCaseDto)theRootDTInterface;
//                 if(phcDT.getVersionCtrlNbr().equals(theRootDTInterface.getVersionCtrlNbr())
//                 || (phcDT.getVersionCtrlNbr().equals(newPhcDT.getVersionCtrlNbr()-1) && newPhcDT.isReentrant()))*/
//                if(phcDT.getVersionCtrlNbr().equals(theRootDTInterface.getVersionCtrlNbr()) )
//                {
//                    return true;
//                }
//            }
//            if(tableName.equalsIgnoreCase("Material"))
//            {
//                MaterialDAOImpl mDao = new MaterialDAOImpl();
//                MaterialDT materialDT  = (MaterialDT)mDao.loadObject(theRootDTInterface.getUid().longValue());
//                if(theRootDTInterface.getVersionCtrlNbr() == null)
//                    ((MaterialDT)theRootDTInterface).setVersionCtrlNbr(new Integer(1));
//                if(materialDT.getVersionCtrlNbr().equals(theRootDTInterface.getVersionCtrlNbr()))
//                {
//                    return true;
//                }
//            }
//            if(tableName.equalsIgnoreCase(DataTables.INTERVENTION_TABLE) )
//            {
//                InterventionDAOImpl pDao = new InterventionDAOImpl();
//                InterventionDto interventionDT  = (InterventionDto)pDao.loadObject(theRootDTInterface.getUid().longValue());
//                if(theRootDTInterface.getVersionCtrlNbr() == null)
//                    ((InterventionDto)theRootDTInterface).setVersionCtrlNbr(new Integer(1));
//                if(interventionDT.getVersionCtrlNbr().equals(theRootDTInterface.getVersionCtrlNbr()))
//                {
//                    return true;
//                }
//            }
//            if(tableName.equalsIgnoreCase(DataTables.TREATMENT_TABLE) )
//            {
//                TreatmentDAOImpl pDao = new TreatmentDAOImpl();
//                TreatmentDT treatmentDT  = (TreatmentDT)pDao.loadObject(theRootDTInterface.getUid().longValue());
//                if(theRootDTInterface.getVersionCtrlNbr() == null)
//                    ((TreatmentDT)theRootDTInterface).setVersionCtrlNbr(new Integer(1));
//                if(treatmentDT.getVersionCtrlNbr().equals(theRootDTInterface.getVersionCtrlNbr()))
//                {
//                    return true;
//                }
//            }
//            if(tableName.equalsIgnoreCase("NBS_document"))
//            {
//                NbsDocumentDAOImpl nbsDocDao = new NbsDocumentDAOImpl();
//                NBSDocumentDT nbsDT  = (NBSDocumentDT)nbsDocDao.loadObject(theRootDTInterface.getUid().longValue());
//                if(theRootDTInterface.getVersionCtrlNbr() == null)
//                    ((NBSDocumentDT)theRootDTInterface).setVersionCtrlNbr(new Integer(1));
//                if(nbsDT.getVersionCtrlNbr().equals(theRootDTInterface.getVersionCtrlNbr()))
//                {
//                    return true;
//                }
//            }
//            if(tableName.equalsIgnoreCase(DataTables.CT_CONTACT))
//            {
//                CTContactDAOImpl cTContactDAOImpl = new CTContactDAOImpl();
//                CTContactDT cTContactDT  = (CTContactDT)cTContactDAOImpl.loadObject(theRootDTInterface.getUid().longValue());
//                if(theRootDTInterface.getVersionCtrlNbr() == null)
//                    ((CTContactDT)theRootDTInterface).setVersionCtrlNbr(new Integer(1));
//                if(cTContactDT.getVersionCtrlNbr().equals(theRootDTInterface.getVersionCtrlNbr()))
//                {
//                    return true;
//                }
//            }

            return false;
        }
        catch(Exception e)
        {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

}
