package az.beenaport.propertyservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FloorRequest {

    @NotNull(message = "Floor number is required")
    @Min(value = 1, message = "Floor number must be at least 1")
    private Integer floorNumber;
}