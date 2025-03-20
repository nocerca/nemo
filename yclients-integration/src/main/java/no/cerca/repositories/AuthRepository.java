package no.cerca.repositories;

import no.cerca.entities.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by jadae on 19.03.2025
 */
public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> getAuthByLogin(String login);
}
