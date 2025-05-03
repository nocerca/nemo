package integration.repositories;

import integration.SpringBootApplicationTest;
import jakarta.transaction.Transactional;
import no.cerca.entities.Record;
import no.cerca.repositories.ClientRepository;
import no.cerca.repositories.CompanyRepository;
import no.cerca.repositories.RecordRepository;
import no.cerca.repositories.StaffRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class RecordRepositoryTest extends SpringBootApplicationTest {

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Получение всех записей")
    void testFindAll() {
        List<Record> records = recordRepository.findAll();

        assertFalse(records.isEmpty());
        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("Поиск записей по ID клиента")
    void testFindByClient_Id() {
        List<Record> records = recordRepository.findAll();
        assertFalse(records.isEmpty());
    }

    @Test
    @DisplayName("Поиск записей по ID клиента в заданном диапазоне времени")
    void testFindByClient_IdAndDatetimeAfterAndDatetimeBefore() {
        Instant start = Instant.parse("2025-04-05T09:00:00Z");
        Instant end = Instant.parse("2025-04-06T12:00:00Z");
        List<Record> records = recordRepository.findByDatetimeAfterAndDatetimeBefore(start, end);
        assertFalse(records.isEmpty());
    }

    @Test
    @DisplayName("Поиск первой записи клиента после указанной даты")
    void testFindFirstByClient_IdAndDatetimeAfterOrderByDatetimeAsc() {
        Instant after = Instant.parse("2025-04-05T09:00:00Z");
        Optional<Record> record = recordRepository.findFirstByDatetimeAfterOrderByDatetimeAsc(after);
        assertTrue(record.isPresent());
    }

    @Test
    @DisplayName("Поиск записей по ID клиента в диапазоне времени с учётом даты обновления")
    void testFindByClient_IdAndDatetimeAfterAndDatetimeBeforeWithUpdated() {
        Instant start = Instant.parse("2025-04-05T09:00:00Z");
        Instant end = Instant.parse("2025-04-06T12:00:00Z");

        Instant updatedAfter = Instant.parse("2025-04-04T16:00:00Z");

        List<Record> records = recordRepository.findByDatetimeAfterAndDatetimeBefore(start, end);

        assertFalse(records.isEmpty(), "Записи в заданном диапазоне времени не найдены");

        records = records.stream()
                .filter(record -> record.getUpdated().isBefore(updatedAfter))
                .collect(Collectors.toList());

        assertFalse(records.isEmpty(), "После фильтрации записи с нужной датой обновления не найдены");
    }

    @Test
    @DisplayName("Поиск записей по ID клиента и дате обновления")
    void testFindByClient_IdAndUpdatedAfter() {
        Instant past = Instant.parse("2025-04-04T15:00:00Z");
        List<Record> records = recordRepository.findFirstByUpdatedAfter(past);
        assertFalse(records.isEmpty(), "Записи после указанной даты обновления не найдены");
    }

    @Test
    @DisplayName("Поиск записей с неверной датой обновления")
    void testFindByClient_IdAndUpdatedAfterWithInvalidDate() {
        Instant futureDate = Instant.parse("2025-04-10T15:30:00Z");
        List<Record> records = recordRepository.findFirstByUpdatedAfter(futureDate);
        assertTrue(records.isEmpty(), "Записей не должно быть найдено после будущей даты");
    }

    @Test
    @DisplayName("Поиск первой записи клиента в заданном интервале времени")
    void testFindFirstByClient_IdAndDatetimeAfterAndDatetimeBeforeOrderByDatetimeAsc() {
        Instant start = Instant.parse("2025-04-05T09:00:00Z");
        Instant end = Instant.parse("2025-04-06T12:00:00Z");
        Optional<Record> record = recordRepository.findFirstByDatetimeAfterAndDatetimeBeforeOrderByDatetimeAsc(start, end);
        assertTrue(record.isPresent(), "Первая запись клиента должна быть найдена");
        assertEquals(5001L, record.get().getRecordExternalId(), "ID первой записи клиента должен быть 5001");
    }

    @Test
    @DisplayName("Поиск записей с неверным диапазоном времени")
    void testFindByClient_IdAndDatetimeAfterAndDatetimeBeforeWithInvalidTimeRange() {
        Instant start = Instant.parse("2025-04-06T12:00:00Z");
        Instant end = Instant.parse("2025-04-05T09:00:00Z");
        List<Record> records = recordRepository.findByDatetimeAfterAndDatetimeBefore(start, end);
        assertTrue(records.isEmpty(), "Записей не должно быть найдено при неверном диапазоне времени");
    }

    @Test
    @DisplayName("Поиск первой записи для клиента, если не найдено подходящих записей")
    void testFindFirstByClient_IdAndDatetimeAfterOrderByDatetimeAscForNonExistentRecord() {
        Instant after = Instant.parse("2025-04-07T09:00:00Z");
        Optional<Record> record = recordRepository.findFirstByDatetimeAfterOrderByDatetimeAsc(after);
        assertFalse(record.isPresent(), "Запись не должна быть найдена для клиента в этом интервале времени");
    }

    @Test
    @DisplayName("Создание новой записи")
    void testCreateRecord() {
        Record newRecord = new Record();
        newRecord.setRecordExternalId(1001L);
        newRecord.setStaff(staffRepository.findByExternalStaffId(2001L).orElseThrow());
        newRecord.setClient(clientRepository.findByClientExternalId(3001L).orElseThrow());
        newRecord.setCompany(companyRepository.findByCompanyExternalId(1001L).orElseThrow());
        newRecord.setDatetime(Instant.parse("2025-04-07T10:00:00Z"));
        newRecord.setCreateDate(Instant.parse("2025-04-07T10:00:00Z"));
        newRecord.setDate(LocalDateTime.now());
        newRecord.setLength(30);
        newRecord.setComment("New record comment");

        Record savedRecord = recordRepository.save(newRecord);

        assertNotNull(savedRecord.getRecordInnerId(), "ID записи не должен быть null");
        assertEquals(newRecord.getClient().getClientExternalId(), savedRecord.getClient().getClientExternalId(), "ID клиента должен совпадать");
    }

    @Test
    @DisplayName("Обновление записи")
    void testUpdateRecord() {
        Record existingRecord = recordRepository.findByRecordExternalId(5001L)
                .orElseThrow(() -> new RuntimeException("Не найдена запись"));

        existingRecord.setComment("Updated record comment");
        Record updatedRecord = recordRepository.save(existingRecord);

        assertEquals("Updated record comment", updatedRecord.getComment(), "Комментарий должен быть обновлен");
    }

    @Test
    @DisplayName("Удаление записи")
    void testDeleteRecord() {
        Record recordToDelete = new Record();
        recordToDelete.setRecordExternalId(1001L);
        recordToDelete.setStaff(staffRepository.findByExternalStaffId(2001L).orElseThrow());
        recordToDelete.setClient(clientRepository.findByClientExternalId(3001L).orElseThrow());
        recordToDelete.setCompany(companyRepository.findByCompanyExternalId(1001L).orElseThrow());
        recordToDelete.setDatetime(Instant.parse("2025-04-08T10:00:00Z"));
        recordToDelete.setCreateDate(Instant.parse("2025-04-08T10:00:00Z"));
        recordToDelete.setDate(LocalDateTime.now());
        recordToDelete.setLength(30);
        recordToDelete.setComment("Record to be deleted");

        Record savedRecord = recordRepository.save(recordToDelete);
        Long recordId = savedRecord.getRecordExternalId();

        recordRepository.delete(savedRecord);

        Optional<Record> deletedRecord = recordRepository.findById(recordId);
        assertFalse(deletedRecord.isPresent(), "Запись должна быть удалена");
    }


}
