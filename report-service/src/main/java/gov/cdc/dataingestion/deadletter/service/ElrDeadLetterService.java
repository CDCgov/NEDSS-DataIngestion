package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.model.ElrDltStatus;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElrDeadLetterService {
    private static final String CREATED_BY = "DeadLetterService";

    private final IElrDeadLetterRepository dltRepository;

    public List<ElrDeadLetterDto> getAllErrorDltRecord() {
        Optional<List<ElrDeadLetterModel>> deadLetterELRModels = dltRepository.findAllDltRecordByDltStatus(ElrDltStatus.ERROR.name());
        var dtoModels = convertModelToDto(deadLetterELRModels.get());
        return dtoModels;
    }

    public ElrDeadLetterDto getDltRecordById(String id) {
        ElrDeadLetterModel model = dltRepository.getById(id);
        return new ElrDeadLetterDto(model);
    }

    public ElrDeadLetterDto saveDltRecord(ElrDeadLetterDto model) {
        ElrDeadLetterModel modelForUpdate = dltRepository.save(convertDtoToModel(model));
        return model;
    }

    private List<ElrDeadLetterDto> convertModelToDto(List<ElrDeadLetterModel> models) {
        List<ElrDeadLetterDto>  dtlModels = new ArrayList<>() {};
        for(ElrDeadLetterModel model: models) {
            dtlModels.add(new ElrDeadLetterDto(model));
        }
        return dtlModels;
    }

    private ElrDeadLetterModel convertDtoToModel(ElrDeadLetterDto dtoModel) {
        ElrDeadLetterModel model = new ElrDeadLetterModel();
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
