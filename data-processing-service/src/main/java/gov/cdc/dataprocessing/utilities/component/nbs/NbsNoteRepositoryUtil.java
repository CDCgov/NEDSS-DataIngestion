package gov.cdc.dataprocessing.utilities.component.nbs;

import gov.cdc.dataprocessing.model.dto.nbs.NbsNoteDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsNote;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsNoteRepository;
import org.springframework.stereotype.Component;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public class NbsNoteRepositoryUtil {

    private final NbsNoteRepository nbsNoteRepository;

    public NbsNoteRepositoryUtil(NbsNoteRepository nbsNoteRepository) {
        this.nbsNoteRepository = nbsNoteRepository;
    }

    public void storeNotes(Long phcUid, Collection<NbsNoteDto> coll) {
        for(var item: coll) {
            NbsNote data = new NbsNote(item);
            data.setNoteParentUid(phcUid);
            nbsNoteRepository.save(data);
        }
    }
}
