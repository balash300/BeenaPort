package az.beenaport.paymentservice.dto.response;

import az.beenaport.paymentservice.enums.PaymentAttemptStatus;
import az.beenaport.paymentservice.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAttemptResponse {
    private Long id;
    private Long paymentId;
    private BigDecimal amount;
    private PaymentAttemptStatus status;
    private PaymentMethod method;
    private String transactionId;
    private String errorMessage;
    private LocalDateTime attemptedAt;
}