package az.beenaport.paymentservice.service;

import az.beenaport.paymentservice.dto.request.PaymentRequest;
import az.beenaport.paymentservice.dto.request.RefundRequest;
import az.beenaport.paymentservice.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse create(PaymentRequest request);
    List<PaymentResponse> getAll();
    List<PaymentResponse> getMyPayments();
    PaymentResponse getById(Long id);
    PaymentResponse refund(Long id, RefundRequest request);
    List<PaymentResponse> getByInvoiceId(Long invoiceId);
}