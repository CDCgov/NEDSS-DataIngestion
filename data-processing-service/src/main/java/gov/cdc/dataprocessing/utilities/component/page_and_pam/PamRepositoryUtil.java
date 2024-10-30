package gov.cdc.dataprocessing.utilities.component.page_and_pam;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.NbsActEntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsCaseAnswerRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class PamRepositoryUtil {
    NbsActEntityRepository nbsActEntityRepository;
    NbsCaseAnswerRepository nbsCaseAnswerRepository;

    public PamRepositoryUtil(NbsActEntityRepository nbsActEntityRepository,
                             NbsCaseAnswerRepository nbsCaseAnswerRepository) {
        this.nbsActEntityRepository = nbsActEntityRepository;
        this.nbsCaseAnswerRepository = nbsCaseAnswerRepository;
    }
    public PublicHealthCaseContainer getPamHistory(PublicHealthCaseContainer publicHealthCaseContainer) throws DataProcessingException {
            Collection<NbsActEntityDto> pamEntityColl = getPamCaseEntityDTCollection(publicHealthCaseContainer.getThePublicHealthCaseDto());
            publicHealthCaseContainer.setNbsCaseEntityCollection(pamEntityColl);
            Collection<NbsCaseAnswerDto>  pamAnswerColl = getPamAnswerDTCollection(publicHealthCaseContainer.getThePublicHealthCaseDto());
            publicHealthCaseContainer.setNbsAnswerCollection(pamAnswerColl);
            return publicHealthCaseContainer;
    }

    private Collection<NbsActEntityDto>  getPamCaseEntityDTCollection(RootDtoInterface rootDTInterface) throws DataProcessingException {
        ArrayList<NbsActEntityDto> pamEntityDTCollection  = new ArrayList<> ();
        try {
            var res  =  nbsActEntityRepository.getNbsActEntitiesByActUid(rootDTInterface.getUid());
            if (res.isPresent()) {
                for(var item : res.get()) {
                    var nbsItem = new NbsActEntityDto(item);
                    pamEntityDTCollection.add(nbsItem);
                }
            }
        } catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return pamEntityDTCollection;
    }

    private Collection<NbsCaseAnswerDto>  getPamAnswerDTCollection(RootDtoInterface rootDTInterface) throws DataProcessingException {
        ArrayList<NbsCaseAnswerDto> nbsAnswerDTCollection  = new ArrayList<> ();
        try {

            var res  =  nbsCaseAnswerRepository.getNbsCaseAnswerByActUid(rootDTInterface.getUid());
            if (res.isPresent()) {
                for(var item : res.get()) {
                    var nbsItem = new NbsCaseAnswerDto(item);
                    nbsAnswerDTCollection.add(nbsItem);
                }
            }
        } catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return nbsAnswerDTCollection;

    }

}
