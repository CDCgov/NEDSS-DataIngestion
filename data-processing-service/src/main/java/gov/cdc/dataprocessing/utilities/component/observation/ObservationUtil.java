package gov.cdc.dataprocessing.utilities.component.observation;


import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

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
public class ObservationUtil {


    public ObservationUtil() {
        // UNIT TEST
    }
    @SuppressWarnings("java:S3776")

    public Long getUid(Collection<ParticipationDto> participationDtoCollection,
                        Collection<ActRelationshipDto> actRelationshipDtoCollection,
                        String uidListType, String uidClassCd, String uidTypeCd,
                        String uidActClassCd, String uidRecordStatusCd) throws DataProcessingException {
        Long anUid = null;

        if (participationDtoCollection != null) {
            for (ParticipationDto partDT : participationDtoCollection) {
                if (
                        (
                                (
                                        partDT.getSubjectClassCd() != null
                                                && partDT.getSubjectClassCd().equalsIgnoreCase(uidClassCd)
                                )
                                        && (partDT.getTypeCd() != null
                                        && partDT.getTypeCd().equalsIgnoreCase(uidTypeCd))
                                        && (partDT.getActClassCd() != null
                                        && partDT.getActClassCd().equalsIgnoreCase(uidActClassCd))
                                        && (partDT.getRecordStatusCd() != null
                                        && partDT.getRecordStatusCd().equalsIgnoreCase(uidRecordStatusCd))
                        )
                )
                {
                    anUid = partDT.getSubjectEntityUid();
                }
            }
        }
        else if (actRelationshipDtoCollection != null) {
            for (ActRelationshipDto actRelDT : actRelationshipDtoCollection) {
                if (
                        (
                                actRelDT.getSourceClassCd() != null
                                        && actRelDT.getSourceClassCd().equalsIgnoreCase(uidClassCd)
                        )
                                && (
                                actRelDT.getTypeCd() != null
                                        && actRelDT.getTypeCd().equalsIgnoreCase(uidTypeCd)
                        )
                                && (
                                actRelDT.getTargetClassCd() != null
                                        && actRelDT.getTargetClassCd().equalsIgnoreCase(uidActClassCd)
                        )
                                && (
                                actRelDT.getRecordStatusCd() != null
                                        && actRelDT.getRecordStatusCd().equalsIgnoreCase(uidRecordStatusCd)
                        )

                ) {
                    if (uidListType.equalsIgnoreCase(NEDSSConstant.ACT_UID_LIST_TYPE)) {
                        anUid = actRelDT.getTargetActUid();
                    } else if (uidListType.equalsIgnoreCase(NEDSSConstant.SOURCE_ACT_UID_LIST_TYPE)) {
                        anUid = actRelDT.getSourceActUid();
                    }
                }
            }
        }

        return anUid;
    }

    /**
     * Description:
     * Root OBS are one of these following
     *  - Ctrl Code Display Form = LabReport;
     *  - Obs Domain Code St 1 = Order;
     *  - Ctrl Code Display Form = MorbReport;

     *  Original Name: getRootDT
     **/
    public ObservationDto getRootObservationDto(BaseContainer proxyVO) throws DataProcessingException {
        ObservationContainer rootVO = getRootObservationContainer(proxyVO);
        if (rootVO != null)
        {
            return rootVO.getTheObservationDto();
        }
        return null;

    }

    /**
     * Description:
     * Root OBS are one of these following
     *  - Ctrl Code Display Form = LabReport;
     *  - Obs Domain Code St 1 = Order;
     *  - Ctrl Code Display Form = MorbReport;
     * Original Name: getRootObservationContainer
     **/
    public ObservationContainer getRootObservationContainer(BaseContainer proxy) throws DataProcessingException
    {
        Collection<ObservationContainer>  obsColl = null;
        boolean isLabReport = false;

        if (proxy instanceof LabResultProxyContainer)
        {
            obsColl = ( (LabResultProxyContainer) proxy).getTheObservationContainerCollection();
            isLabReport = true;
        }

        ObservationContainer rootVO = getRootObservationContainerFromObsCollection(obsColl, isLabReport);

        if( rootVO == null)
        {
            throw new DataProcessingException("Expected the proxyVO containing a root observation(e.g., ordered test)");
        }
        return rootVO;

    }


    /**
     * Description:
     * Root OBS are one of these following
     *  - Ctrl Code Display Form = LabReport;
     *  - Obs Domain Code St 1 = Order;
     *  - Ctrl Code Display Form = MorbReport;
     **/
    private ObservationContainer getRootObservationContainerFromObsCollection(Collection<ObservationContainer> obsColl, boolean isLabReport) {
        if(obsColl == null){
            return null;
        }

        Iterator<ObservationContainer> iterator;
        for (iterator = obsColl.iterator(); iterator.hasNext(); )
        {
            ObservationContainer observationContainer = iterator.next();
            if (
                observationContainer.getTheObservationDto() != null
                && (
                        (
                            observationContainer.getTheObservationDto().getCtrlCdDisplayForm() != null
                            && observationContainer.getTheObservationDto().getCtrlCdDisplayForm().equalsIgnoreCase(NEDSSConstant.LAB_CTRLCD_DISPLAY)
                        )
                        || (
                            observationContainer.getTheObservationDto().getObsDomainCdSt1() != null
                            && observationContainer.getTheObservationDto().getObsDomainCdSt1().equalsIgnoreCase(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD)
                            && isLabReport
                        ) || (
                            observationContainer.getTheObservationDto().getCtrlCdDisplayForm() != null &&
                            observationContainer.getTheObservationDto().getCtrlCdDisplayForm().equalsIgnoreCase(NEDSSConstant.MOB_CTRLCD_DISPLAY)
                        )
                )
            )
            {
                return observationContainer;
            }
        }
        return null;

    }


}
