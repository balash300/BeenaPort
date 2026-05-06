package az.beenaport.propertyservice.dto.request;

import az.beenaport.propertyservice.enums.UnitType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UnitRequest {

    @NotBlank(message = "Unit number is required")
    private String unitNumber;

    @NotNull(message = "Area is required")
    private Double area;

    @NotNull(message = "Unit type is required")
    private UnitType type;

    @NotNull(message = "Rent amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rent amount must be greater than 0")
    private BigDecimal rentAmount;

    private Long commercialOwnerId;
}