package integration.repositories;

import integration.SpringBootApplicationTest;
import jakarta.transaction.Transactional;
import no.cerca.entities.Service;
import no.cerca.repositories.ServiceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by jadae on 06.04.2025
 */
@Transactional
public class ServiceRepositoryTest extends SpringBootApplicationTest {

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    @DisplayName("Поиск услуги по externalServiceId")
    void testFindByExternalServiceId() {
        Optional<Service> service = serviceRepository.findByExternalServiceId(4001L);
        assertTrue(service.isPresent());
        assertEquals(4001L, service.get().getExternalServiceId());
    }

    @Test
    @DisplayName("Услуга не найдена по несуществующему externalServiceId")
    void testFindByExternalServiceIdNotFound() {
        Optional<Service> service = serviceRepository.findByExternalServiceId(9999L);
        assertFalse(service.isPresent());
    }

    @Test
    @DisplayName("Создание новой услуги")
    void testCreateService() {
        Service newService = new Service();
        newService.setTitle("New Service");
        newService.setWeight(1);
        newService.setSeanceLength(2);
        newService.setExternalServiceId(5001L);

        Service savedService = serviceRepository.save(newService);

        assertNotNull(savedService.getServiceInnerId(), "ID услуги не должен быть null");
        assertEquals("New Service", savedService.getTitle(), "Название услуги должно совпадать");
    }

    @Test
    @DisplayName("Обновление данных услуги")
    void testUpdateService() {
        Service existingService = serviceRepository.findByExternalServiceId(4001L)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        existingService.setTitle("Updated Service");
        Service updatedService = serviceRepository.save(existingService);

        assertEquals("Updated Service", updatedService.getTitle(), "Название услуги должно быть обновлено");
    }

    @Test
    @DisplayName("Удаление услуги")
    void testDeleteService() {
        Service serviceToDelete = new Service();
        serviceToDelete.setTitle("Service to Delete");
        serviceToDelete.setWeight(2);
        serviceToDelete.setSeanceLength(45);
        serviceToDelete.setExternalServiceId(6001L);

        Service savedService = serviceRepository.save(serviceToDelete);
        Long serviceId = savedService.getExternalServiceId();

        serviceRepository.delete(savedService);

        Optional<Service> deletedService = serviceRepository.findById(serviceId);
        assertFalse(deletedService.isPresent(), "Услуга должна быть удалена");
    }
}
