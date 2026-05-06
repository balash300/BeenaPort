package az.beenaport.propertyservice.dto.response;

import az.beenaport.propertyservice.enums.LeaseStatus;
import az.beenaport.propertyservice.enums.PaymentResponsibility;
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
public class LeaseResponse {
    private Long id;
    private Long unitId;
    private Long tenantId;
    private Long ownerId;
    private BigDecimal rentAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaseStatus status;
    private PaymentResponsibility paymentResponsibility;
    private Integer ownerSharePercent;
    private LocalDateTime createdAt;
}