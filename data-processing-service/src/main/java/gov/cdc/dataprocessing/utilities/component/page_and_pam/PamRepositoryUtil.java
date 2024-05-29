package gov.cdc.dataprocessing.utilities.component.page_and_pam;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.dto.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.NbsCaseAnswerRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsActEntityRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class PamRepositoryUtil {
    NbsActEntityRepository nbsActEntityRepository;
    NbsCaseAnswerRepository nbsCaseAnswerRepository;

    public PamRepositoryUtil(NbsActEntityRepository nbsActEntityRepository,
                             NbsCaseAnswerRepository nbsCaseAnswerRepository) {
        this.nbsActEntityRepository = nbsActEntityRepository;
        this.nbsCaseAnswerRepository = nbsCaseAnswerRepository;
    }
    public PublicHealthCaseVO getPamHistory(PublicHealthCaseVO publicHealthCaseVO) throws DataProcessingException {
        try{
            Collection<NbsActEntityDto> pamEntityColl = getPamCaseEntityDTCollection(publicHealthCaseVO.getThePublicHealthCaseDT());
            publicHealthCaseVO.setNbsCaseEntityCollection(pamEntityColl);
            Collection<NbsCaseAnswerDto>  pamAnswerColl = getPamAnswerDTCollection(publicHealthCaseVO.getThePublicHealthCaseDT());
            publicHealthCaseVO.setNbsAnswerCollection(pamAnswerColl);
            return publicHealthCaseVO;
        }catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
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
            throw new DataProcessingException(ex.toString());
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
            throw new DataProcessingException(ex.toString());
        }
        return nbsAnswerDTCollection;

    }

}
