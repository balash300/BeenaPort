package az.beenaport.billingservice.dto.response;

import az.beenaport.billingservice.enums.BillingPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingScheduleResponse {
    private Long id;
    private Long leaseId;
    private BillingPeriod billingPeriod;
    private LocalDate nextBillingDate;
    private LocalDate leaseEndDate;
    private boolean active;
    private Long createdBy;
    private LocalDateTime createdAt;
}