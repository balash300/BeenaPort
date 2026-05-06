package az.beenaport.propertyservice.service;

import az.beenaport.propertyservice.dto.request.LeaseRequest;
import az.beenaport.propertyservice.dto.response.LeaseResponse;

import java.util.List;

public interface LeaseService {
    LeaseResponse create(Long unitId, LeaseRequest request);
    List<LeaseResponse> getByUnit(Long unitId);
    List<LeaseResponse> getMyLeases();
    LeaseResponse getById(Long id);
    LeaseResponse update(Long id, LeaseRequest request);
    void terminate(Long id);
}