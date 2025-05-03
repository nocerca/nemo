package no.cerca.repositories;

import no.cerca.entities.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Created by jadae on 12.03.2025
 */
@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findByRecordExternalId(Long recordExternalId);
    List<Record> findByDatetimeAfterAndDatetimeBefore(Instant start, Instant end);
    List<Record> findFirstByUpdatedAfter(Instant dateBefore);
    List<Record> findByUpdatedAfter(Instant updated);
    Optional<Record> findFirstByDatetimeAfterOrderByDatetimeAsc(Instant now);
    Optional<Record> findFirstByDatetimeAfterAndDatetimeBeforeOrderByDatetimeAsc(Instant start, Instant end);
}