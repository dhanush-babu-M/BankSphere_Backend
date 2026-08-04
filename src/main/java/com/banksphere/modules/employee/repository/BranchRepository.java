package com.banksphere.modules.employee.repository;

import com.banksphere.modules.employee.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    Optional<Branch> findByBranchCode(String branchCode);
    List<Branch> findByCity(String city);
    List<Branch> findByActive(boolean active);
}
