package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.springframework.stereotype.Service;

@Service
public class NokMatchingBaseService extends PatientMatchingBaseService {

    public NokMatchingBaseService(EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
                                  EntityHelper entityHelper, PatientRepositoryUtil patientRepositoryUtil,
                                  CachingValueService cachingValueService,
                                  PrepareAssocModelHelper prepareAssocModelHelper) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService, prepareAssocModelHelper);
    }


}
