package az.beenaport.paymentservice.service.impl;

import az.beenaport.paymentservice.client.BillingServiceClient;
import az.beenaport.paymentservice.client.dto.InvoiceResponse;
import az.beenaport.paymentservice.client.dto.PayInvoiceRequest;
import az.beenaport.paymentservice.dto.request.PaymentRequest;
import az.beenaport.paymentservice.dto.request.RefundRequest;
import az.beenaport.paymentservice.dto.response.PaymentResponse;
import az.beenaport.paymentservice.entity.Payment;
import az.beenaport.paymentservice.entity.PaymentAttempt;
import az.beenaport.paymentservice.enums.*;
import az.beenaport.paymentservice.exception.AccessDeniedException;
import az.beenaport.paymentservice.exception.BusinessException;
import az.beenaport.paymentservice.exception.ResourceNotFoundException;
import az.beenaport.paymentservice.mapper.PaymentMapper;
import az.beenaport.paymentservice.repository.PaymentAttemptRepository;
import az.beenaport.paymentservice.repository.PaymentRepository;
import az.beenaport.paymentservice.auth.CurrentUserUtil;
import az.beenaport.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository attemptRepository;
    private final BillingServiceClient billingServiceClient;
    private final PaymentMapper paymentMapper;
    private final CurrentUserUtil currentUserUtil;

    @Override
    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        log.info("Creating payment for invoiceId={}", request.getInvoiceId());

        Optional<Payment> existing = paymentRepository
                .findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            log.info("Returning existing payment for idempotencyKey={}",
                    request.getIdempotencyKey());
            return paymentMapper.toResponse(existing.get());
        }

        InvoiceResponse invoice = billingServiceClient
                .getInvoiceById(request.getInvoiceId());

        if (!"PENDING".equals(invoice.getStatus()) &&
                !"PARTIALLY_PAID".equals(invoice.getStatus())) {
            throw new BusinessException("Invoice is not payable: " + invoice.getStatus());
        }

        if (request.getAmount().compareTo(invoice.getRemainingAmount()) > 0) {
            throw new BusinessException("Payment amount exceeds remaining invoice amount");
        }

        Long currentUserId = currentUserUtil.getCurrentUserId();

        BigDecimal exchangeRate = getExchangeRate(request.getCurrency());
        BigDecimal convertedAmount = request.getAmount()
                .multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);

        Payment payment = Payment.builder()
                .invoiceId(request.getInvoiceId())
                .payerId(currentUserId)
                .originalAmount(request.getAmount())
                .convertedAmount(convertedAmount)
                .exchangeRate(exchangeRate)
                .amount(convertedAmount)
                .originalCurrency(request.getCurrency())
                .settlementCurrency(Currency.AZN)
                .status(PaymentStatus.INITIATED)
                .method(request.getMethod())
                .gateway(request.getGateway())
                .transactionId(request.getTransactionId())
                .idempotencyKey(request.getIdempotencyKey())
                .createdBy(currentUserId)
                .build();

        Payment saved = paymentRepository.save(payment);

        PaymentAttempt attempt = PaymentAttempt.builder()
                .payment(saved)
                .amount(convertedAmount)
                .status(PaymentAttemptStatus.PENDING)
                .method(request.getMethod())
                .transactionId(request.getTransactionId())
                .build();

        attemptRepository.save(attempt);

        saved.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(saved);

        try {
            processGateway(saved, attempt, request);

            notifyBilling(saved);

        } catch (Exception e) {
            log.error("Payment failed for invoiceId={}: {}", request.getInvoiceId(), e.getMessage());
            saved.setStatus(PaymentStatus.FAILED);
            saved.setErrorMessage(e.getMessage());
            saved.setUpdatedBy(currentUserId);
            attempt.setStatus(PaymentAttemptStatus.FAILED);
            attempt.setErrorMessage(e.getMessage());
            attemptRepository.save(attempt);
            paymentRepository.save(saved);
        }

        return paymentMapper.toResponse(paymentRepository.save(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAll() {
        if (!currentUserUtil.hasRole("ADMIN")) {
            throw new AccessDeniedException("Only admin can view all payments");
        }
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments() {
        Long userId = currentUserUtil.getCurrentUserId();
        return paymentRepository.findByPayerId(userId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        return paymentMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse refund(Long id, RefundRequest request) {
        log.info("Processing refund for paymentId={}", id);
        Payment payment = findById(id);
        Long currentUserId = currentUserUtil.getCurrentUserId();

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("Only successful payments can be refunded");
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessException("Payment already fully refunded");
        }

        BigDecimal alreadyRefunded = payment.getRefundedAmount();
        BigDecimal maxRefundable = payment.getAmount().subtract(alreadyRefunded);

        if (request.getRefundAmount().compareTo(maxRefundable) > 0) {
            throw new BusinessException("Refund amount exceeds refundable amount: " + maxRefundable);
        }

        BigDecimal newRefundedAmount = alreadyRefunded.add(request.getRefundAmount());
        payment.setRefundedAmount(newRefundedAmount);
        payment.setRefundReason(request.getRefundReason());
        payment.setRefundedAt(LocalDateTime.now());
        payment.setUpdatedBy(currentUserId);

        if (newRefundedAmount.compareTo(payment.getAmount()) == 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    private Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
    }

    private void processGateway(Payment payment, PaymentAttempt attempt, PaymentRequest request) {
        // MOCK gateway — həmişə SUCCESS
        log.info("Processing gateway={} for paymentId={}", request.getGateway(), payment.getId());

        String transactionId = request.getTransactionId() != null
                ? request.getTransactionId()
                : "TXN-" + System.currentTimeMillis();

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(transactionId);
        payment.setUpdatedBy(payment.getPayerId());

        attempt.setStatus(PaymentAttemptStatus.SUCCESS);
        attempt.setTransactionId(transactionId);
        attempt.setResponsePayload("{\"status\": \"SUCCESS\", \"transactionId\": \"" + transactionId + "\"}");

        attemptRepository.save(attempt);
    }

    private void notifyBilling(Payment payment) {
        try {
            log.info("Notifying billing service for paymentId={}", payment.getId());

            billingServiceClient.payInvoice(
                    payment.getInvoiceId(),
                    new PayInvoiceRequest(payment.getId(), payment.getAmount())
            );

            payment.setBillingNotified(true);
            log.info("Billing service notified successfully for paymentId={}", payment.getId());

        } catch (Exception e) {
            log.error("Failed to notify billing service for paymentId={}: {}",
                    payment.getId(), e.getMessage());
            payment.setBillingNotified(false);
        }
    }

    private BigDecimal getExchangeRate(Currency currency) {
        return switch (currency) {
            case AZN -> BigDecimal.ONE;
            case USD -> new BigDecimal("1.70");
            case EUR -> new BigDecimal("1.85");
        };
    }
}