package az.beenaport.paymentservice.repository;

import az.beenaport.paymentservice.entity.Payment;
import az.beenaport.paymentservice.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPayerId(Long payerId);

    List<Payment> findByInvoiceId(Long invoiceId);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByStatusAndBillingNotifiedFalse(PaymentStatus status);

    Optional<Payment> findByTransactionId(String transactionId);
}