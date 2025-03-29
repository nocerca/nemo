package no.cerca.services;

import no.cerca.entities.Record;
import no.cerca.repositories.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by jadae on 12.03.2025
 */
@Service
public class RecordService {
    @Autowired
    private RecordRepository recordRepository;

    public List<Record> getAllRecordsForClient(Long clientId) {
        return recordRepository.findByClientId(clientId);
    }

    public List<Record> getRecordsForNextHour(Long clientId) {
        Instant start = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = start.plus(1, ChronoUnit.HOURS);
        return recordRepository.findByClientIdAndDatetimeBetween(clientId, start, end);
    }

    public Record getCurrentRecord(Long clientId) {
        Instant start = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().minus(10, ChronoUnit.MINUTES);
        Instant end = start.plus(10, ChronoUnit.SECONDS);
        return recordRepository.findFirstByClientIdAndDatetimeBeforeAndEndTimeAfterOrderByDatetimeAsc(clientId, start, end).orElse(null);
    }

    public Record getNextRecord(Long clientId) {
        Instant now = Instant.now();
        return recordRepository.findFirstByClientIdAndDatetimeAfterOrderByDatetimeAsc(clientId, now).orElse(null);
    }

    public List<Record> getRecordsForToday(Long clientId) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant startOfDay = LocalDate.now().atStartOfDay(zoneId).toInstant();
        Instant endOfDay = LocalDate.now().atTime(LocalTime.MAX).atZone(zoneId).toInstant();

        return recordRepository.findByClientIdAndDatetimeBetween(clientId, startOfDay, endOfDay);
    }

    public void updateRecordStatus(Long recordId, boolean deleted) {
        Optional<Record> recordOpt = recordRepository.findById(recordId);
        recordOpt.ifPresent(record -> {
            record.setDeleted(deleted);
            recordRepository.save(record);
        });

    }
}