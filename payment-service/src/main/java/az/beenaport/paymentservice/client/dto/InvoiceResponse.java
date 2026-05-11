package az.beenaport.paymentservice.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceResponse {
    private Long id;
    private Long leaseId;
    private Long tenantId;
    private Long ownerId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private String status;
    private LocalDateTime createdAt;
}
