package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.deadletter.model.DeadLetterELRDto;
import gov.cdc.dataingestion.deadletter.repository.IDltRepository;
import gov.cdc.dataingestion.deadletter.repository.model.DeadLetterELRModel;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadLetterService {
    private static final String CREATED_BY = "DeadLetterService";
    private final IDltRepository dltRepository;
    private final KafkaProducerService kafkaProducerService;

    public List<DeadLetterELRDto> getAllNewDltRecord() {
        List<DeadLetterELRModel> deadLetterELRModels = dltRepository.findAllNewDlt(Sort.by("updated_on"));
        var dtoModels = convertModelToDto(deadLetterELRModels);
        return dtoModels;
    }

    public DeadLetterELRDto getDltRecordById(String id) {
        DeadLetterELRModel model = dltRepository.getById(id);
        return new DeadLetterELRDto(model);
    }

    public DeadLetterELRDto saveDltRecord(DeadLetterELRDto model) {
        DeadLetterELRModel modelForUpdate = dltRepository.save(convertDtoToModel(model));
        return model;
    }

    private List<DeadLetterELRDto> convertModelToDto(List<DeadLetterELRModel> models) {
        List<DeadLetterELRDto>  dtlModels = new ArrayList<>() {};
        for(DeadLetterELRModel model: models) {
            dtlModels.add(new DeadLetterELRDto(model));
        }
        return dtlModels;
    }

    private DeadLetterELRModel convertDtoToModel(DeadLetterELRDto dtoModel) {
        DeadLetterELRModel model = new DeadLetterELRModel();
        model.setId(dtoModel.getId());
        model.setRawId(dtoModel.getRawId());
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
