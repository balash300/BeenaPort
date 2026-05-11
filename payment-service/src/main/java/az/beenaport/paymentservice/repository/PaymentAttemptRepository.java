package az.beenaport.paymentservice.repository;

import az.beenaport.paymentservice.entity.PaymentAttempt;
import az.beenaport.paymentservice.enums.PaymentAttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {

    List<PaymentAttempt> findByPaymentId(Long paymentId);

    List<PaymentAttempt> findByPaymentIdAndStatus(
            Long paymentId,
            PaymentAttemptStatus status
    );

    int countByPaymentId(Long paymentId);
}