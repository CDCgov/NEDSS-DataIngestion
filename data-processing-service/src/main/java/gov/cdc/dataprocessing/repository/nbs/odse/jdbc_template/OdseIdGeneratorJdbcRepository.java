//package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;
//
//import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
//import gov.cdc.dataprocessing.exception.DataProcessingException;
//import gov.cdc.dataprocessing.model.dto.uid.LocalUidCacheModel;
//import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class OdseIdGeneratorJdbcRepository {
//
//    private final NamedParameterJdbcTemplate jdbcTemplate;
//
//    public OdseIdGeneratorJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    private static final String SELECT_FOR_UPDATE_SQL = """
//        SELECT * FROM Local_uid_generator WITH (UPDLOCK, ROWLOCK) WHERE class_name_cd = :classNameCd
//    """;
//
//    private static final String UPDATE_SEED_SQL = """
//        UPDATE Local_uid_generator
//        SET seed_value_nbr = :newSeedValue
//        WHERE class_name_cd = :classNameCd
//    """;
//
//    public LocalUidGeneratorDto fetchLocalId(LocalIdClass localIdClass) throws DataProcessingException {
//        try {
//            MapSqlParameterSource params = new MapSqlParameterSource()
//                    .addValue("classNameCd", localIdClass.name());
//
//            LocalUidGeneratorDto dto = jdbcTemplate.queryForObject(
//                    SELECT_FOR_UPDATE_SQL,
//                    params,
//                    (rs, rowNum) -> {
//                        LocalUidGeneratorDto result = new LocalUidGeneratorDto();
//                        result.setUidPrefixCd(rs.getString("uid_prefix_cd"));
//                        result.setUidSuffixCd(rs.getString("uid_suffix_cd"));
//                        result.setClassNameCd(rs.getString("class_name_cd"));
//                        result.setTypeCd(rs.getString("type_cd"));
//                        result.setSeedValueNbr(rs.getLong("seed_value_nbr"));
//                        return result;
//                    });
//
//            long newSeed = dto.getSeedValueNbr() + LocalUidCacheModel.SEED_COUNTER + 1;
//
//            MapSqlParameterSource updateParams = new MapSqlParameterSource()
//                    .addValue("newSeedValue", newSeed)
//                    .addValue("classNameCd", localIdClass.name());
//
//            jdbcTemplate.update(UPDATE_SEED_SQL, updateParams);
//
//            dto.setCounter(LocalUidCacheModel.SEED_COUNTER);
//            dto.setUsedCounter(1);
//            return dto;
//
//        } catch (EmptyResultDataAccessException e) {
//            throw new DataProcessingException("Local UID not found for class: " + localIdClass.name());
//        } catch (Exception e) {
//            throw new DataProcessingException("Error fetching local UID for class: " + localIdClass.name(), e);
//        }
//    }
//}
