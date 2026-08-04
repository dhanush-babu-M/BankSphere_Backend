package com.banksphere.modules.payment.repository;

import com.banksphere.modules.payment.entity.BillMerchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillMerchantRepository extends JpaRepository<BillMerchant, UUID> {
    Optional<BillMerchant> findByMerchantCode(String merchantCode);
    List<BillMerchant> findByCategory(String category);
    List<BillMerchant> findByActive(boolean active);
}
