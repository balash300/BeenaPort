package az.beenaport.propertyservice.service;

import az.beenaport.propertyservice.dto.request.AssignManagerRequest;
import az.beenaport.propertyservice.dto.request.PropertyRequest;
import az.beenaport.propertyservice.dto.response.PropertyManagerResponse;
import az.beenaport.propertyservice.dto.response.PropertyResponse;

import java.util.List;

public interface PropertyService {

    PropertyResponse create(PropertyRequest request);
    List<PropertyResponse> getAll();
    List<PropertyResponse> getMyProperties();
    PropertyResponse getById(Long id);
    PropertyResponse update(Long id, PropertyRequest request);
    void delete(Long id);
    PropertyManagerResponse assignManager(Long propertyId, AssignManagerRequest request);
    List<PropertyManagerResponse> getManagers(Long propertyId);
    void removeManager(Long propertyId, Long managerId);
}
