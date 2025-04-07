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
    List<Record> findByClient_ClientExternalId(Long clientExternalId);
    List<Record> findByClient_ClientExternalIdAndDatetimeAfterAndDatetimeBefore(Long clientExternalId, Instant start, Instant end);
    List<Record> findByClient_ClientExternalIdAndUpdatedAfter(Long clientExternalId, Instant dateBefore);
    Optional<Record> findFirstByClient_ClientExternalIdAndDatetimeAfterOrderByDatetimeAsc(Long clientExternalId, Instant now);
    Optional<Record> findFirstByClient_ClientExternalIdAndDatetimeAfterAndDatetimeBeforeOrderByDatetimeAsc(Long clientExternalId, Instant start, Instant end);
}