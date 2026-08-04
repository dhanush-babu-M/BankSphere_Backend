package com.banksphere.modules.customer.repository;

import com.banksphere.modules.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    Optional<Customer> findByCustomerId(String customerId);
    Page<Customer> findByKycStatus(String kycStatus, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByPanNumber(String panNumber);
}
