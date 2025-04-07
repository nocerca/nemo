package no.cerca.services;

import jakarta.persistence.EntityNotFoundException;
import no.cerca.dtos.basic.RecordDTO;
import no.cerca.entities.Client;
import no.cerca.entities.Company;
import no.cerca.entities.Record;
import no.cerca.entities.Staff;
import no.cerca.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by jadae on 12.03.2025
 */
@Service
public class RecordService {
    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private StaffRepository staffRepository;

    public List<Record> getAllRecordsForClient(Long clientId) {
        return recordRepository.findByClient_ClientExternalId(clientId);
    }

    public List<Record> getRecordsForNextHour(Long clientId) {
        Instant start = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = start.plus(1, ChronoUnit.HOURS);
        return recordRepository.findByClient_ClientExternalIdAndDatetimeAfterAndDatetimeBefore(clientId, start, end);
    }

    public Record getCurrentRecord(Long clientId) {
        Instant start = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().minus(10, ChronoUnit.MINUTES);
        Instant end = start.plus(10, ChronoUnit.SECONDS);
        return recordRepository.findFirstByClient_ClientExternalIdAndDatetimeAfterAndDatetimeBeforeOrderByDatetimeAsc(clientId, start, end).orElse(null);
    }

    public Record getNextRecord(Long clientId) {
        Instant now = Instant.now();
        return recordRepository.findFirstByClient_ClientExternalIdAndDatetimeAfterOrderByDatetimeAsc(clientId, now).orElse(null);
    }

    public List<Record> getRecordsForToday(Long clientId) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant startOfDay = LocalDate.now().atStartOfDay(zoneId).toInstant();
        Instant endOfDay = LocalDate.now().atTime(LocalTime.MAX).atZone(zoneId).toInstant();

    }

    public void updateRecordStatus(Long recordId, boolean deleted) {
        Optional<Record> recordOpt = recordRepository.findById(recordId);
        recordOpt.ifPresent(record -> {
            record.setDeleted(deleted);
            recordRepository.save(record);
        });

        return recordRepository.findByClient_ClientExternalIdAndDatetimeAfterAndDatetimeBefore(clientId, startOfDay, endOfDay);
    }

    public Boolean wasUpdatedLessThan15minAgo(Long clientId) {
        Instant fifteenMinutesAgo = Instant.now().minus(15, ChronoUnit.MINUTES);
        List<Record> records = recordRepository.findByClient_ClientExternalIdAndUpdatedAfter(clientId, fifteenMinutesAgo);
        return !records.isEmpty();
    }

    public Record createOrUpdateRecordFromDTO(RecordDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        Staff staff = staffRepository.findById(dto.getStaff().getId())
                .orElseGet(() -> new Staff(dto.getStaff()));

        Client client = clientRepository.findById(dto.getClient().getId())
                .orElseGet(() -> new Client(dto.getClient()));

        Set<no.cerca.entities.Service> services = dto.getServices().stream()
                .map(serviceDto -> serviceRepository.findById(serviceDto.getId())
                        .orElseGet(() -> new no.cerca.entities.Service(serviceDto)))
                .collect(Collectors.toSet());

        return recordRepository.findById(dto.getId())
                .map(existingRecord -> {
                    // Обновляем только изменившиеся поля
                    existingRecord.setCompany(company);
                    existingRecord.setStaff(staff);
                    existingRecord.setClient(client);
                    existingRecord.setServices(services);
                    existingRecord.setDatetime(dto.getDatetime().toInstant());
                    existingRecord.setCreateDate(dto.getCreateDate().toInstant());
                    existingRecord.setComment(dto.getComment());
                    existingRecord.setLength(dto.getLength());
                    existingRecord.setDeleted(dto.isDeleted());
                    return existingRecord;
                })
                .orElseGet(() -> new Record(dto, company, staff, client, services));
    }

}