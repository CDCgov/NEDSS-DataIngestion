package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.edx.EdxDocumentRepository;
import gov.cdc.dataprocessing.service.interfaces.observation.IEdxDocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class EdxDocumentService implements IEdxDocumentService {
    private final EdxDocumentRepository edxDocumentRepository;

    public EdxDocumentService(EdxDocumentRepository edxDocumentRepository) {
        this.edxDocumentRepository = edxDocumentRepository;
    }

    public Collection<EDXDocumentDto> selectEdxDocumentCollectionByActUid(Long uid) {
        Collection<EDXDocumentDto> edxDocumentDtoCollection = new ArrayList<>();
        var result = edxDocumentRepository.selectEdxDocumentCollectionByActUid(uid);
        if (result.isPresent()) {
            for(var item: result.get()) {
                var elem = new EDXDocumentDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                edxDocumentDtoCollection.add(elem);
            }
        }

        return edxDocumentDtoCollection;
    }

    @Transactional
    public EDXDocumentDto saveEdxDocument(EDXDocumentDto edxDocumentDto) {
        EdxDocument data = new EdxDocument(edxDocumentDto);
        var res = edxDocumentRepository.save(data);
        return new EDXDocumentDto(res);
    }
}
