package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.ILdfService;
import org.springframework.stereotype.Service;

import java.util.List;

import static gov.cdc.dataprocessing.constant.ComplexQueries.SELECT_LDF;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public class LdfService implements ILdfService {

    private final String SELECT_LDF_ORDER_BY = " order by sf.ldf_uid ";
    private final String SELECT_LDF_COND_CD_WHERE_CLAUSE = " and sdfmd.condition_cd = :conditionCd ";

    private final CustomRepository customRepository;

    public LdfService(CustomRepository customRepository) {
        this.customRepository = customRepository;
    }

    public List<StateDefinedFieldDataDto> getLDFCollection(Long busObjectUid, String conditionCode) throws DataProcessingException {

        StateDefinedFieldDataDto stateDefinedFieldDataDT = new StateDefinedFieldDataDto();
        List<StateDefinedFieldDataDto> pList;
        try
        {
            StringBuilder query = new StringBuilder(SELECT_LDF);
            if (conditionCode != null) //only include this where clause when the cond code is not null
            {
                query.append(this.SELECT_LDF_COND_CD_WHERE_CLAUSE);
            }
            query.append(this.SELECT_LDF_ORDER_BY);
            pList = customRepository.getLdfCollection(busObjectUid, conditionCode, query.toString());

        }
        catch(Exception ex)
        {
            throw new DataProcessingException( ex.getMessage());
        }
        return pList;
    }//end of selecting place




}
