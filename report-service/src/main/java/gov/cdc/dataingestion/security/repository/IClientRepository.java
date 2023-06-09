package gov.cdc.dataingestion.security.repository;

import gov.cdc.dataingestion.security.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByUsername(String username);

}
