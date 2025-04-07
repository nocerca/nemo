package no.cerca.repositories;

import no.cerca.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>  {
    Optional<Company> findByCompanyExternalId(Long companyExternalId);
}
