package az.beenaport.billingservice.service.impl;

import az.beenaport.billingservice.client.response.LeaseResponse;
import az.beenaport.billingservice.enums.BillingPeriod;
import az.beenaport.billingservice.enums.PaymentResponsibility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Slf4j
@Service
public class InvoiceCalculatorService {

    /**
     * Tenant ödəməli olan məbləği hesabla
     */
    public BigDecimal calculateTenantAmount(LeaseResponse lease) {
        if (lease.getPaymentResponsibility() == PaymentResponsibility.OWNER) {
            return BigDecimal.ZERO;
        }

        if (lease.getPaymentResponsibility() == PaymentResponsibility.SPLIT) {
            return calculateTenantShare(lease);
        }

        return lease.getRentAmount();
    }

    /**
     * Owner ödəməli olan məbləği hesabla
     */
    public BigDecimal calculateOwnerAmount(LeaseResponse lease) {
        if (lease.getPaymentResponsibility() == PaymentResponsibility.TENANT) {
            return BigDecimal.ZERO;
        }

        if (lease.getPaymentResponsibility() == PaymentResponsibility.SPLIT) {
            return calculateOwnerShare(lease);
        }

        return lease.getRentAmount();
    }

    /**
     * Period start və end tarixlərini hesabla
     */
    public LocalDate calculatePeriodEnd(LocalDate periodStart, BillingPeriod billingPeriod) {
        return switch (billingPeriod) {
            case MONTHLY -> periodStart.plusMonths(1).minusDays(1);
            case QUARTERLY -> periodStart.plusMonths(3).minusDays(1);
        };
    }

    /**
     * Növbəti billing tarixini hesabla
     */
    public LocalDate calculateNextBillingDate(LocalDate current, BillingPeriod billingPeriod) {
        return switch (billingPeriod) {
            case MONTHLY -> current.plusMonths(1);
            case QUARTERLY -> current.plusMonths(3);
        };
    }

    /**
     * Due date hesabla — period start-dan 5 gün sonra
     */
    public LocalDate calculateDueDate(LocalDate periodStart) {
        return periodStart.plusDays(5);
    }
    
    private BigDecimal calculateTenantShare(LeaseResponse lease) {
        int ownerPercent = lease.getOwnerSharePercent();
        int tenantPercent = 100 - ownerPercent;

        return lease.getRentAmount()
                .multiply(BigDecimal.valueOf(tenantPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateOwnerShare(LeaseResponse lease) {
        return lease.getRentAmount()
                .multiply(BigDecimal.valueOf(lease.getOwnerSharePercent()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}