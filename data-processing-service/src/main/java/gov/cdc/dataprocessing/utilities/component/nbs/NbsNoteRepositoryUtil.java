package gov.cdc.dataprocessing.utilities.component.nbs;

import gov.cdc.dataprocessing.model.dto.nbs.NbsNoteDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsNoteJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsNote;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component

public class NbsNoteRepositoryUtil {

    private final NbsNoteJdbcRepository nbsNoteJdbcRepository;

    public NbsNoteRepositoryUtil(
                                 NbsNoteJdbcRepository nbsNoteJdbcRepository) {
        this.nbsNoteJdbcRepository = nbsNoteJdbcRepository;
    }

    public void storeNotes(Long phcUid, Collection<NbsNoteDto> coll) {
        for(var item: coll) {
            NbsNote data = new NbsNote(item);
            data.setNoteParentUid(phcUid);
            nbsNoteJdbcRepository.mergeNbsNote(data);
//            nbsNoteRepository.save(data);
        }
    }
}
