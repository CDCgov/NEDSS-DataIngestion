package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObsValueCodedDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.utilities.component.ObservationUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface IObservationCodeService {

    ArrayList<String> deriveTheConditionCodeList(LabResultProxyContainer labResultProxyVO, ObservationVO orderTest) throws DataProcessingException;

}
