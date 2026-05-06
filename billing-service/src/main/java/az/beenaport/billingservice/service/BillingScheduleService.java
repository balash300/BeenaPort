package az.beenaport.billingservice.service;

import az.beenaport.billingservice.dto.request.BillingScheduleRequest;
import az.beenaport.billingservice.dto.response.BillingScheduleResponse;

import java.util.List;

public interface BillingScheduleService {
    BillingScheduleResponse create(BillingScheduleRequest request);
    List<BillingScheduleResponse> getAll();
    BillingScheduleResponse getByLeaseId(Long leaseId);
    BillingScheduleResponse deactivate(Long id);
}