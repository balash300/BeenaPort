package az.beenaport.propertyservice.service;

import az.beenaport.propertyservice.dto.request.UnitRequest;
import az.beenaport.propertyservice.dto.response.UnitResponse;

import java.util.List;

public interface UnitService {
    UnitResponse create(Long floorId, UnitRequest request);
    List<UnitResponse> getByFloor(Long floorId);
    UnitResponse getById(Long id);
    UnitResponse update(Long id, UnitRequest request);
    void softDelete(Long id);
    void hardDelete(Long id);
}
