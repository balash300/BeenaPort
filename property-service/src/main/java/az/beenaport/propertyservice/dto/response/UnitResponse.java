package az.beenaport.propertyservice.dto.response;

import az.beenaport.propertyservice.enums.UnitStatus;
import az.beenaport.propertyservice.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitResponse {
    private Long id;
    private String unitNumber;
    private Double area;
    private UnitType type;
    private UnitStatus status;
    private BigDecimal rentAmount;
    private Long commercialOwnerId;
    private Long floorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LeaseResponse> leases;
}