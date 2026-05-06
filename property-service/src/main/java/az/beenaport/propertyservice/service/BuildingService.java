package az.beenaport.propertyservice.service;

import az.beenaport.propertyservice.dto.request.BuildingRequest;
import az.beenaport.propertyservice.dto.response.BuildingResponse;

import java.util.List;

public interface BuildingService {

    BuildingResponse create(Long propertyId, BuildingRequest request);
    List<BuildingResponse> getByProperty(Long propertyId);
    BuildingResponse getById(Long id);
    BuildingResponse update(Long id, BuildingRequest request);
    void softDelete(Long id);
    void hardDelete(Long id);
}
