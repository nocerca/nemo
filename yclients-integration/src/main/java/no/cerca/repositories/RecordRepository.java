package no.cerca.repositories;

import no.cerca.entities.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by jadae on 12.03.2025
 */
@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByClientId(Long clientId);
    List<Record> findByClientIdAndDatetimeBetween(Long clientId, Instant start, Instant end);
    List<Record> getRecordsByClientIdAndUpdatedAfter(Long clientId, LocalDateTime dateBefore);
    Optional<Record> findFirstByClientIdAndDatetimeBeforeAndEndTimeAfterOrderByDatetimeAsc(Long clientId, Instant now, Instant nowEnd);
    Optional<Record> findFirstByClientIdAndDatetimeAfterOrderByDatetimeAsc(Long clientId, Instant now);
}