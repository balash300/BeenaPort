package az.beenaport.paymentservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayInvoiceRequest {
    private Long paymentId;
    private BigDecimal paidAmount;
}
