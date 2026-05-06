package az.beenaport.propertyservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingResponse {
    private Long id;
    private String name;
    private int totalFloors;
    private Long propertyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FloorResponse> floors;
}