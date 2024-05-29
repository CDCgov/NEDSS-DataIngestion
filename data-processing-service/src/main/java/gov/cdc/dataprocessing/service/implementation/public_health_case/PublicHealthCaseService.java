package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class PublicHealthCaseService implements IPublicHealthCaseService {
    private static final Logger logger = LoggerFactory.getLogger(PublicHealthCaseService.class);

    private final EntityHelper entityHelper;
    private final PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    public PublicHealthCaseService(EntityHelper entityHelper,
                                   PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil) {

        this.entityHelper = entityHelper;
        this.publicHealthCaseRepositoryUtil = publicHealthCaseRepositoryUtil;
    }

    public Long setPublicHealthCase(PublicHealthCaseVO publicHealthCaseVO) throws DataProcessingException {

        Long PubHealthCaseUid = -1L;

        try
        {

            PublicHealthCaseDT publicHealthCase = null;

            Collection<ActivityLocatorParticipationDto> alpDTCol = publicHealthCaseVO.getTheActivityLocatorParticipationDTCollection();
            Collection<ActRelationshipDto> arDTCol = publicHealthCaseVO.getTheActRelationshipDTCollection();
            Collection<ParticipationDto> pDTCol = publicHealthCaseVO.getTheParticipationDTCollection();
            Collection<ActivityLocatorParticipationDto> col = null;
            Collection<ActRelationshipDto> colActRelationship = null;
            Collection<ParticipationDto> colParticipation = null;

            if (alpDTCol != null)
            {
                col = entityHelper.iterateALPDTActivityLocatorParticipation(alpDTCol);
                publicHealthCaseVO.setTheActivityLocatorParticipationDTCollection(col);
            }

            if (arDTCol != null)
            {
                colActRelationship = entityHelper.iterateARDTActRelationship(arDTCol);
                publicHealthCaseVO.setTheActRelationshipDTCollection(colActRelationship);
            }

            if (pDTCol != null)
            {
                colParticipation = entityHelper.iteratePDTForParticipation(pDTCol);
                publicHealthCaseVO.setTheParticipationDTCollection(colParticipation);
            }

            if (publicHealthCaseVO.isItNew())
            {
                publicHealthCaseRepositoryUtil.create(publicHealthCaseVO);
                publicHealthCase =  publicHealthCaseVO.getThePublicHealthCaseDT();
                PubHealthCaseUid = publicHealthCase.getPublicHealthCaseUid();
            }
            else
            {
                publicHealthCaseRepositoryUtil.update(publicHealthCaseVO);
                PubHealthCaseUid = publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid();
            }
        }
        catch (Exception e)
        {
           throw new DataProcessingException(e.getMessage(), e);
        }

        return PubHealthCaseUid;
    }
}
