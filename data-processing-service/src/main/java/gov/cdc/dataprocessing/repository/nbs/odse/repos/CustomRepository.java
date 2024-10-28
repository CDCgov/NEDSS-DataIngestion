package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import gov.cdc.dataprocessing.model.dto.phc.CTContactSummaryDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public interface CustomRepository {
    Collection<CTContactSummaryDto> getContactByPatientInfo(String queryString);
    Map<Object,Object> getLabParticipations(Long observationUID);
    ArrayList<Object> getPatientPersonInfo(Long observationUID);
    ArrayList<Object>  getProviderInfo(Long observationUID,String partTypeCd);
    ArrayList<Object>  getActIdDetails(Long observationUID);
    String getReportingFacilityName(Long organizationUid);
    String getSpecimanSource(Long materialUid);
    ProviderDataForPrintContainer getOrderingFacilityAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid);
    ProviderDataForPrintContainer getOrderingFacilityPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid);
    ProviderDataForPrintContainer getOrderingPersonAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid);
    ProviderDataForPrintContainer getOrderingPersonPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid);
    ArrayList<ResultedTestSummaryContainer> getTestAndSusceptibilities(String typeCode, Long observationUid, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm);
    ArrayList<UidSummaryContainer> getSusceptibilityUidSummary(ResultedTestSummaryContainer RVO, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm, String typeCode, Long observationUid); //NOSONAR

    ArrayList<ResultedTestSummaryContainer> getSusceptibilityResultedTestSummary(String typeCode, Long observationUid);
    Map<Object,Object>  getAssociatedInvList(Long uid,String sourceClassCd, String theQuery);
    Map<Object, Object> retrieveTreatmentSummaryVOForInv(Long publicHealthUID, String theQuery);
    Map<String, EDXEventProcessDto>getEDXEventProcessMapByCaseId(Long publicHealthCaseUid);
    Map<Object, Object> retrieveDocumentSummaryVOForInv(Long publicHealthUID);
    List<NotificationSummaryContainer> retrieveNotificationSummaryListForInvestigation(Long publicHealthUID, String theQuery);

    Map<Object, Object> getAssociatedDocumentList(Long uid, String targetClassCd, String sourceClassCd, String theQuery);

    List<StateDefinedFieldDataDto> getLdfCollection(Long busObjectUid, String conditionCode, String theQuery);
    NbsDocumentContainer getNbsDocument(Long nbsUid) throws DataProcessingException;
    ArrayList<Object> getInvListForCoInfectionId(Long mprUid,String coInfectionId) throws DataProcessingException;
}
