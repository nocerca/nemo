package no.cerca.repositories;

import no.cerca.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jadae on 23.03.2025
 */
@Repository
public interface StaffRepository  extends JpaRepository<Staff, Long> {
}
