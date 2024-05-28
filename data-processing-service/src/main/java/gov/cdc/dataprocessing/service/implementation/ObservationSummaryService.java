package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.ProviderDataForPrintContainer;
import gov.cdc.dataprocessing.model.container.ResultedTestSummaryContainer;
import gov.cdc.dataprocessing.model.container.UidSummaryContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.Observation_SummaryRepository;
import gov.cdc.dataprocessing.service.interfaces.IObservationSummaryService;
import gov.cdc.dataprocessing.utilities.component.QueryHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.cdc.dataprocessing.constant.ComplexQueries.ASSOCIATED_INV_QUERY;

@Service
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
            throw new DataProcessingException(ex.toString(), ex);
        }
        return uidSummaryVOCollection;
    }


    public Map<Object,Object> getLabParticipations(Long observationUID) throws DataProcessingException {
        Map<Object,Object> vals = new HashMap<Object,Object>();
        try {

            vals = customRepository.getLabParticipations(observationUID);

        } catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
        }
        return vals;
    }


    public ArrayList<Object>  getPatientPersonInfo(Long observationUID) throws DataProcessingException
    {
        ArrayList<Object> vals= new ArrayList<> ();
        try
        {
            var res = customRepository.getPatientPersonInfo(observationUID);
            vals = res;
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.toString());
        }


        return vals;
    }


    public ArrayList<Object>  getProviderInfo(Long observationUID,String partTypeCd) throws DataProcessingException
    {
        ArrayList<Object> orderProviderInfo= new ArrayList<Object> ();
        try
        {
           orderProviderInfo = customRepository.getProviderInfo(observationUID, partTypeCd);
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.toString());
        }

        return orderProviderInfo;
    }

    public ArrayList<Object>  getActIdDetails(Long observationUID) throws DataProcessingException
    {
        ArrayList<Object> actIdDetails= new ArrayList<Object> ();
        try
        {
            actIdDetails = customRepository.getActIdDetails(observationUID);
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.toString());
        }

        return actIdDetails;
    }

    public String getReportingFacilityName(Long organizationUid) throws DataProcessingException
    {

        String orgName = null;

        try {
           orgName = customRepository.getReportingFacilityName(organizationUid);
        } catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
        }
        return orgName;
    }

    public String getSpecimanSource(Long materialUid) throws DataProcessingException {
        String specSource = null;
        try {
            specSource = customRepository.getSpecimanSource(materialUid);
        } catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
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
            throw new DataProcessingException(ex.toString());
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
            throw new DataProcessingException(ex.toString());
        }
        return providerDataForPrintVO;
    }

    public ProviderDataForPrintContainer getOrderingPersonAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws  DataProcessingException
    {
        try {
            providerDataForPrintVO = customRepository.getOrderingPersonAddress(providerDataForPrintVO, organizationUid);
        } catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
        }
        return providerDataForPrintVO;
    }

    public ProviderDataForPrintContainer getOrderingPersonPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws DataProcessingException
    {
        try {
            providerDataForPrintVO = customRepository.getOrderingPersonPhone(providerDataForPrintVO, organizationUid);
        }  catch (Exception ex) {

            throw new DataProcessingException(ex.toString());
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
                labRep.setProviderUid((String)(orderProvider[5]+""));
            }
        }

        return providerUid;

    }


    public void getTestAndSusceptibilities(String typeCode, Long observationUid, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm)
    {
        String query = "";

        ResultedTestSummaryContainer testVO = new ResultedTestSummaryContainer();
        ArrayList<ResultedTestSummaryContainer>  testList = null;

        testList = customRepository.getTestAndSusceptibilities(typeCode, observationUid, labRepEvent, labRepSumm);
        //afterReflex = System.currentTimeMillis();
        //totalReflex += (afterReflex - beforeReflex);

        if (testList != null) {

            if(labRepEvent != null)
                labRepEvent.setTheResultedTestSummaryVOCollection(testList);
            if(labRepSumm != null)
                labRepSumm.setTheResultedTestSummaryVOCollection(testList);

            Iterator<ResultedTestSummaryContainer> it = testList.iterator();

            //timing
            //t3begin = System.currentTimeMillis();
            while (it.hasNext()) {
                ResultedTestSummaryContainer RVO = it.next();
                setSusceptibility(RVO, labRepEvent, labRepSumm);

            }
        }
        
    }

    public Map<Object,Object>  getAssociatedInvList(Long uid,String sourceClassCd) throws DataProcessingException
    {
        Map<Object,Object> assocoiatedInvMap= new HashMap<Object,Object> ();
        try{
            String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "");
            if (dataAccessWhereClause == null) {
                dataAccessWhereClause = "";
            }
            else {
                dataAccessWhereClause = " AND " + dataAccessWhereClause;

            }

            String query = ASSOCIATED_INV_QUERY+dataAccessWhereClause;

            assocoiatedInvMap = customRepository.getAssociatedInvList(uid, sourceClassCd, query);
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.toString());
        }

        return assocoiatedInvMap;
    }


    private void setSusceptibility(ResultedTestSummaryContainer RVO, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm)
    {

        ResultedTestSummaryContainer susVO = new ResultedTestSummaryContainer();
        int countResult = 0;
        int countSus = 0;
        UidSummaryContainer sourceActUidVO = new UidSummaryContainer();
        ArrayList<UidSummaryContainer>  susList = new ArrayList<> ();

        Long sourceActUid = RVO.getSourceActUid();
        countResult = countResult + 1;


        susList = customRepository.getSusceptibilityUidSummary(RVO, labRepEvent, labRepSumm, "REFR", sourceActUid);
        //afterSus = System.currentTimeMillis();
        //totalSus += (afterSus - beforeSus);
        if (susList != null) {
            Iterator<UidSummaryContainer> susIter = susList.iterator();
            ArrayList<ResultedTestSummaryContainer>  susListFinal = new ArrayList<> ();
            //timing
            //t4begin = System.currentTimeMillis();

            ArrayList<Object>  multipleSusceptArray = new ArrayList<> ();

            while (susIter.hasNext()) {
                UidSummaryContainer uidVO = (UidSummaryContainer) susIter.next();
                Long sourceAct = uidVO.getUid();

                countSus = countSus + 1;
                //beforeReflex2 = System.currentTimeMillis();

                susListFinal = customRepository.getSusceptibilityResultedTestSummary( "COMP", sourceAct);


                //afterReflex2 = System.currentTimeMillis();
                //totalReflex2 += (afterReflex2 - beforeReflex2);

                Iterator<ResultedTestSummaryContainer> multSuscepts = susListFinal.iterator();
                while (multSuscepts.hasNext())
                {
                    ResultedTestSummaryContainer rtsVO = multSuscepts.next();
                    multipleSusceptArray.add(rtsVO);
                }
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
