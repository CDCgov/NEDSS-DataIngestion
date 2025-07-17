package gov.cdc.nbs.deduplication.auth.authentication;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionResolver {
  static final String QUERY = """
      select
              (CAST(100000 as bigint) * [jurisdiction].[nbs_uid]) + [program_area].[nbs_uid]  as [oid]
      from     auth_user [user]
              join auth_user_role [role] on
                      [role].auth_user_uid=[user].auth_user_uid

              join auth_perm_set [set] on
                      [role].auth_perm_set_uid=[set].auth_perm_set_uid

              join auth_bus_obj_rt [object_right] on
                      [object_right].auth_perm_set_uid=[set].auth_perm_set_uid

              join auth_bus_obj_type [object_type] on
                      [object_right].auth_bus_obj_type_uid=[object_type].auth_bus_obj_type_uid

              join auth_bus_op_rt [operation_right] on
                      [operation_right].auth_bus_obj_rt_uid=[object_right].auth_bus_obj_rt_uid

              join auth_bus_op_type [operation_type] on
                      [operation_type].auth_bus_op_type_uid=[operation_right].auth_bus_op_type_uid

              join NBS_SRTE..Program_area_code [program_area] on
                      [program_area].[prog_area_cd] = [role].prog_area_cd

              join NBS_SRTE..Jurisdiction_code [jurisdiction] on
                      [jurisdiction].code =   case
                                                      when [role].jurisdiction_cd = 'ALL'
                                                      then [jurisdiction].code
                                                      else [role].jurisdiction_cd
                                              end
      where   [user].user_id = :username
          and [operation_type].bus_op_nm = :operation
          and [object_type].bus_obj_nm  = :object
      """;

  final JdbcClient client;

  public PermissionResolver(@Qualifier("nbsJdbcClient") final JdbcClient client) {
    this.client = client;
  }

  // Get all program area + jurisdiction oid's the user has access to for the
  // provided operation and object
  public List<Long> resolve(final String operation, final String object) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

    return client.sql(QUERY)
        .param("username", username)
        .param("operation", operation)
        .param("object", object)
        .query(Long.class)
        .list();
  }
}
