package az.beenaport.propertyservice.dto.request;

import az.beenaport.propertyservice.enums.PaymentResponsibility;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LeaseRequest {

    @NotNull(message = "Tenant ID is required")
    private Long tenantId;

    @NotNull(message = "Rent amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rent amount must be greater than 0")
    private BigDecimal rentAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Payment responsibility is required")
    private PaymentResponsibility paymentResponsibility;

    @Min(value = 1, message = "Owner share must be at least 1%")
    @Max(value = 99, message = "Owner share must be at most 99%")
    private Integer ownerSharePercent;
}