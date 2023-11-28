package gov.cdc.dataingestion.registration.repository;


import gov.cdc.dataingestion.registration.model.RegisterClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IClientRegisterRepository extends JpaRepository<RegisterClient, String> {
    Optional<RegisterClient> findByUsername(String username);

}
