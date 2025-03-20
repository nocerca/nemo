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
        LocalDateTime now = LocalDateTime.now();
        return recordRepository.findByClientIdAndDatetimeBetween(clientId, now, now.plusHours(1));
    }

    public void updateRecordStatus(Long recordId, boolean deleted) {
        Optional<Record> recordOpt = recordRepository.findById(recordId);
        recordOpt.ifPresent(record -> {
            record.setDeleted(deleted);
            recordRepository.save(record);
        });

    }
}