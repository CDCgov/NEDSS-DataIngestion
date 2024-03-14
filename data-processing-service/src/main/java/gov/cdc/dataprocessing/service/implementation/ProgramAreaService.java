package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.service.interfaces.IProgramAreaService;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Service
public class ProgramAreaService implements IProgramAreaService {

    private final SrteCodeObsService srteCodeObsService;
    private final ObservationUtil observationUtil;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;

    public ProgramAreaService(SrteCodeObsService srteCodeObsService,
                              ObservationUtil observationUtil,
                              OrganizationRepositoryUtil organizationRepositoryUtil) {
        this.srteCodeObsService = srteCodeObsService;
        this.observationUtil = observationUtil;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
    }

    public String deriveProgramAreaCd(LabResultProxyContainer labResultProxyVO, ObservationVO orderTest) throws DataProcessingException {
        //Gathering the result tests
        Collection<ObservationVO> resultTests = new ArrayList<>();
        for (ObservationVO obsVO : labResultProxyVO.getTheObservationVOCollection()) {
            String obsDomainCdSt1 = obsVO.getTheObservationDT().getObsDomainCdSt1();
            if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD)) {
                resultTests.add(obsVO);
            }
        }

        //Get the reporting lab clia
        String reportingLabCLIA = "";
        if(labResultProxyVO.getLabClia()!=null && labResultProxyVO.isManualLab())
        {
            reportingLabCLIA =labResultProxyVO.getLabClia();
        }
        else
        {
            reportingLabCLIA = getReportingLabCLIA(labResultProxyVO);
        }

        if(reportingLabCLIA == null || reportingLabCLIA.trim().equals(""))
        {
            reportingLabCLIA = NEDSSConstant.DEFAULT;
        }

        //Get program area
        if(!orderTest.getTheObservationDT().getElectronicInd().equals(NEDSSConstant.ELECTRONIC_IND_ELR)){
            Map<Object, Object> paResults = null;
            if (resultTests.size() > 0)
            {
                paResults = srteCodeObsService.getProgramArea(reportingLabCLIA, resultTests, orderTest.getTheObservationDT().getElectronicInd());
            }

            //set program area for order test
            if (paResults != null && paResults.containsKey(ELRConstant.PROGRAM_AREA_HASHMAP_KEY))
            {
                orderTest.getTheObservationDT().setProgAreaCd( (String) paResults.get(ELRConstant.PROGRAM_AREA_HASHMAP_KEY));
            }
            else
            {
                orderTest.getTheObservationDT().setProgAreaCd(null);
            }

            //Return errors if any
            if (paResults != null
                && paResults.containsKey("ERROR")
            )
            {
                return (String) paResults.get("ERROR");
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    private String getReportingLabCLIA(AbstractVO proxy) throws DataProcessingException {
        Collection<ParticipationDT>  partColl = null;
        if (proxy instanceof LabResultProxyContainer)
        {
            partColl = ( (LabResultProxyContainer) proxy).getTheParticipationDTCollection();
        }
//            if (proxy instanceof MorbidityProxyVO)
//            {
//                partColl = ( (MorbidityProxyVO) proxy).getTheParticipationDTCollection();
//            }

        //Get the reporting lab
        Long reportingLabUid = observationUtil.getUid(partColl,
                null,
                NEDSSConstant.ENTITY_UID_LIST_TYPE,
                NEDSSConstant.ORGANIZATION,
                NEDSSConstant.PAR111_TYP_CD,
                NEDSSConstant.PART_ACT_CLASS_CD,
                NEDSSConstant.RECORD_STATUS_ACTIVE);

        OrganizationVO reportingLabVO = null;
        if (reportingLabUid != null)
        {
            reportingLabVO = organizationRepositoryUtil.loadObject(reportingLabUid, null);
        }

        //Get the CLIA
        String reportingLabCLIA = null;

        if(reportingLabVO != null)
        {
            Collection<EntityIdDto>  entityIdColl = reportingLabVO.getTheEntityIdDtoCollection();

            if (entityIdColl != null && entityIdColl.size() > 0) {
                for (Iterator<EntityIdDto> it = entityIdColl.iterator(); it.hasNext(); ) {
                    EntityIdDto idDT = (EntityIdDto) it.next();
                    if (idDT == null) {
                        continue;
                    }

                    String authoCd = idDT.getAssigningAuthorityCd();
                    String idTypeCd = idDT.getTypeCd();
                    if (authoCd != null && idTypeCd != null
                            && authoCd.equalsIgnoreCase(NEDSSConstant.REPORTING_LAB_CLIA)
                            && idTypeCd.equalsIgnoreCase(NEDSSConstant.REPORTING_LAB_FI_TYPE)) { //civil00011659
                        reportingLabCLIA = idDT.getRootExtensionTxt();
                        break;
                    }
                }
            }
        }
        return reportingLabCLIA;
    }



}
