package az.beenaport.billingservice.client.response;

import az.beenaport.billingservice.enums.PaymentResponsibility;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LeaseResponse {
    private Long id;
    private Long unitId;
    private Long tenantId;
    private Long ownerId;
    private BigDecimal rentAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private PaymentResponsibility paymentResponsibility;
    private Integer ownerSharePercent;
}