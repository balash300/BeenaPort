package az.beenaport.billingservice.dto.request;

import az.beenaport.billingservice.enums.BillingPeriod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BillingScheduleRequest {

    @NotNull(message = "Lease ID is required")
    private Long leaseId;

    @NotNull(message = "Billing period is required")
    private BillingPeriod billingPeriod;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;
}