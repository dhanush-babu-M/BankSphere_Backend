package com.banksphere.modules.customer.repository;

import com.banksphere.modules.customer.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, UUID> {
    List<KycDocument> findByCustomerId(UUID customerId);
    Optional<KycDocument> findByCustomerIdAndDocumentType(UUID customerId, String documentType);
}
