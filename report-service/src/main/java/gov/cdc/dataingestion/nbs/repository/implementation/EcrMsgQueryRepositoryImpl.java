package gov.cdc.dataingestion.nbs.repository.implementation;

import gov.cdc.dataingestion.nbs.repository.EcrMsgQueryRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgContainerDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EcrMsgQueryRepositoryImpl implements EcrMsgQueryRepository {
    @PersistenceContext(unitName = "nbs")
    private EntityManager entityManager;

    public EcrMsgContainerDto FetchMsgContainerForApplicableEcr() {
        String queryString = loadSqlFromFile("ecr_msg_container.sql");
        Query query = entityManager.createNativeQuery(queryString);

        EcrMsgContainerDto ecrMsgContainerDto = new EcrMsgContainerDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            ecrMsgContainerDto.setMsgContainerUid(((Number)val[0]).intValue());
            ecrMsgContainerDto.setInvLocalId(String.valueOf(nullToString(val[1])));
            ecrMsgContainerDto.setNbsInterfaceUid(((Number)val[2]).intValue());
            ecrMsgContainerDto.setReceivingSystem(String.valueOf(nullToString(val[3])));
            ecrMsgContainerDto.setOngoingCase(String.valueOf(nullToString(val[4])));
            ecrMsgContainerDto.setVersionCtrNbr(((Number)val[5]).intValue());
            ecrMsgContainerDto.setDataMigrationStatus(((Number)val[6]).intValue());

            return ecrMsgContainerDto;

        }
        return null;
    }

    public void UpdateMatchEcrRecordForProcessing(Integer containerUid) {
        String queryString = loadSqlFromFile("ecr_msg_container_update_match_record.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("customValue", containerUid);
        query.executeUpdate();
    }

    private String loadSqlFromFile(String filename) {
        try (InputStream is = getClass().getResourceAsStream("/queries/ecr/" + filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SQL file: " + filename, e);
        }
    }

    public static String nullToString(Object obj) {
        return obj != null ? String.valueOf(obj) : "";
    }
}
