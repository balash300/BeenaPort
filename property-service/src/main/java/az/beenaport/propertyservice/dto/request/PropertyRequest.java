package az.beenaport.propertyservice.dto.request;

import az.beenaport.propertyservice.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PropertyRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Type is required")
    private PropertyType type;

    @NotBlank(message = "Address is required")
    private String address;
}