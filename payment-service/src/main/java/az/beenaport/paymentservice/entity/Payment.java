package az.beenaport.paymentservice.entity;

import az.beenaport.paymentservice.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"idempotency_key"},
                        name = "uk_payment_idempotency_key"
                )
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "payer_id", nullable = false)
    private Long payerId;

    @Column(name = "original_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalAmount;

    @Column(name = "converted_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal convertedAmount;

    @Column(name = "exchange_rate", precision = 10, scale = 6)
    private BigDecimal exchangeRate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "original_currency", nullable = false)
    private Currency originalCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_currency", nullable = false)
    @Builder.Default
    private Currency settlementCurrency = Currency.AZN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.INITIATED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentGateway gateway = PaymentGateway.MOCK;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;             // nullable — CASH-da olmaya bilər

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "refunded_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "billing_notified", nullable = false)
    @Builder.Default
    private boolean billingNotified = false;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PaymentAttempt> attempts = new ArrayList<>();
}