package az.beenaport.billingservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayInvoiceRequest {

    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    @NotNull(message = "Paid amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Paid amount must be greater than 0")
    private BigDecimal paidAmount;
}
