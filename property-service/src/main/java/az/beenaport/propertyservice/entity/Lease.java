package az.beenaport.propertyservice.entity;

import az.beenaport.propertyservice.enums.LeaseStatus;
import az.beenaport.propertyservice.enums.PaymentResponsibility;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leases")
public class Lease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "rent_amount", nullable = false)
    private BigDecimal rentAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeaseStatus status = LeaseStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_responsibility", nullable = false)
    @Builder.Default
    private PaymentResponsibility paymentResponsibility = PaymentResponsibility.TENANT;

    @Column(name = "owner_share_percent")
    private Integer ownerSharePercent;   // yalnız SPLIT olduqda dolu olur

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;
}