package az.beenaport.billingservice.repository;

import az.beenaport.billingservice.entity.BillingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillingScheduleRepository extends JpaRepository<BillingSchedule, Long> {

    Optional<BillingSchedule> findByLeaseId(Long leaseId);

    List<BillingSchedule> findByActiveTrue();

    // Bu gün invoice yaranmalı olan schedule-lər
    List<BillingSchedule> findByActiveTrueAndNextBillingDateLessThanEqual(
            LocalDate today
    );

    boolean existsByLeaseIdAndActiveTrue(Long leaseId);
}