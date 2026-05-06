package az.beenaport.billingservice.scheduler;

import az.beenaport.billingservice.client.PropertyServiceClient;
import az.beenaport.billingservice.client.response.LeaseResponse;
import az.beenaport.billingservice.entity.BillingSchedule;
import az.beenaport.billingservice.entity.Invoice;
import az.beenaport.billingservice.enums.InvoiceStatus;
import az.beenaport.billingservice.enums.InvoiceType;
import az.beenaport.billingservice.enums.PaymentResponsibility;
import az.beenaport.billingservice.repository.BillingScheduleRepository;
import az.beenaport.billingservice.repository.InvoiceRepository;
import az.beenaport.billingservice.service.InvoiceCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingScheduler {

    private final BillingScheduleRepository scheduleRepository;
    private final InvoiceRepository invoiceRepository;
    private final PropertyServiceClient propertyServiceClient;
    private final InvoiceCalculatorService calculatorService;

    // Hər ayın 1-i saat 00:00-da işləyir
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void generateInvoices() {
        log.info("BillingScheduler started — generating invoices");
        LocalDate today = LocalDate.now();

        List<BillingSchedule> schedules = scheduleRepository
                .findByActiveTrueAndNextBillingDateLessThanEqual(today);

        log.info("Found {} active schedules to process", schedules.size());

        for (BillingSchedule schedule : schedules) {
            try {
                processSchedule(schedule, today);
            } catch (Exception e) {
                log.error("Failed to process schedule id={}, leaseId={}: {}",
                        schedule.getId(), schedule.getLeaseId(), e.getMessage());
            }
        }

        log.info("BillingScheduler completed");
    }

    private void processSchedule(BillingSchedule schedule, LocalDate today) {
        Long leaseId = schedule.getLeaseId();
        log.info("Processing schedule id={} for leaseId={}", schedule.getId(), leaseId);

        // Lease məlumatlarını al
        LeaseResponse lease = propertyServiceClient.getLeaseById(leaseId);

        // Lease aktiv deyilsə schedule-u dayandır
        if (!"ACTIVE".equals(lease.getStatus())) {
            log.warn("Lease {} is not active, deactivating schedule", leaseId);
            schedule.setActive(false);
            scheduleRepository.save(schedule);
            return;
        }

        LocalDate periodStart = schedule.getNextBillingDate();
        LocalDate periodEnd = calculatorService.calculatePeriodEnd(
                periodStart, schedule.getBillingPeriod());
        LocalDate dueDate = calculatorService.calculateDueDate(periodStart);

        // Idempotency yoxlaması
        if (invoiceRepository.existsByLeaseIdAndPeriodStartAndPeriodEnd(
                leaseId, periodStart, periodEnd)) {
            log.warn("Invoice already exists for leaseId={}, period={}/{}",
                    leaseId, periodStart, periodEnd);
        } else {
            // Invoice-ları yarat
            List<Invoice> invoices = buildInvoices(
                    lease, periodStart, periodEnd, dueDate, schedule);
            invoiceRepository.saveAll(invoices);
            log.info("Created {} invoice(s) for leaseId={}", invoices.size(), leaseId);
        }

        // nextBillingDate yenilə
        LocalDate nextBillingDate = calculatorService.calculateNextBillingDate(
                periodStart, schedule.getBillingPeriod());

        // Lease bitibsə schedule-u dayandır
        if (nextBillingDate.isAfter(schedule.getLeaseEndDate())) {
            log.info("Lease {} ended, deactivating schedule", leaseId);
            schedule.setActive(false);
        } else {
            schedule.setNextBillingDate(nextBillingDate);
        }

        scheduleRepository.save(schedule);
    }

    private List<Invoice> buildInvoices(LeaseResponse lease,
                                        LocalDate periodStart,
                                        LocalDate periodEnd,
                                        LocalDate dueDate,
                                        BillingSchedule schedule) {
        List<Invoice> invoices = new ArrayList<>();

        if (lease.getPaymentResponsibility() == PaymentResponsibility.SPLIT) {
            // Tenant invoice-u
            BigDecimal tenantAmount = calculatorService.calculateTenantAmount(lease);
            invoices.add(buildInvoice(lease, tenantAmount, lease.getTenantId(),
                    periodStart, periodEnd, dueDate, schedule));

            // Owner invoice-u
            BigDecimal ownerAmount = calculatorService.calculateOwnerAmount(lease);
            invoices.add(buildInvoice(lease, ownerAmount, lease.getOwnerId(),
                    periodStart, periodEnd, dueDate, schedule));

        } else if (lease.getPaymentResponsibility() == PaymentResponsibility.TENANT) {
            BigDecimal amount = calculatorService.calculateTenantAmount(lease);
            invoices.add(buildInvoice(lease, amount, lease.getTenantId(),
                    periodStart, periodEnd, dueDate, schedule));

        } else {
            // OWNER
            BigDecimal amount = calculatorService.calculateOwnerAmount(lease);
            invoices.add(buildInvoice(lease, amount, lease.getOwnerId(),
                    periodStart, periodEnd, dueDate, schedule));
        }

        return invoices;
    }

    private Invoice buildInvoice(LeaseResponse lease,
                                 BigDecimal amount,
                                 Long payerId,
                                 LocalDate periodStart,
                                 LocalDate periodEnd,
                                 LocalDate dueDate,
                                 BillingSchedule schedule) {
        return Invoice.builder()
                .leaseId(lease.getId())
                .tenantId(lease.getTenantId())
                .ownerId(payerId)
                .amount(amount)
                .billingPeriod(schedule.getBillingPeriod())
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .dueDate(dueDate)
                .status(InvoiceStatus.PENDING)
                .type(InvoiceType.AUTOMATIC)
                .createdBy(0L)   // system
                .build();
    }
}