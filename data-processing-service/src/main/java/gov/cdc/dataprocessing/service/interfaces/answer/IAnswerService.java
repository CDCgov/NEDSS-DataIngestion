package gov.cdc.dataprocessing.service.interfaces.answer;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.container.PageContainer;

import java.util.Collection;

public interface IAnswerService {
    PageContainer getNbsAnswerAndAssociation(Long uid) throws DataProcessingException;
    void storePageAnswer(PageContainer pageContainer, ObservationDto observationDto) throws DataProcessingException;
    void insertPageVO(PageContainer pageContainer, ObservationDto rootDTInterface) throws DataProcessingException;
    void storeActEntityDTCollectionWithPublicHealthCase(Collection<NbsActEntityDto> pamDTCollection, PublicHealthCaseDT rootDTInterface)
            throws  DataProcessingException;
}
