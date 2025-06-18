package gov.cdc.dataprocessing.utilities.component.page_and_pam;

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

public class PamRepositoryUtil {

    private final NbsActJdbcRepository nbsActJdbcRepository;
    private final NbsCaseAnswerJdbcRepository nbsCaseAnswerJdbcRepository;

    public PamRepositoryUtil(
            NbsActJdbcRepository nbsActJdbcRepository,
            NbsCaseAnswerJdbcRepository nbsCaseAnswerJdbcRepository) {
        this.nbsActJdbcRepository = nbsActJdbcRepository;
        this.nbsCaseAnswerJdbcRepository = nbsCaseAnswerJdbcRepository;
    }
    public PublicHealthCaseContainer getPamHistory(PublicHealthCaseContainer publicHealthCaseContainer)  {
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
