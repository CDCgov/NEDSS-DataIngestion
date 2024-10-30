package gov.cdc.dataprocessing.repository.nbs.srte.repository.custom;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LabResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataprocessing.utilities.DataParserForSql.parseValue;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class SrteCustomRepositoryImpl implements SrteCustomRepository{
    @PersistenceContext(unitName = "srte")
    private EntityManager entityManager;


    //THIS ONE IS  FOR CACHING
    public List<LabResult> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd()  {
        String codeSql =
                "Select  Lab_result.LAB_RESULT_CD , lab_result_desc_txt  FROM "
                        + " Lab_result Lab_result, "
                        + " Lab_coding_system Lab_coding_system WHERE "+
                        " Lab_coding_system.laboratory_id = 'DEFAULT' and "+
                        " Lab_result.organism_name_ind = 'Y'";

        Query query = entityManager.createNativeQuery(codeSql);

        List<LabResult> lst = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                int i = 0;
                LabResult labResult = new LabResult();
                labResult.setLabResultCd(parseValue(item[i], String.class));
                labResult.setLabResultDescTxt(parseValue(item[++i], String.class));
                lst.add(labResult);
            }

        }
        return lst;
    }

}
