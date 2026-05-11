package az.beenaport.paymentservice.entity;

import az.beenaport.paymentservice.enums.PaymentAttemptStatus;
import az.beenaport.paymentservice.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_attempts")
public class PaymentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentAttemptStatus status = PaymentAttemptStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(name = "transaction_id")
    private String transactionId;          // nullable

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;        // JSON format

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "attempted_at", updatable = false)
    private LocalDateTime attemptedAt;

    @PrePersist
    public void prePersist() {
        attemptedAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

}