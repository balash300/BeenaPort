package az.beenaport.billingservice.dto.response;

import az.beenaport.billingservice.enums.BillingPeriod;
import az.beenaport.billingservice.enums.InvoiceStatus;
import az.beenaport.billingservice.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private Long leaseId;
    private Long tenantId;
    private Long ownerId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private InvoiceStatus status;
    private InvoiceType type;
    private BillingPeriod billingPeriod;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDate dueDate;
    private Long paymentId;
    private LocalDateTime paidAt;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}