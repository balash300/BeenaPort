package az.beenaport.billingservice.scheduler;

import az.beenaport.billingservice.entity.Invoice;
import az.beenaport.billingservice.enums.InvoiceStatus;
import az.beenaport.billingservice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueScheduler {

    private final InvoiceRepository invoiceRepository;

    // Hər gün saat 01:00-da işləyir
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void markOverdueInvoices() {
        log.info("OverdueScheduler started");
        LocalDate today = LocalDate.now();

        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(today);
        log.info("Found {} overdue invoices", overdueInvoices.size());

        overdueInvoices.forEach(invoice -> {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            log.info("Marking invoice id={} as OVERDUE", invoice.getId());
        });

        invoiceRepository.saveAll(overdueInvoices);
        log.info("OverdueScheduler completed");
    }
}