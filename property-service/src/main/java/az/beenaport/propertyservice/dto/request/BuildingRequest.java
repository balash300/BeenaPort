package az.beenaport.propertyservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuildingRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Total floors is required")
    @Min(value = 1, message = "Total floors must be at least 1")
    private Integer totalFloors;
}