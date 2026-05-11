package az.beenaport.paymentservice.client;

import az.beenaport.paymentservice.client.dto.InvoiceResponse;
import az.beenaport.paymentservice.client.dto.PayInvoiceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "billing-service",
        url = "${billing.service.url}"
)
public interface BillingServiceClient {

    @GetMapping("/api/v1/billing/invoices/{id}")
    InvoiceResponse getInvoiceById(@PathVariable Long id);

    @PostMapping("/api/v1/billing/invoices/{id}/pay")
    InvoiceResponse payInvoice(@PathVariable Long id,
                               @RequestBody PayInvoiceRequest request);
}
