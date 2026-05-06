package az.beenaport.billingservice.controller;

import az.beenaport.billingservice.dto.request.InvoiceRequest;
import az.beenaport.billingservice.dto.request.PayInvoiceRequest;
import az.beenaport.billingservice.dto.response.InvoiceResponse;
import az.beenaport.billingservice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<List<InvoiceResponse>> create(
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invoiceService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    @GetMapping("/my")
    public ResponseEntity<List<InvoiceResponse>> getMyInvoices() {
        return ResponseEntity.ok(invoiceService.getMyInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/lease/{leaseId}")
    public ResponseEntity<List<InvoiceResponse>> getByLeaseId(
            @PathVariable Long leaseId) {
        return ResponseEntity.ok(invoiceService.getByLeaseId(leaseId));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponse> pay(
            @PathVariable Long id,
            @Valid @RequestBody PayInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.pay(id, request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<InvoiceResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.cancel(id));
    }
}