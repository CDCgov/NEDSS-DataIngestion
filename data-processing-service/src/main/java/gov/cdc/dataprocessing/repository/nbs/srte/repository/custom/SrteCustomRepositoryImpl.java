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
