package az.beenaport.billingservice.repository;

import az.beenaport.billingservice.entity.Invoice;
import az.beenaport.billingservice.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByTenantId(Long tenantId);

    List<Invoice> findByOwnerId(Long ownerId);

    List<Invoice> findByLeaseId(Long leaseId);

    List<Invoice> findByStatus(InvoiceStatus status);

    // Idempotency check
    boolean existsByLeaseIdAndPeriodStartAndPeriodEnd(
            Long leaseId,
            LocalDate periodStart,
            LocalDate periodEnd
    );

    // OVERDUE check — dueDate keçib, hələ PENDING olan-lar
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :today AND i.status = 'PENDING'")
    List<Invoice> findOverdueInvoices(LocalDate today);

    // Tenant və ya owner-a aid invoice-lər
    @Query("SELECT i FROM Invoice i WHERE i.tenantId = :userId OR i.ownerId = :userId")
    List<Invoice> findByUserId(Long userId);
}