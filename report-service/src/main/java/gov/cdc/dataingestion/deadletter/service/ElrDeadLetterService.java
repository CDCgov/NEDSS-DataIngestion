package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterELRDto;
import gov.cdc.dataingestion.deadletter.repository.IElrDltRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterELRModel;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElrDeadLetterService {
    private static final String CREATED_BY = "DeadLetterService";

    private final IElrDltRepository dltRepository;

    public List<ElrDeadLetterELRDto> getAllNewDltRecord() {
        List<ElrDeadLetterELRModel> deadLetterELRModels = dltRepository.findAllNewDlt(Sort.by("updated_on"));
        var dtoModels = convertModelToDto(deadLetterELRModels);
        return dtoModels;
    }

    public ElrDeadLetterELRDto getDltRecordById(String id) {
        ElrDeadLetterELRModel model = dltRepository.getById(id);
        return new ElrDeadLetterELRDto(model);
    }

    public ElrDeadLetterELRDto saveDltRecord(ElrDeadLetterELRDto model) {
        ElrDeadLetterELRModel modelForUpdate = dltRepository.save(convertDtoToModel(model));
        return model;
    }

    private List<ElrDeadLetterELRDto> convertModelToDto(List<ElrDeadLetterELRModel> models) {
        List<ElrDeadLetterELRDto>  dtlModels = new ArrayList<>() {};
        for(ElrDeadLetterELRModel model: models) {
            dtlModels.add(new ElrDeadLetterELRDto(model));
        }
        return dtlModels;
    }

    private ElrDeadLetterELRModel convertDtoToModel(ElrDeadLetterELRDto dtoModel) {
        ElrDeadLetterELRModel model = new ElrDeadLetterELRModel();
        model.setId(dtoModel.getId());
        model.setErrorMessageId(dtoModel.getErrorMessageId());
        model.setErrorMessageSource(dtoModel.getErrorMessageSource());
        model.setErrorStackTrace(dtoModel.getErrorStackTrace());
        model.setDltOccurrence(dtoModel.getDltOccurrence());
        model.setDltStatus(dtoModel.getDltStatus());
        model.setCreatedOn(dtoModel.getCreatedOn());
        model.setUpdatedOn(dtoModel.getUpdatedOn());
        model.setCreatedBy(dtoModel.getCreatedBy());
        model.setUpdatedBy(dtoModel.getUpdatedBy());
        return model;
    }
}
