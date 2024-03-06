package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXDocumentDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.edx.EdxDocumentRepository;
import gov.cdc.dataprocessing.service.interfaces.IEdxDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class EdxDocumentService implements IEdxDocumentService {
    private static final Logger logger = LoggerFactory.getLogger(EdxDocumentService.class);
    private final EdxDocumentRepository edxDocumentRepository;

    public EdxDocumentService(EdxDocumentRepository edxDocumentRepository) {
        this.edxDocumentRepository = edxDocumentRepository;
    }

    public Collection<EDXDocumentDT> selectEdxDocumentCollectionByActUid(Long uid) {
        Collection<EDXDocumentDT> edxDocumentDTCollection = new ArrayList<>();
        var result = edxDocumentRepository.selectEdxDocumentCollectionByActUid(uid);
        if (result.isPresent()) {
            for(var item: result.get()) {
                var elem = new EDXDocumentDT(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                edxDocumentDTCollection.add(elem);
            }
        }

        return edxDocumentDTCollection;
    }
}
