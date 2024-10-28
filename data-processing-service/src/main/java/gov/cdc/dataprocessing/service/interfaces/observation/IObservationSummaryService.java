package gov.cdc.dataprocessing.service.interfaces.observation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.ProviderDataForPrintContainer;
import gov.cdc.dataprocessing.model.container.model.UidSummaryContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public interface IObservationSummaryService {
    Collection<UidSummaryContainer> findAllActiveLabReportUidListForManage(Long investigationUid, String whereClause) throws DataProcessingException;
    Map<Object,Object> getLabParticipations(Long observationUID) throws DataProcessingException;
    ArrayList<Object> getPatientPersonInfo(Long observationUID) throws DataProcessingException;
    ArrayList<Object>  getProviderInfo(Long observationUID,String partTypeCd) throws DataProcessingException;
    ArrayList<Object>  getActIdDetails(Long observationUID) throws DataProcessingException;
    String getReportingFacilityName(Long organizationUid) throws DataProcessingException;
    String getSpecimanSource(Long materialUid) throws DataProcessingException;
    ProviderDataForPrintContainer getOrderingFacilityAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws DataProcessingException;
    ProviderDataForPrintContainer getOrderingFacilityPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws DataProcessingException;
    ProviderDataForPrintContainer getOrderingPersonAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws  DataProcessingException;
    ProviderDataForPrintContainer getOrderingPersonPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) throws DataProcessingException;
    Long getProviderInformation (ArrayList<Object>  providerDetails, LabReportSummaryContainer labRep);
    void getTestAndSusceptibilities(String typeCode, Long observationUid, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm);
    Map<Object,Object>  getAssociatedInvList(Long uid,String sourceClassCd) throws DataProcessingException;
}
