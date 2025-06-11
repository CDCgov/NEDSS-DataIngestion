package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
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

    public Collection<UidSummaryContainer> findAllActiveLabReportUidListForManage(Long investigationUid, String whereClause)  {

        Collection<UidSummaryContainer>  uidSummaryVOCollection  = new ArrayList<>();
        var obsSums = observationSummaryRepository.findAllActiveLabReportUidListForManage(investigationUid, whereClause);

        for(var item : obsSums) {
            UidSummaryContainer container = new UidSummaryContainer();
            container.setUid(item.getUid());
            container.setAddTime(item.getAddTime());
            container.setAddReasonCd(item.getAddReasonCd());
            uidSummaryVOCollection.add(container);
        }

        return uidSummaryVOCollection;
    }


    public Map<Object,Object> getLabParticipations(Long observationUID)   {
        Map<Object,Object> vals;
        vals = customRepository.getLabParticipations(observationUID);
        return vals;
    }


    public ArrayList<Object>  getPatientPersonInfo(Long observationUID)
    {
        ArrayList<Object> vals;
        vals = customRepository.getPatientPersonInfo(observationUID);
        return vals;
    }


    public ArrayList<Object>  getProviderInfo(Long observationUID,String partTypeCd)
    {
        ArrayList<Object> orderProviderInfo;
        orderProviderInfo = customRepository.getProviderInfo(observationUID, partTypeCd);
        return orderProviderInfo;
    }

    public ArrayList<Object>  getActIdDetails(Long observationUID)
    {
        ArrayList<Object> actIdDetails;
        actIdDetails = customRepository.getActIdDetails(observationUID);
        return actIdDetails;
    }

    public String getReportingFacilityName(Long organizationUid)
    {
        String orgName;
        orgName = customRepository.getReportingFacilityName(organizationUid);
        return orgName;
    }

    public String getSpecimanSource(Long materialUid)   {
        String specSource;
        specSource = customRepository.getSpecimanSource(materialUid);
        return specSource;
    }


    public ProviderDataForPrintContainer getOrderingFacilityAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid)
    {
        providerDataForPrintVO = customRepository.getOrderingFacilityAddress(providerDataForPrintVO, organizationUid);
        return providerDataForPrintVO;
    }

    public ProviderDataForPrintContainer getOrderingFacilityPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid)
    {
        providerDataForPrintVO = customRepository.getOrderingFacilityPhone(providerDataForPrintVO, organizationUid);
        return providerDataForPrintVO;
    }

    public ProviderDataForPrintContainer getOrderingPersonAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid)
    {
        providerDataForPrintVO = customRepository.getOrderingPersonAddress(providerDataForPrintVO, organizationUid);
        return providerDataForPrintVO;
    }

    public ProviderDataForPrintContainer getOrderingPersonPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid)
    {
        providerDataForPrintVO = customRepository.getOrderingPersonPhone(providerDataForPrintVO, organizationUid);
        return providerDataForPrintVO;
    }

    public Long getProviderInformation (ArrayList<Object>  providerDetails, LabReportSummaryContainer labRep)
    {

        Long providerUid = null;

        if (providerDetails != null && !providerDetails.isEmpty() && labRep != null) {
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
        ArrayList<ResultedTestSummaryContainer>  testList;

        testList = customRepository.getTestAndSusceptibilities(typeCode, observationUid, labRepEvent, labRepSumm);
        if (testList != null) {

            if(labRepEvent != null)
                labRepEvent.setTheResultedTestSummaryVOCollection(testList);
            if(labRepSumm != null)
                labRepSumm.setTheResultedTestSummaryVOCollection(testList);

            for (ResultedTestSummaryContainer RVO : testList) {
                setSusceptibility(RVO, labRepEvent, labRepSumm);

            }
        }
        
    }

    public Map<Object,Object>  getAssociatedInvList(Long uid,String sourceClassCd)
    {
        Map<Object,Object> assocoiatedInvMap;
        String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "");
        if (dataAccessWhereClause == null) {
            dataAccessWhereClause = "";
        }
        else {
            dataAccessWhereClause = AND_UPPERCASE + dataAccessWhereClause;

        }

        String query = ASSOCIATED_INV_QUERY+dataAccessWhereClause;

        assocoiatedInvMap = customRepository.getAssociatedInvList(uid, sourceClassCd, query);

        return assocoiatedInvMap;
    }


    @SuppressWarnings("java:S2589")
    private void setSusceptibility(ResultedTestSummaryContainer RVO, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm)
    {
        int countSus = 0;
        ArrayList<UidSummaryContainer>  susList;

        Long sourceActUid = RVO.getSourceActUid();


        susList = customRepository.getSusceptibilityUidSummary(RVO, labRepEvent, labRepSumm, "REFR", sourceActUid);

        if (susList != null) {
            Iterator<UidSummaryContainer> susIter = susList.iterator();
            ArrayList<ResultedTestSummaryContainer>  susListFinal ;

            ArrayList<Object>  multipleSusceptArray = new ArrayList<> ();

            while (susIter.hasNext()) {
                UidSummaryContainer uidVO = susIter.next();
                Long sourceAct = uidVO.getUid();

                countSus = countSus + 1;

                susListFinal = customRepository.getSusceptibilityResultedTestSummary( "COMP", sourceAct);

                multipleSusceptArray.addAll(susListFinal);
            }

            if (multipleSusceptArray != null) {
                RVO.setTheSusTestSummaryVOColl(multipleSusceptArray);
            }

        }




    }







}
