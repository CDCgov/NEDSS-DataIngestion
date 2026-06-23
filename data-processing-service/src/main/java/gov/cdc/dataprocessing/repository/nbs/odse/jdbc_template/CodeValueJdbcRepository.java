package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import static gov.cdc.dataprocessing.constant.data_field.CODE_SET_NM_JAVA;
import static gov.cdc.dataprocessing.constant.query.CodeValueQuery.*;

import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CodeValueJdbcRepository {
  private final NamedParameterJdbcTemplate jdbcTemplateOdse;

  public CodeValueJdbcRepository(
      @Qualifier("srteNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
    this.jdbcTemplateOdse = jdbcTemplateOdse;
  }

  public List<CodeValueGeneral> findCodeDescriptionsByCodeSetNm(String codeSetNm) {
    MapSqlParameterSource params =
        new MapSqlParameterSource().addValue(CODE_SET_NM_JAVA, codeSetNm.toUpperCase());
    return jdbcTemplateOdse.query(
        SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_NM,
        params,
        new BeanPropertyRowMapper<>(CodeValueGeneral.class));
  }

  public List<CodeValueGeneral> findCodeValuesByCodeSetNm(String codeSetNm) {
    MapSqlParameterSource params =
        new MapSqlParameterSource().addValue(CODE_SET_NM_JAVA, codeSetNm.toUpperCase());
    return jdbcTemplateOdse.query(
        SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_NM_ORDERED,
        params,
        new BeanPropertyRowMapper<>(CodeValueGeneral.class));
  }

  public List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code) {
    MapSqlParameterSource params =
        new MapSqlParameterSource().addValue(CODE_SET_NM_JAVA, codeSetNm).addValue("code", code);
    return jdbcTemplateOdse.query(
        SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_AND_CODE,
        params,
        new BeanPropertyRowMapper<>(CodeValueGeneral.class));
  }
}
