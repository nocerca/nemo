package no.cerca.repositories;

import no.cerca.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jadae on 12.03.2025
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {}