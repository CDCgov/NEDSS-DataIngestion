package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueTxtDto;
import gov.cdc.dataprocessing.model.phdc.HL7NTEType;

import java.util.ArrayList;
import java.util.List;

public class ObsReqNoteHelper {
    protected static ObservationContainer getObsReqNotes(List<HL7NTEType> noteArray, ObservationContainer observationContainer) throws DataProcessingException {
        try {
            for (HL7NTEType notes : noteArray) {
                if (notes.getHL7Comment() != null && notes.getHL7Comment().size() > 0) {
                    for (int j = 0; j < notes.getHL7Comment().size(); j++) {
                        String note = notes.getHL7Comment().get(j);
                        ObsValueTxtDto obsValueTxtDto = new ObsValueTxtDto();
                        obsValueTxtDto.setItNew(true);
                        obsValueTxtDto.setItDirty(false);
                        obsValueTxtDto.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
                        obsValueTxtDto.setTxtTypeCd(EdxELRConstant.ELR_OBX_COMMENT_TYPE);

                        obsValueTxtDto.setValueTxt(note);
                        if (observationContainer.getTheObsValueTxtDtoCollection() == null) {
                            observationContainer.setTheObsValueTxtDtoCollection(new ArrayList<>());
                        }
                        int seq = observationContainer.getTheObsValueTxtDtoCollection().size();
                        obsValueTxtDto.setObsValueTxtSeq(++seq);
                        observationContainer.getTheObsValueTxtDtoCollection().add(obsValueTxtDto);
                    }
                } else {
                    ObsValueTxtDto obsValueTxtDto = new ObsValueTxtDto();
                    obsValueTxtDto.setItNew(true);
                    obsValueTxtDto.setItDirty(false);
                    obsValueTxtDto.setValueTxt("\r");
                    obsValueTxtDto.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
                    obsValueTxtDto.setTxtTypeCd(EdxELRConstant.ELR_OBX_COMMENT_TYPE);

                    if (observationContainer.getTheObsValueTxtDtoCollection() == null)
                        observationContainer.setTheObsValueTxtDtoCollection(new ArrayList<>());
                    int seq = observationContainer.getTheObsValueTxtDtoCollection().size();
                    obsValueTxtDto.setObsValueTxtSeq(++seq);
                    observationContainer.getTheObsValueTxtDtoCollection().add(obsValueTxtDto);

                }

            }
        } catch (Exception e) {
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.getObsReqNotes:" + e.getMessage());

        }
        return observationContainer;


    }
}
