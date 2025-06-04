package gov.cdc.dataprocessing.utilities.component.page_and_pam;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsActJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsCaseAnswerJdbcRepository;
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
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class PamRepositoryUtil {

    private final NbsActJdbcRepository nbsActJdbcRepository;
    private final NbsCaseAnswerJdbcRepository nbsCaseAnswerJdbcRepository;

    public PamRepositoryUtil(
            NbsActJdbcRepository nbsActJdbcRepository,
            NbsCaseAnswerJdbcRepository nbsCaseAnswerJdbcRepository) {
        this.nbsActJdbcRepository = nbsActJdbcRepository;
        this.nbsCaseAnswerJdbcRepository = nbsCaseAnswerJdbcRepository;
    }
    public PublicHealthCaseContainer getPamHistory(PublicHealthCaseContainer publicHealthCaseContainer) throws DataProcessingException {
            Collection<NbsActEntityDto> pamEntityColl = getPamCaseEntityDTCollection(publicHealthCaseContainer.getThePublicHealthCaseDto());
            publicHealthCaseContainer.setNbsCaseEntityCollection(pamEntityColl);
            Collection<NbsCaseAnswerDto>  pamAnswerColl = getPamAnswerDTCollection(publicHealthCaseContainer.getThePublicHealthCaseDto());
            publicHealthCaseContainer.setNbsAnswerCollection(pamAnswerColl);
            return publicHealthCaseContainer;
    }

    private Collection<NbsActEntityDto>  getPamCaseEntityDTCollection(RootDtoInterface rootDTInterface)   {
        ArrayList<NbsActEntityDto> pamEntityDTCollection  = new ArrayList<> ();
        var res  =  nbsActJdbcRepository.getNbsActEntitiesByActUid(rootDTInterface.getUid());
        if (res != null && !res.isEmpty()) {
            for(var item : res) {
                var nbsItem = new NbsActEntityDto(item);
                pamEntityDTCollection.add(nbsItem);
            }
        }
        return pamEntityDTCollection;
    }

    private Collection<NbsCaseAnswerDto>  getPamAnswerDTCollection(RootDtoInterface rootDTInterface)   {
        ArrayList<NbsCaseAnswerDto> nbsAnswerDTCollection  = new ArrayList<> ();

        var res  =  nbsCaseAnswerJdbcRepository.getNbsCaseAnswerByActUid(rootDTInterface.getUid());
        if (res != null && !res.isEmpty()) {
            for(var item : res) {
                var nbsItem = new NbsCaseAnswerDto(item);
                nbsAnswerDTCollection.add(nbsItem);
            }
        }

        return nbsAnswerDTCollection;

    }

}
