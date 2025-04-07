package integration.repositories;

import static org.junit.jupiter.api.Assertions.*;

import integration.SpringBootApplicationTest;
import jakarta.transaction.Transactional;
import no.cerca.entities.Auth;
import no.cerca.repositories.AuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * Created by jadae on 05.04.2025
 */
@Transactional
public class AuthRepositoryTest extends SpringBootApplicationTest {

    @Autowired
    private AuthRepository authRepository;

    @Test
    @DisplayName("Поиск авторизации по логину (существующий логин)")
    void testFindByLogin_ExistingLogin() {
        String login = "admin1";
        Optional<Auth> auth = authRepository.findByLogin(login);

        assertTrue(auth.isPresent(), "Авторизация с таким логином должна быть найдена");
        assertEquals(login, auth.get().getLogin(), "Логин должен совпадать");
    }

    @Test
    @DisplayName("Поиск авторизации по логину (несуществующий логин)")
    void testFindByLogin_NonExistingLogin() {
        String login = "nonexistentUser";
        Optional<Auth> auth = authRepository.findByLogin(login);

        assertFalse(auth.isPresent(), "Авторизация с таким логином не должна быть найдена");
    }

    @Test
    @DisplayName("Поиск авторизации по логину (пустой логин)")
    void testFindByLogin_EmptyLogin() {
        String login = "";
        Optional<Auth> auth = authRepository.findByLogin(login);

        assertFalse(auth.isPresent(), "Авторизация с пустым логином не должна быть найдена");
    }

    @Test
    @DisplayName("Поиск авторизации по логину (null логин)")
    void testFindByLogin_NullLogin() {
        Optional<Auth> auth = authRepository.findByLogin(null);

        assertFalse(auth.isPresent(), "Авторизация с null логином не должна быть найдена");
    }

    @Test
    @DisplayName("Создание новой авторизации")
    void testCreateAuth() {
        Auth newAuth = new Auth();
        newAuth.setLogin("newUser");
        newAuth.setUserToken("user-token");
        newAuth.setPassword("newPassword123");
        newAuth.setEmail("newuser@example.com");
        newAuth.setPhone("+1234567890");
        newAuth.setApproved(true);

        Auth savedAuth = authRepository.save(newAuth);

        assertNotNull(savedAuth.getId(), "ID авторизации не должен быть null");
        assertEquals(newAuth.getLogin(), savedAuth.getLogin(), "Логин должен совпадать");
    }

    @Test
    @DisplayName("Обновление авторизации")
    void testUpdateAuth() {
        Auth existingAuth = authRepository.findByLogin("admin1")
                .orElseThrow(() -> new RuntimeException("Не найдена авторизация"));

        existingAuth.setEmail("updatedemail@example.com");
        Auth updatedAuth = authRepository.save(existingAuth);

        assertEquals("updatedemail@example.com", updatedAuth.getEmail(), "Email должен быть обновлен");
    }

    @Test
    @DisplayName("Удаление авторизации")
    void testDeleteAuth() {
        Auth authToDelete = new Auth();
        authToDelete.setLogin("deleteUser");
        authToDelete.setUserToken("user-token");
        authToDelete.setPassword("deletePassword");
        authToDelete.setEmail("deleteuser@example.com");
        authToDelete.setPhone("+0987654321");
        authToDelete.setApproved(false);

        Auth savedAuth = authRepository.save(authToDelete);
        Long authId = savedAuth.getId();

        authRepository.delete(savedAuth);

        Optional<Auth> deletedAuth = authRepository.findById(authId);
        assertFalse(deletedAuth.isPresent(), "Авторизация должна быть удалена");
    }
}

