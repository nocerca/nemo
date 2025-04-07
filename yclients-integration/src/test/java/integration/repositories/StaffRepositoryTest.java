package integration.repositories;

import integration.SpringBootApplicationTest;
import jakarta.transaction.Transactional;
import no.cerca.entities.Staff;
import no.cerca.repositories.StaffRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by jadae on 06.04.2025
 */
@Transactional
public class StaffRepositoryTest extends SpringBootApplicationTest {

    @Autowired
    private StaffRepository staffRepository;

    @Test
    @DisplayName("Поиск сотрудника по externalStaffId")
    void testFindByExternalStaffId() {
        Optional<Staff> staff = staffRepository.findByExternalStaffId(2001L);
        assertTrue(staff.isPresent());
        assertEquals(2001L, staff.get().getExternalStaffId());
    }

    @Test
    @DisplayName("Сотрудник не найден по несуществующему externalStaffId")
    void testFindByExternalStaffIdNotFound() {
        Optional<Staff> staff = staffRepository.findByExternalStaffId(9999L);
        assertFalse(staff.isPresent());
    }

    @Test
    @DisplayName("Создание нового сотрудника")
    void testCreateStaff() {
        Staff newStaff = new Staff();
        newStaff.setName("John Doe");
        newStaff.setSpecialization("Manager");
        newStaff.setExternalStaffId(3001L);

        Staff savedStaff = staffRepository.save(newStaff);

        assertNotNull(savedStaff.getServiceInnerId(), "ID сотрудника не должен быть null");
        assertEquals("John Doe", savedStaff.getName(), "Имя сотрудника должно совпадать");
    }

    @Test
    @DisplayName("Обновление данных сотрудника")
    void testUpdateStaff() {
        Staff existingStaff = staffRepository.findByExternalStaffId(2001L)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        existingStaff.setSpecialization("Hairdresser");
        Staff updatedStaff = staffRepository.save(existingStaff);

        assertEquals("Hairdresser", updatedStaff.getSpecialization(), "Специализация сотрудника должна быть обновлена");
    }

    @Test
    @DisplayName("Удаление сотрудника")
    void testDeleteStaff() {
        Staff staffToDelete = new Staff();
        staffToDelete.setName("Mark Smith");
        staffToDelete.setSpecialization("Barber");
        staffToDelete.setExternalStaffId(4001L);

        Staff savedStaff = staffRepository.save(staffToDelete);
        Long staffId = savedStaff.getExternalStaffId();

        staffRepository.delete(savedStaff);

        Optional<Staff> deletedStaff = staffRepository.findById(staffId);
        assertFalse(deletedStaff.isPresent(), "Сотрудник должен быть удален");
    }
}
