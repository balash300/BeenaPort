package az.beenaport.paymentservice.dto.request;

import az.beenaport.paymentservice.enums.Currency;
import az.beenaport.paymentservice.enums.PaymentGateway;
import az.beenaport.paymentservice.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    @NotNull(message = "Payment gateway is required")
    private PaymentGateway gateway;

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;

    private String transactionId;
}