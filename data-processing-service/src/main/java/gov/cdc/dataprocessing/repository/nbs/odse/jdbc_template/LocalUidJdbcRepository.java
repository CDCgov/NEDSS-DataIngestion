package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LocalUidJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public LocalUidJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public LocalUidGenerator getLocalUID(String className, int count) {
        LocalUidGenerator localUidGenerator = new LocalUidGenerator();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplateOdse.getJdbcTemplate())
                .withProcedureName("GetUid");

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("classNameCd", className);
        inParams.put("count", count);

        Map<String, Object> result = jdbcCall.execute(inParams);

//        Map<String, String> output = new HashMap<>();
//        output.put("currentUID", (String) result.get("fromseedValueNbr"));
//        output.put("maxUID", (String) result.get("toseedValueNbr"));
//        output.put("uidPrefixCd", (String) result.get("uidPrefixCd"));
//        output.put("uidSuffixCd", (String) result.get("uidSuffixCd"));

        localUidGenerator.setClassNameCd(className);
        localUidGenerator.setUidPrefixCd((String) result.get("uidPrefixCd"));
        localUidGenerator.setUidSuffixCd((String) result.get("uidSuffixCd"));
        localUidGenerator.setSeedValueNbr(Long.valueOf((String) result.get("fromseedValueNbr")));
        return localUidGenerator;
    }
}
