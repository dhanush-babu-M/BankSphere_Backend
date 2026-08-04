package com.banksphere.modules.admin.repository;

import com.banksphere.modules.admin.entity.PendingOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PendingOperationRepository extends JpaRepository<PendingOperation, UUID> {
    Page<PendingOperation> findByStatus(String status, Pageable pageable);
    List<PendingOperation> findByRequestedByAndStatus(String requestedBy, String status);
    long countByStatus(String status);
}
