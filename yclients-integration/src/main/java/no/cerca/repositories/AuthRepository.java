package no.cerca.repositories;

import no.cerca.entities.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by jadae on 19.03.2025
 */
@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByLogin(String login);
}
