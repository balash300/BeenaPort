package az.beenaport.propertyservice.property.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyManagerResponse {
    private Long id;
    private Long managerId;
    private Long propertyId;
    private Long assignedBy;
    private LocalDateTime assignedAt;
    private boolean active;
}