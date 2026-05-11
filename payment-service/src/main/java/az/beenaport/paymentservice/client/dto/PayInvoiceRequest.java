package az.beenaport.paymentservice.client.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayInvoiceRequest {
    private Long paymentId;
    private BigDecimal paidAmount;
}
