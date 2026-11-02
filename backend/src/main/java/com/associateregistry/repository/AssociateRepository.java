package com.associateregistry.repository;

import com.associateregistry.entity.Associate;
import com.associateregistry.entity.EmploymentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface AssociateRepository extends JpaRepository<Associate, Long>, JpaSpecificationExecutor<Associate> {

    boolean existsByAssociateIdIgnoreCase(String associateId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByAssociateIdIgnoreCaseAndIdNot(String associateId, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    long countByStatus(EmploymentStatus status);

    @Query("select distinct a.department from Associate a order by a.department")
    List<String> findDistinctDepartments();

    List<Associate> findTop5ByOrderByRegistrationDateDescIdDesc();
}
