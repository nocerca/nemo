package no.cerca.repositories;

import no.cerca.entities.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by jadae on 12.03.2025
 */
@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByClientId(Long clientId);
    List<Record> findByClientIdAndDatetimeBetween(Long clientId, LocalDateTime start, LocalDateTime end);
}