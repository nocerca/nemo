package integration.services;

import integration.SpringBootApplicationTest;
import jakarta.transaction.Transactional;
import no.cerca.dtos.basic.ClientDTO;
import no.cerca.dtos.basic.RecordDTO;
import no.cerca.dtos.basic.ServiceDTO;
import no.cerca.dtos.basic.StaffDTO;
import no.cerca.entities.Record;
import no.cerca.services.RecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by jadae on 07.04.2025
 */
@Transactional
class RecordServiceTest extends SpringBootApplicationTest {

    @Autowired
    private RecordService recordService;

    @Test
    @DisplayName("Получение всех записей клиента")
    void testGetAllRecordsForClient() {
        List<Record> records = recordService.getAllRecords();
        assertFalse(records.isEmpty());
        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("Получение записей клиента на ближайший час")
    void testGetRecordsForNextHour() {
        List<Record> records = recordService.getRecordsForNextHour();
        assertNotNull(records);
    }

    @Test
    @DisplayName("Получение текущей записи клиента")
    void testGetCurrentRecord() {
        Record record = recordService.getCurrentRecord();
        assertNull(record);
    }

    @Test
    @DisplayName("Получение следующей записи клиента")
    void testGetNextRecord() {
        RecordDTO dto = new RecordDTO(
                9997L,
                1001L,
                2001L,
                List.of(new ServiceDTO(4001L, "Haircut")),
                new StaffDTO(2001L, "John Smith", "Hairdresser"),
                new ClientDTO(3001L, "Alice", "Brown", "Ivanovna", "+1234567890", "alice.brown@example.com", false),
                LocalDate.now().toString(),
                OffsetDateTime.now().plusHours(2),
                OffsetDateTime.now(),
                "Запись в будущем",
                30,
                30,
                false
        );

        recordService.createOrUpdateRecordFromDTO(dto);

        Record record = recordService.getNextRecord();
        assertNotNull(record);
        assertEquals(3001L, record.getClient().getClientExternalId());
    }

    @Test
    @DisplayName("Получение записей клиента за сегодня")
    void testGetRecordsForToday() {
        RecordDTO dto = new RecordDTO(
                9998L,
                1001L,
                2001L,
                List.of(new ServiceDTO(4001L, "Haircut")),
                new StaffDTO(2001L, "John Smith", "Hairdresser"),
                new ClientDTO(3001L, "Alice", "Brown", "Ivanovna", "+1234567890", "alice.brown@example.com", false),
                LocalDate.now().toString(),
                OffsetDateTime.now(),
                OffsetDateTime.now().minusHours(1),
                "Запись на сегодня",
                30,
                30,
                false
        );

        recordService.createOrUpdateRecordFromDTO(dto);

        List<Record> records = recordService.getRecordsForToday();
        assertFalse(records.isEmpty());
    }

    @Test
    @DisplayName("Проверка обновления записи менее 15 минут назад")
    void testWasUpdatedLessThan15minAgo() {
        Boolean updated = recordService.wasUpdatedLessThan15minAgo();
        assertFalse(updated);
    }

    @Test
    @DisplayName("Создание новой записи из DTO")
    void testCreateRecordFromDTO() {
        RecordDTO dto = new RecordDTO(
                9999L,
                1001L,
                2001L,
                List.of(new ServiceDTO(4001L, "Haircut")),
                new StaffDTO(2001L, "John Smith", "Hairdresser"),
                new ClientDTO(3001L, "Alice", "Brown", "Ivanovna", "+1234567890", "alice.brown@example.com", false),
                "2025-04-08",
                OffsetDateTime.now().plusHours(1),
                OffsetDateTime.now(),
                "New record",
                30,
                30,
                false
        );

        Record record = recordService.createOrUpdateRecordFromDTO(dto);
        assertNotNull(record);
        assertEquals("New record", record.getComment());
    }

    @Test
    @DisplayName("Обновление существующей записи из DTO")
    void testUpdateRecordFromDTO() {
        RecordDTO dto = new RecordDTO(
                5001L,
                1001L,
                2001L,
                List.of(new ServiceDTO(4001L, "Haircut")),
                new StaffDTO(2001L, "John Smith", "Hairdresser"),
                new ClientDTO(3001L, "Alice", "Brown", "Ivanovna", "+1234567890", "alice.brown@example.com", false),
                "2025-04-05",
                OffsetDateTime.parse("2025-04-05T10:00:00Z"),
                OffsetDateTime.parse("2025-04-04T15:00:00Z"),
                "Updated comment",
                30,
                30,
                false
        );

        Record updated = recordService.createOrUpdateRecordFromDTO(dto);
        assertNotNull(updated);
        assertEquals("Updated comment", updated.getComment());
    }

    @Test
    @DisplayName("Создание записи с новым клиентом")
    void testCreateRecordWithNewClient() {
        ClientDTO newClient = new ClientDTO(9999L, "New", "Client", "", "+1230000000", "new@example.com", false);

        RecordDTO dto = new RecordDTO(
                8001L,
                1001L,
                2001L,
                List.of(new ServiceDTO(4001L, "Haircut")),
                new StaffDTO(2001L, "John Smith", "Hairdresser"),
                newClient,
                LocalDate.now().toString(),
                OffsetDateTime.now().plusHours(1),
                OffsetDateTime.now(),
                "Запись с новым клиентом",
                30,
                30,
                false
        );

        Record record = recordService.createOrUpdateRecordFromDTO(dto);
        assertNotNull(record);
        assertEquals("new@example.com", record.getClient().getEmail());
    }

    @Test
    @DisplayName("Создание записи с новым сотрудником")
    void testCreateRecordWithNewStaff() {
        StaffDTO newStaff = new StaffDTO(9998L, "New Staff", "Barber");

        RecordDTO dto = new RecordDTO(
                8002L,
                1001L,
                9998L,
                List.of(new ServiceDTO(4001L, "Haircut")),
                newStaff,
                new ClientDTO(3001L, "Alice", "Brown", "Ivanovna", "+1234567890", "alice.brown@example.com", false),
                LocalDate.now().toString(),
                OffsetDateTime.now().plusHours(2),
                OffsetDateTime.now(),
                "Запись с новым сотрудником",
                30,
                30,
                false
        );

        Record record = recordService.createOrUpdateRecordFromDTO(dto);
        assertNotNull(record);
        assertEquals("New Staff", record.getStaff().getName());
    }

    @Test
    @DisplayName("Создание записи с новой услугой")
    void testCreateRecordWithNewService() {
        ServiceDTO newService = new ServiceDTO(9997L, "Beard Trim");

        RecordDTO dto = new RecordDTO(
                8003L,
                1001L,
                2001L,
                List.of(newService),
                new StaffDTO(2001L, "John Smith", "Hairdresser"),
                new ClientDTO(3001L, "Alice", "Brown", "Ivanovna", "+1234567890", "alice.brown@example.com", false),
                LocalDate.now().toString(),
                OffsetDateTime.now().plusHours(3),
                OffsetDateTime.now(),
                "Запись с новой услугой",
                30,
                30,
                false
        );

        Record record = recordService.createOrUpdateRecordFromDTO(dto);
        assertNotNull(record);
        assertTrue(record.getServices().stream().anyMatch(s -> s.getTitle().equals("Beard Trim")));
    }

}

