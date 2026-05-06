package az.beenaport.billingservice.service;

import az.beenaport.billingservice.dto.request.InvoiceRequest;
import az.beenaport.billingservice.dto.request.PayInvoiceRequest;
import az.beenaport.billingservice.dto.response.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    List<InvoiceResponse> create(InvoiceRequest request);
    List<InvoiceResponse> getAll();
    List<InvoiceResponse> getMyInvoices();
    List<InvoiceResponse> getByLeaseId(Long leaseId);
    InvoiceResponse getById(Long id);
    InvoiceResponse pay(Long id, PayInvoiceRequest request);
    InvoiceResponse cancel(Long id);
}