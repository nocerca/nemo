package integration.repositories;

import static org.junit.jupiter.api.Assertions.*;

import integration.SpringBootApplicationTest;
import jakarta.transaction.Transactional;
import no.cerca.entities.Client;
import no.cerca.repositories.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * Created by jadae on 06.04.2025
 */
@Transactional
public class ClientRepositoryTest extends SpringBootApplicationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    @DisplayName("Поиск клиента по номеру телефона")
    void testFindByPhone() {
        Optional<Client> foundClient = clientRepository.findByPhone("+1234567890");
        assertTrue(foundClient.isPresent());
        assertEquals("+1234567890", foundClient.get().getPhone());
    }

    @Test
    @DisplayName("Поиск клиента по email")
    void testFindByEmail() {
        Optional<Client> foundClient = clientRepository.findByEmail("alice.brown@example.com");
        assertTrue(foundClient.isPresent());
        assertEquals("alice.brown@example.com", foundClient.get().getEmail());
    }

    @Test
    @DisplayName("Поиск клиента по clientInnerId")
    void testFindByClientInnerId() {
        Optional<Client> foundClient = clientRepository.findByClientInnerId(1L);
        assertTrue(foundClient.isPresent());
        assertEquals(1L, foundClient.get().getClientInnerId());
    }

    @Test
    @DisplayName("Поиск клиента по ID")
    void testFindById() {
        Optional<Client> foundClient = clientRepository.findByClientExternalId(3001L);
        assertTrue(foundClient.isPresent());
        assertEquals(3001L, foundClient.get().getClientExternalId());
    }

    @Test
    @DisplayName("Клиент не найден по несуществующему номеру телефона")
    void testFindByPhoneNotFound() {
        Optional<Client> foundClient = clientRepository.findByPhone("nonexistentPhone");
        assertFalse(foundClient.isPresent());
    }

    @Test
    @DisplayName("Клиент не найден по несуществующему email")
    void testFindByEmailNotFound() {
        Optional<Client> foundClient = clientRepository.findByEmail("nonexistentEmail@example.com");
        assertFalse(foundClient.isPresent());
    }

    @Test
    @DisplayName("Клиент не найден по несуществующему clientInnerId")
    void testFindByClientInnerIdNotFound() {
        Optional<Client> foundClient = clientRepository.findByClientInnerId(999L);
        assertFalse(foundClient.isPresent());
    }

    @Test
    @DisplayName("Создание нового клиента")
    void testCreateClient() {
        Client newClient = new Client();
        newClient.setPhone("+1122334455");
        newClient.setEmail("newclient@example.com");
        newClient.setClientExternalId(4L);
        newClient.setName("John");
        newClient.setSurname("Doe");

        Client savedClient = clientRepository.save(newClient);

        assertNotNull(savedClient.getClientExternalId(), "ID клиента не должен быть null");
        assertEquals(newClient.getPhone(), savedClient.getPhone(), "Телефон клиента должен совпадать");
    }

    @Test
    @DisplayName("Обновление клиента")
    void testUpdateClient() {
        Client existingClient = clientRepository.findByPhone("+1234567890")
                .orElseThrow(() -> new RuntimeException("Не найден клиент"));

        existingClient.setEmail("updatedemail@example.com");
        Client updatedClient = clientRepository.save(existingClient);

        assertEquals("updatedemail@example.com", updatedClient.getEmail(), "Email должен быть обновлен");
    }

    @Test
    @DisplayName("Удаление клиента")
    void testDeleteClient() {
        Client clientToDelete = new Client();
        clientToDelete.setPhone("+9988776655");
        clientToDelete.setEmail("deleteclient@example.com");
        clientToDelete.setClientExternalId(5L);
        clientToDelete.setName("Delete");
        clientToDelete.setSurname("Test");

        Client savedClient = clientRepository.save(clientToDelete);
        Long clientId = savedClient.getClientExternalId();

        clientRepository.delete(savedClient);

        Optional<Client> deletedClient = clientRepository.findById(clientId);
        assertFalse(deletedClient.isPresent(), "Клиент должен быть удален");
    }

}

