package az.beenaport.paymentservice.scheduler;

import az.beenaport.paymentservice.client.BillingServiceClient;
import az.beenaport.paymentservice.client.dto.PayInvoiceRequest;
import az.beenaport.paymentservice.entity.Payment;
import az.beenaport.paymentservice.enums.PaymentStatus;
import az.beenaport.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryScheduler {

    private final PaymentRepository paymentRepository;
    private final BillingServiceClient billingServiceClient;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void retryBillingNotification() {
        log.info("RetryScheduler started");

        List<Payment> payments = paymentRepository
                .findByStatusAndBillingNotifiedFalse(PaymentStatus.SUCCESS);

        log.info("Found {} payments to retry billing notification", payments.size());

        for (Payment payment : payments) {
            try {
                log.info("Retrying billing notification for paymentId={}", payment.getId());

                billingServiceClient.payInvoice(
                        payment.getInvoiceId(),
                        new PayInvoiceRequest(payment.getId(), payment.getAmount())
                );

                payment.setBillingNotified(true);
                paymentRepository.save(payment);

                log.info("Billing notification successful for paymentId={}", payment.getId());

            } catch (Exception e) {
                log.error("Retry failed for paymentId={}: {}", payment.getId(), e.getMessage());
            }
        }

        log.info("RetryScheduler completed");
    }
}