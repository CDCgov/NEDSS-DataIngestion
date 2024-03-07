package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.service.interfaces.IJurisdictionService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

@Service
public class JurisdictionService implements IJurisdictionService {
    private StringBuffer detailError= null;

    public HashMap<Object, Object> resolveLabReportJurisdiction(PersonContainer subject,
                                                                PersonContainer provider,
                                                                OrganizationVO organizationVO,
                                                                OrganizationVO organizationVO2) throws DataProcessingException {

        try {
            Collection<Object> subjectColl = null;
            Collection<Object> providerColl = null;
            Collection<Object> organizationColl = null;
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            detailError = new StringBuffer();
            String jurisdiction =null;
            //TODO: JURISDICTION
            //subjectColl = findJurisdictionForPatient(subject);

            // Check to see the subject size.  Only proceed if the subject size is not greater than 1.
            if (subjectColl.size() <= 1)
            {
                // Check the result to make sure that there is a value for the subject's jurisdiction.
                // If not then go and find the jurisdiction based on the provider
                if (subjectColl.size() == 1)
                {
                    Iterator<Object> iter = subjectColl.iterator();
                    jurisdiction = (String) iter.next();
                    map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
                }
                if (jurisdiction==null && provider!=null)
                {
                    //TODO: JURISDICTION
                    //providerColl = findJurisdictionForProvider(provider);
                    if(!(providerColl.size()==0))

                        // Check to see the provider size.  Only proceed if the provider size is not greater than 1.
                        if (providerColl.size() <= 1)
                        {
                            // Check the result to make sure that there is a value for the provider's jurisdiction.
                            // If not then go and find the jurisdiction based on the provider
                            if (providerColl.size() == 1)
                            {

                                Iterator<Object> iter = providerColl.iterator();
                                jurisdiction = (String) iter.next();
                                map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
                            }

                        }
                }
                if(jurisdiction==null){
                    if (organizationVO != null)
                    {
                        //TODO: JURISDICTION
                        //organizationColl = findJurisdictionForOrganization(organizationVO);
                    }
                    if (organizationColl != null)
                    {

                        // Check to see the organization size.  Only proceed if the organization size is not greater than 1.
                        if (organizationColl.size() <= 1)
                        {
                            // Check the result to make sure that there is a value for the organization's jurisdiction.
                            // If not then go and find the jurisdiction based on the organization
                            if (organizationColl.size() == 1)
                            {

                                Iterator<Object> iter = organizationColl.iterator();
                                jurisdiction = (String) iter.next();
                                map.put(ELRConstant.JURISDICTION_HASHMAP_KEY, jurisdiction);
                            }
                        }
                    }
                }

                if (jurisdiction == null) {
                    organizationColl = null;
                    if (organizationVO2 != null) {
                        //TODO: JURISDICTION
                        //organizationColl = findJurisdictionForOrganization(organizationVO2);
                    }
                    if (organizationColl != null) {

                        // Check to see the organization size. Only proceed if the
                        // organization size is not greater than 1.
                        if (organizationColl.size() <= 1) {
                            // Check the result to make sure that there is a value
                            // for the organization's jurisdiction.
                            // If not then go and find the jurisdiction based on the
                            // organization
                            if (organizationColl.size() == 1) {

                                Iterator<Object> iter = organizationColl.iterator();
                                jurisdiction = (String) iter.next();
                                map.put(ELRConstant.JURISDICTION_HASHMAP_KEY,
                                        jurisdiction);
                            }
                        }
                    }
                }
            }

            detailError= null;
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

}
