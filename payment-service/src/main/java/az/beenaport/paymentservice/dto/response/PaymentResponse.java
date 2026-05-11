package az.beenaport.paymentservice.dto.response;

import az.beenaport.paymentservice.enums.Currency;
import az.beenaport.paymentservice.enums.PaymentGateway;
import az.beenaport.paymentservice.enums.PaymentMethod;
import az.beenaport.paymentservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long invoiceId;
    private Long payerId;
    private BigDecimal originalAmount;
    private BigDecimal convertedAmount;
    private BigDecimal exchangeRate;
    private BigDecimal amount;
    private Currency originalCurrency;
    private Currency settlementCurrency;
    private PaymentStatus status;
    private PaymentMethod method;
    private PaymentGateway gateway;
    private String transactionId;
    private String idempotencyKey;
    private String errorMessage;
    private BigDecimal refundedAmount;
    private String refundReason;
    private LocalDateTime refundedAt;
    private boolean billingNotified;
    private Long createdBy;
    private Long updatedBy;
    private List<PaymentAttemptResponse> attempts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}