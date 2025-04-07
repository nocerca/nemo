package integration.repositories;

import integration.SpringBootApplicationTest;
import jakarta.transaction.Transactional;
import no.cerca.entities.Company;
import no.cerca.repositories.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by jadae on 06.04.2025
 */
@Transactional
public class CompanyRepositoryTest extends SpringBootApplicationTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Поиск компании по companyExternalId")
    void testFindByCompanyExternalId() {
        Optional<Company> company = companyRepository.findByCompanyExternalId(1001L);
        assertTrue(company.isPresent());
        assertEquals(1001L, company.get().getCompanyExternalId());
    }

    @Test
    @DisplayName("Компания не найдена по несуществующему companyExternalId")
    void testFindByCompanyExternalIdNotFound() {
        Optional<Company> company = companyRepository.findByCompanyExternalId(9999L);
        assertFalse(company.isPresent());
    }

    @Test
    @DisplayName("Создание новой компании")
    void testCreateCompany() {
        Company newCompany = new Company();
        newCompany.setTitle("New Company");
        newCompany.setPhone("+1234567890");
        newCompany.setCountry("New Country");
        newCompany.setAllowDeleteRecord(true);
        newCompany.setAllowChangeRecord(true);
        newCompany.setSite("https://newcompany.com");
        newCompany.setCompanyExternalId(3001L);

        Company savedCompany = companyRepository.save(newCompany);

        assertNotNull(savedCompany.getCompanyInnerId(), "ID компании не должен быть null");
        assertEquals("New Company", savedCompany.getTitle(), "Название компании должно совпадать");
    }

    @Test
    @DisplayName("Обновление данных компании")
    void testUpdateCompany() {
        Company existingCompany = companyRepository.findByCompanyExternalId(1001L)
                .orElseThrow(() -> new RuntimeException("Компания не найдена"));

        existingCompany.setTitle("Updated Company");
        Company updatedCompany = companyRepository.save(existingCompany);

        assertEquals("Updated Company", updatedCompany.getTitle(), "Название компании должно быть обновлено");
    }

    @Test
    @DisplayName("Удаление компании")
    void testDeleteCompany() {
        Company companyToDelete = new Company();
        companyToDelete.setTitle("Company to Delete");
        companyToDelete.setPhone("+1987654321");
        companyToDelete.setCountry("Delete Country");
        companyToDelete.setAllowDeleteRecord(true);
        companyToDelete.setAllowChangeRecord(false);
        companyToDelete.setSite("https://deletecompany.com");
        companyToDelete.setCompanyExternalId(4001L);

        Company savedCompany = companyRepository.save(companyToDelete);
        Long companyId = savedCompany.getCompanyExternalId();

        companyRepository.delete(savedCompany);

        Optional<Company> deletedCompany = companyRepository.findById(companyId);
        assertFalse(deletedCompany.isPresent(), "Компания должна быть удалена");
    }
}
