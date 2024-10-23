package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.ProviderDataForPrintContainer;
import gov.cdc.dataprocessing.model.container.model.ResultedTestSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.UidSummaryContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.Observation_SummaryRepository;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationSummaryService;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static gov.cdc.dataprocessing.constant.ComplexQueries.ASSOCIATED_INV_QUERY;
import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.AND_UPPERCASE;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class ObservationSummaryService implements IObservationSummaryService {

    private final Observation_SummaryRepository observationSummaryRepository;
    private final CustomRepository customRepository;
    private final QueryHelper queryHelper;

    public ObservationSummaryService(Observation_SummaryRepository observationSummaryRepository,
                                     CustomRepository customRepository, QueryHelper queryHelper) {
        this.observationSummaryRepository = observationSummaryRepository;
        this.customRepository = customRepository;
        this.queryHelper = queryHelper;
    }

    public Collection<UidSummaryContainer> findAllActiveLabReportUidListForManage(Long investigationUid, String whereClause) throws DataProcessingException {

        Collection<UidSummaryContainer>  uidSummaryVOCollection  = new ArrayList<>();
        try{
            var obsSums = observationSummaryRepository.findAllActiveLabReportUidListForManage(investigationUid, whereClause);

            for(var item : obsSums) {
                UidSummaryContainer container = new UidSummaryContainer();
                container.setUid(item.getUid());
                container.setAddTime(item.getAddTime());
                container.setAddReasonCd(item.getAddReasonCd());
                uidSummaryVOCollection.add(container);
            }

        }catch(Exception ex){
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return uidSummaryVOCollection;
    }


    public Map<Object,Object> getLabParticipations(Long observationUID) throws DataProcessingException {
        Map<Object,Object> vals;
        try {

            vals = customRepository.getLabParticipations(observationUID);

        } catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return vals;
    }


    public ArrayList<Object>  getPatientPersonInfo(Long observationUID) throws DataProcessingException
    {
        ArrayList<Object> vals;
        try
        {
            vals = customRepository.getPatientPersonInfo(observationUID);
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.getMessage(), ex);
        }


        return vals;
    }


    public ArrayList<Object>  getProviderInfo(Long observationUID,String partTypeCd) throws DataProcessingException
    {
        ArrayList<Object> orderProviderInfo;
        try
        {
           orderProviderInfo = customRepository.getProviderInfo(observationUID, partTypeCd);
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.getMessage(), ex);
        }

        return orderProviderInfo;
    }

    public ArrayList<Object>  getActIdDetails(Long observationUID) throws DataProcessingException
    {
        ArrayList<Object> actIdDetails;
        try
        {
            actIdDetails = customRepository.getActIdDetails(observationUID);
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.getMessage(), ex);
        }

        return actIdDetails;
    }

    public String getReportingFacilityName(Long organizationUid) throws DataProcessingException
    {

        String orgName;

        try {
           orgName = customRepository.getReportingFacilityName(organizationUid);
        } catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return orgName;
    }

    public String getSpecimanSource(Long materialUid) throws DataProcessingException {
        String specSource;
        try {
            specSource = customRepository.getSpecimanSource(materialUid);
        } catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return specSource;
    }


    public ProviderDataForPrintContainer getOrderingFacilityAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws DataProcessingException
    {

        String ORG_NAME_QUERY = "select street_addr1 \"streetAddr1\", street_addr2 \"streetAddr2\", city_desc_txt \"cityDescTxt\", state_cd \"stateCd\", zip_cd \"zipCd\" from Postal_locator with (nolock) where postal_locator_uid in ("
                + "select locator_uid from Entity_locator_participation with (nolock) where entity_uid in (?)and cd='O' and class_cd='PST')";
        /**
         * Get the OrgAddress
         */

        try {
            providerDataForPrintVO = customRepository.getOrderingFacilityAddress(providerDataForPrintVO, organizationUid);
        }  catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return providerDataForPrintVO;
    }

    public ProviderDataForPrintContainer getOrderingFacilityPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws DataProcessingException
    {

        String ORG_NAME_QUERY = "select phone_nbr_txt \"phoneNbrTxt\", extension_txt \"extensionTxt\" from TELE_locator with (nolock) where TELE_locator_uid in ("
                +" select locator_uid from Entity_locator_participation with (nolock) where entity_uid= ? and cd='PH' and class_cd='TELE')  ";
        /**
         * Get the OrgPhone
         */

        try {
            providerDataForPrintVO = customRepository.getOrderingFacilityPhone(providerDataForPrintVO, organizationUid);
        }catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return providerDataForPrintVO;
    }

    public ProviderDataForPrintContainer getOrderingPersonAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws  DataProcessingException
    {
        try {
            providerDataForPrintVO = customRepository.getOrderingPersonAddress(providerDataForPrintVO, organizationUid);
        } catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return providerDataForPrintVO;
    }

    public ProviderDataForPrintContainer getOrderingPersonPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws DataProcessingException
    {
        try {
            providerDataForPrintVO = customRepository.getOrderingPersonPhone(providerDataForPrintVO, organizationUid);
        }  catch (Exception ex) {

            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return providerDataForPrintVO;
    }

    public Long getProviderInformation (ArrayList<Object>  providerDetails, LabReportSummaryContainer labRep)
    {

        Long providerUid = null;

        if (providerDetails != null && providerDetails.size() > 0 && labRep != null) {
            Object[] orderProvider = providerDetails.toArray();

            if (orderProvider[0] != null) {
                labRep.setProviderLastName((String) orderProvider[0]);
            }
            if (orderProvider[1] != null){
                labRep.setProviderFirstName((String) orderProvider[1]);
            }
            if (orderProvider[2] != null){
                labRep.setProviderPrefix((String) orderProvider[2]);
            }
            if (orderProvider[3] != null){
                labRep.setProviderSuffix(( String)orderProvider[3]);
            }
            if (orderProvider[4] != null){
                labRep.setDegree(( String)orderProvider[4]);
            }
            if (orderProvider[5] != null){
                providerUid= (Long)orderProvider[5];
                labRep.setProviderUid((String.valueOf(orderProvider[5])));
            }
        }

        return providerUid;

    }


    public void getTestAndSusceptibilities(String typeCode, Long observationUid, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm)
    {
        ResultedTestSummaryContainer testVO = new ResultedTestSummaryContainer();
        ArrayList<ResultedTestSummaryContainer>  testList;

        testList = customRepository.getTestAndSusceptibilities(typeCode, observationUid, labRepEvent, labRepSumm);
        //afterReflex = System.currentTimeMillis();
        //totalReflex += (afterReflex - beforeReflex);

        if (testList != null) {

            if(labRepEvent != null)
                labRepEvent.setTheResultedTestSummaryVOCollection(testList);
            if(labRepSumm != null)
                labRepSumm.setTheResultedTestSummaryVOCollection(testList);

            //timing
            //t3begin = System.currentTimeMillis();
            for (ResultedTestSummaryContainer RVO : testList) {
                setSusceptibility(RVO, labRepEvent, labRepSumm);

            }
        }
        
    }

    public Map<Object,Object>  getAssociatedInvList(Long uid,String sourceClassCd) throws DataProcessingException
    {
        Map<Object,Object> assocoiatedInvMap;
        try{
            String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "");
            if (dataAccessWhereClause == null) {
                dataAccessWhereClause = "";
            }
            else {
                dataAccessWhereClause = AND_UPPERCASE + dataAccessWhereClause;

            }

            String query = ASSOCIATED_INV_QUERY+dataAccessWhereClause;

            assocoiatedInvMap = customRepository.getAssociatedInvList(uid, sourceClassCd, query);
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.getMessage(), ex);
        }

        return assocoiatedInvMap;
    }


    private void setSusceptibility(ResultedTestSummaryContainer RVO, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm)
    {

        ResultedTestSummaryContainer susVO = new ResultedTestSummaryContainer();
        int countResult = 0;
        int countSus = 0;
        UidSummaryContainer sourceActUidVO = new UidSummaryContainer();
        ArrayList<UidSummaryContainer>  susList;

        Long sourceActUid = RVO.getSourceActUid();
        countResult = countResult + 1;


        susList = customRepository.getSusceptibilityUidSummary(RVO, labRepEvent, labRepSumm, "REFR", sourceActUid);
        //afterSus = System.currentTimeMillis();
        //totalSus += (afterSus - beforeSus);
        if (susList != null) {
            Iterator<UidSummaryContainer> susIter = susList.iterator();
            ArrayList<ResultedTestSummaryContainer>  susListFinal ;
            //timing
            //t4begin = System.currentTimeMillis();

            ArrayList<Object>  multipleSusceptArray = new ArrayList<> ();

            while (susIter.hasNext()) {
                UidSummaryContainer uidVO = susIter.next();
                Long sourceAct = uidVO.getUid();

                countSus = countSus + 1;
                //beforeReflex2 = System.currentTimeMillis();

                susListFinal = customRepository.getSusceptibilityResultedTestSummary( "COMP", sourceAct);


                //afterReflex2 = System.currentTimeMillis();
                //totalReflex2 += (afterReflex2 - beforeReflex2);

                multipleSusceptArray.addAll(susListFinal);
                //multipleSuscept.add(susListFinal);
            }

            if (multipleSusceptArray != null) {
                RVO.setTheSusTestSummaryVOColl(multipleSusceptArray);
            }

            //if (multipleSuscept != null) {
            //	RVO.setTheSusTestSummaryVOColl(multipleSuscept);
            //}
            //t4end = System.currentTimeMillis();
        }

        //t3end = System.currentTimeMillis();



    }







}
