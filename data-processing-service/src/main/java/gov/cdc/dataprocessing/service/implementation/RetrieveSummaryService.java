package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.container.LabReportSummaryContainer;
import gov.cdc.dataprocessing.service.interfaces.IRetrieveSummaryService;
import gov.cdc.dataprocessing.utilities.component.PublicHealthCaseRepositoryUtil;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RetrieveSummaryService implements IRetrieveSummaryService {
    private final PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;

    public RetrieveSummaryService(PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil) {
        this.publicHealthCaseRepositoryUtil = publicHealthCaseRepositoryUtil;
    }

    public void checkBeforeCreateAndStoreMessageLogDTCollection(Long investigationUID,
                                                                Collection<LabReportSummaryContainer> reportSumVOCollection){

        try {
            PublicHealthCaseDT publicHealthCaseDT = null;

            publicHealthCaseDT = publicHealthCaseRepositoryUtil.findPublicHealthCase(investigationUID);

            if(publicHealthCaseDT.isStdHivProgramAreaCode()){
                //TODO: LOGGING PIPELINE
                createAndStoreMessageLogDTCollection( reportSumVOCollection, publicHealthCaseDT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * LOGGING
     * TODO: this need to move to logging pipeline
     * */
    private void createAndStoreMessageLogDTCollection(Collection<LabReportSummaryContainer> reportSumVOCollection,PublicHealthCaseDT publicHealthCaseDT){
//        try {
//            Collection<MessageLogDT> coll =  new ArrayList<MessageLogDT>();
//            java.util.Date dateTime = new java.util.Date();
//            Timestamp time = new Timestamp(dateTime.getTime());
//
//            if(!reportSumVOCollection.isEmpty())
//            {
//                logger.debug("Number of observation sum vo: " + reportSumVOCollection.size());
//                Iterator<Object>  theIterator = reportSumVOCollection.iterator();
//                while( theIterator.hasNext() )
//                {
//                    ReportSummaryInterface reportSumVO = (ReportSummaryInterface)theIterator.next();
//                    if(reportSumVO.getIsAssociated()== true && reportSumVO.getIsTouched()== true){
//                        PublicHealthCaseRootDAOImpl phc = new PublicHealthCaseRootDAOImpl();
//                        PublicHealthCaseDT phcDT =phc.getOpenPublicHealthCaseWithInvestigatorDT(publicHealthCaseDT.getPublicHealthCaseUid());
//                        Long providerUid=nbsSecurityObj.getTheUserProfile().getTheUser().getProviderUid();
//                        if( phcDT!=null
//                                && (providerUid==null
//                                || !(providerUid.compareTo(phcDT.getCurrentInvestigatorUid())==0))){
//                            MessageLogDT messageLogDT =createMessageLogDT(phcDT, nbsSecurityObj);
//                            coll.add(messageLogDT);
//                        }
//
//                    }
//                }
//                MessageLogDAOImpl messageLogDAOImpl =  new MessageLogDAOImpl();
//                try {
//                    messageLogDAOImpl.storeMessageLogDTCollection(coll);
//                } catch (Exception e) {
//                    logger.error("Unable to store the Error message in createAndStoreMesssageLogDTCollection for = "
//                            + publicHealthCaseDT.toString());
//                }
//            }
//        } catch (Exception e) {
//            logger.error("createAndStoreMesssageLogDTCollection error throw"+ e.getMessage(),e);
//        }
    }
}
