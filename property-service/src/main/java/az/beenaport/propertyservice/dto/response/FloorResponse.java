package az.beenaport.propertyservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorResponse {
    private Long id;
    private int floorNumber;
    private Long buildingId;
    private List<UnitResponse> units;
}