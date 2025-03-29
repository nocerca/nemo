package no.cerca.repositories;

import no.cerca.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Created by jadae on 12.03.2025
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByPhone(String phone);
    Optional<Client> findByEmail(String email);
    Optional<Client> findByClientInnerId(Long id);
    Optional<Client> findById(Long id);
}
