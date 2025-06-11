package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.ILdfService;
import org.springframework.stereotype.Service;

import java.util.List;

import static gov.cdc.dataprocessing.constant.ComplexQueries.SELECT_LDF;

@Service

public class LdfService implements ILdfService {

    private static final String SELECT_LDF_ORDER_BY = " order by sf.ldf_uid ";
    private static final String SELECT_LDF_COND_CD_WHERE_CLAUSE = " and sdfmd.condition_cd = :conditionCd ";

    private final CustomRepository customRepository;

    public LdfService(CustomRepository customRepository) {
        this.customRepository = customRepository;
    }

    public List<StateDefinedFieldDataDto> getLDFCollection(Long busObjectUid, String conditionCode)  {

        List<StateDefinedFieldDataDto> pList;

        StringBuilder query = new StringBuilder(SELECT_LDF);
        if (conditionCode != null) //only include this where clause when the cond code is not null
        {
            query.append(SELECT_LDF_COND_CD_WHERE_CLAUSE);
        }
        query.append(SELECT_LDF_ORDER_BY);
        pList = customRepository.getLdfCollection(busObjectUid, conditionCode, query.toString());


        return pList;
    }//end of selecting place




}
