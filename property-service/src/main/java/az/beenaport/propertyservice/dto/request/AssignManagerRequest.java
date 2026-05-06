package az.beenaport.propertyservice.property.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignManagerRequest {

    @NotNull(message = "Manager ID is required")
    private Long managerId;
}