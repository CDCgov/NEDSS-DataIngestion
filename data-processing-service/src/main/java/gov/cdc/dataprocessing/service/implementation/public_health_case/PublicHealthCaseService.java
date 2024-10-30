package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class PublicHealthCaseService implements IPublicHealthCaseService {
    private final EntityHelper entityHelper;
    private final PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    public PublicHealthCaseService(EntityHelper entityHelper,
                                   PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil) {

        this.entityHelper = entityHelper;
        this.publicHealthCaseRepositoryUtil = publicHealthCaseRepositoryUtil;
    }

    public Long setPublicHealthCase(PublicHealthCaseContainer publicHealthCaseContainer) throws DataProcessingException {

        Long PubHealthCaseUid;

        try
        {

            PublicHealthCaseDto publicHealthCase;

            Collection<ActivityLocatorParticipationDto> alpDTCol = publicHealthCaseContainer.getTheActivityLocatorParticipationDTCollection();
            Collection<ActRelationshipDto> arDTCol = publicHealthCaseContainer.getTheActRelationshipDTCollection();
            Collection<ParticipationDto> pDTCol = publicHealthCaseContainer.getTheParticipationDTCollection();
            Collection<ActivityLocatorParticipationDto> col;
            Collection<ActRelationshipDto> colActRelationship;
            Collection<ParticipationDto> colParticipation ;

            if (alpDTCol != null)
            {
                col = entityHelper.iterateALPDTActivityLocatorParticipation(alpDTCol);
                publicHealthCaseContainer.setTheActivityLocatorParticipationDTCollection(col);
            }

            if (arDTCol != null)
            {
                colActRelationship = entityHelper.iterateARDTActRelationship(arDTCol);
                publicHealthCaseContainer.setTheActRelationshipDTCollection(colActRelationship);
            }

            if (pDTCol != null)
            {
                colParticipation = entityHelper.iteratePDTForParticipation(pDTCol);
                publicHealthCaseContainer.setTheParticipationDTCollection(colParticipation);
            }

            if (publicHealthCaseContainer.isItNew())
            {
                publicHealthCaseRepositoryUtil.create(publicHealthCaseContainer);
                publicHealthCase =  publicHealthCaseContainer.getThePublicHealthCaseDto();
                PubHealthCaseUid = publicHealthCase.getPublicHealthCaseUid();
            }
            else
            {
                publicHealthCaseRepositoryUtil.update(publicHealthCaseContainer);
                PubHealthCaseUid = publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid();
            }
        }
        catch (Exception e)
        {
           throw new DataProcessingException(e.getMessage(), e);
        }

        return PubHealthCaseUid;
    }
}
