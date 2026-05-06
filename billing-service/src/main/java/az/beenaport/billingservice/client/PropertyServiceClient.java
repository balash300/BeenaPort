package az.beenaport.billingservice.client;

import az.beenaport.billingservice.client.response.LeaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "property-service",
        url = "${property.service.url}"
)
public interface PropertyServiceClient {

    @GetMapping("/api/v1/property/buildings/floors/units/leases/{id}")
    LeaseResponse getLeaseById(@PathVariable Long id);
}