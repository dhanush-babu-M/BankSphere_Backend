package com.banksphere.modules.beneficiary.repository;

import com.banksphere.modules.beneficiary.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID> {
    List<Beneficiary> findByOwnerCustomerId(UUID ownerCustomerId);
    List<Beneficiary> findByOwnerCustomerIdAndActive(UUID ownerCustomerId, boolean active);
    Optional<Beneficiary> findByOwnerCustomerIdAndAccountNumber(UUID ownerCustomerId, String accountNumber);
    boolean existsByOwnerCustomerIdAndAccountNumber(UUID ownerCustomerId, String accountNumber);
}
