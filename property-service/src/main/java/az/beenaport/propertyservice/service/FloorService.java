package az.beenaport.propertyservice.service;

import az.beenaport.propertyservice.dto.request.FloorRequest;
import az.beenaport.propertyservice.dto.response.FloorResponse;

import java.util.List;

public interface FloorService {
    FloorResponse create(Long buildingId, FloorRequest request);
    List<FloorResponse> getByBuilding(Long buildingId);
    FloorResponse getById(Long id);
    FloorResponse update(Long id, FloorRequest request);
    void softDelete(Long id);
    void hardDelete(Long id);
}