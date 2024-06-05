package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.edx.EdxDocumentRepository;
import gov.cdc.dataprocessing.service.interfaces.observation.IEdxDocumentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
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
