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

    public List<Record> getAllRecords() {
        return recordRepository.findAll();
    }

    public List<Record> getRecordsForNextHour() {
        Instant start = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = start.plus(1, ChronoUnit.HOURS);
        return recordRepository.findByDatetimeAfterAndDatetimeBefore(start, end);
    }

    public Record getCurrentRecord() {
        Instant start = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().minus(10, ChronoUnit.MINUTES);
        Instant end = start.plus(10, ChronoUnit.SECONDS);
        return recordRepository.findFirstByDatetimeAfterAndDatetimeBeforeOrderByDatetimeAsc(start, end).orElse(null);
    }

    public Record getNextRecord() {
        Instant now = Instant.now();
        return recordRepository.findFirstByDatetimeAfterOrderByDatetimeAsc(now).orElse(null);
    }

    public List<Record> getRecordsForToday() {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant startOfDay = LocalDate.now().atStartOfDay(zoneId).toInstant();
        Instant endOfDay = LocalDate.now().atTime(LocalTime.MAX).atZone(zoneId).toInstant();

        return recordRepository.findByDatetimeAfterAndDatetimeBefore(startOfDay, endOfDay);
    }

    public Boolean wasUpdatedLessThan15minAgo() {
        Instant fifteenMinutesAgo = Instant.now().minus(15, ChronoUnit.MINUTES);
        List<Record> records = recordRepository.findByUpdatedAfter(fifteenMinutesAgo);
        return !records.isEmpty();
    }

    public Record createOrUpdateRecordFromDTO(RecordDTO dto) {
        Company company = companyRepository.findByCompanyExternalId(dto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        Staff staff = staffRepository.findByExternalStaffId(dto.getStaff().getId())
                .orElseGet(() -> staffRepository.save(new Staff(dto.getStaff())));

        Client client = clientRepository.findByClientExternalId(dto.getClient().getId())
                .orElseGet(() -> clientRepository.save(new Client(dto.getClient())));

        Set<no.cerca.entities.Service> services = dto.getServices().stream()
                .map(serviceDto -> serviceRepository.findByExternalServiceId(serviceDto.getId())
                        .orElseGet(() -> serviceRepository.save(new no.cerca.entities.Service(serviceDto))))
                .collect(Collectors.toSet());

        Record record = recordRepository.findByRecordExternalId(dto.getId())
                .map(existingRecord -> {
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

        return recordRepository.save(record);
    }

}