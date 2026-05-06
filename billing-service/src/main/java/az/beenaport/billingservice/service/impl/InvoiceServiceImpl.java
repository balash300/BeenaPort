package az.beenaport.billingservice.service.impl;

import az.beenaport.billingservice.client.PropertyServiceClient;
import az.beenaport.billingservice.client.response.LeaseResponse;
import az.beenaport.billingservice.dto.request.InvoiceRequest;
import az.beenaport.billingservice.dto.request.PayInvoiceRequest;
import az.beenaport.billingservice.dto.response.InvoiceResponse;
import az.beenaport.billingservice.entity.Invoice;
import az.beenaport.billingservice.enums.InvoiceStatus;
import az.beenaport.billingservice.enums.InvoiceType;
import az.beenaport.billingservice.enums.PaymentResponsibility;
import az.beenaport.billingservice.exception.AccessDeniedException;
import az.beenaport.billingservice.exception.BusinessException;
import az.beenaport.billingservice.exception.ResourceNotFoundException;
import az.beenaport.billingservice.mapper.InvoiceMapper;
import az.beenaport.billingservice.repository.InvoiceRepository;
import az.beenaport.billingservice.auth.CurrentUserUtil;
import az.beenaport.billingservice.service.InvoiceCalculatorService;
import az.beenaport.billingservice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PropertyServiceClient propertyServiceClient;
    private final InvoiceCalculatorService calculatorService;
    private final InvoiceMapper invoiceMapper;
    private final CurrentUserUtil currentUserUtil;

    @Override
    @Transactional
    public List<InvoiceResponse> create(InvoiceRequest request) {
        log.info("Creating manual invoice for leaseId={}", request.getLeaseId());

        LeaseResponse lease = propertyServiceClient.getLeaseById(request.getLeaseId());

        if (!"ACTIVE".equals(lease.getStatus())) {
            throw new BusinessException("Lease is not active: " + request.getLeaseId());
        }

        if (request.getPeriodEnd().isBefore(request.getPeriodStart())) {
            throw new BusinessException("Period end cannot be before period start");
        }

        if (invoiceRepository.existsByLeaseIdAndPeriodStartAndPeriodEnd(
                request.getLeaseId(),
                request.getPeriodStart(),
                request.getPeriodEnd())) {
            throw new BusinessException("Invoice already exists for this period");
        }

        Long currentUserId = currentUserUtil.getCurrentUserId();
        List<Invoice> invoices = new ArrayList<>();

        if (lease.getPaymentResponsibility() == PaymentResponsibility.SPLIT) {
            BigDecimal tenantAmount = calculatorService.calculateTenantAmount(lease);
            invoices.add(buildInvoice(request, lease, tenantAmount,
                    lease.getOwnerId(),
                    InvoiceType.MANUAL, currentUserId));

            BigDecimal ownerAmount = calculatorService.calculateOwnerAmount(lease);
            invoices.add(buildInvoice(request, lease, ownerAmount,
                    lease.getOwnerId(),
                    InvoiceType.MANUAL, currentUserId));

        } else if (lease.getPaymentResponsibility() == PaymentResponsibility.TENANT) {
            BigDecimal amount = calculatorService.calculateTenantAmount(lease);
            invoices.add(buildInvoice(request, lease, amount,
                    lease.getOwnerId(),
                    InvoiceType.MANUAL, currentUserId));

        } else {
            BigDecimal amount = calculatorService.calculateOwnerAmount(lease);
            invoices.add(buildInvoice(request, lease, amount,
                    lease.getOwnerId(),
                    InvoiceType.MANUAL, currentUserId));
        }

        return invoiceRepository.saveAll(invoices)
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAll() {
        if (!currentUserUtil.hasRole("ADMIN")) {
            throw new AccessDeniedException("Only admin can view all invoices");
        }
        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getMyInvoices() {
        Long userId = currentUserUtil.getCurrentUserId();
        return invoiceRepository.findByUserId(userId)
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getByLeaseId(Long leaseId) {
        return invoiceRepository.findByLeaseId(leaseId)
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        return invoiceMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public InvoiceResponse pay(Long id, PayInvoiceRequest request) {
        log.info("Processing payment for invoiceId={}", id);
        Invoice invoice = findById(id);

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessException("Cannot pay cancelled invoice");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Invoice already paid");
        }

        if (request.getPaidAmount().compareTo(invoice.getRemainingAmount()) > 0) {
            throw new BusinessException("Paid amount exceeds remaining amount");
        }

        BigDecimal newPaidAmount = invoice.getPaidAmount().add(request.getPaidAmount());
        BigDecimal newRemainingAmount = invoice.getAmount().subtract(newPaidAmount);

        invoice.setPaidAmount(newPaidAmount);
        invoice.setRemainingAmount(newRemainingAmount);
        invoice.setPaymentId(request.getPaymentId());
        invoice.setUpdatedBy(currentUserUtil.getCurrentUserId());

        if (newRemainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(LocalDateTime.now());
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public InvoiceResponse cancel(Long id) {
        log.info("Cancelling invoiceId={}", id);
        Invoice invoice = findById(id);

        if (invoice.getStatus() != InvoiceStatus.PENDING) {
            throw new BusinessException("Only PENDING invoices can be cancelled");
        }

        Long currentUserId = currentUserUtil.getCurrentUserId();
        boolean isAdmin = currentUserUtil.hasRole("ADMIN");
        boolean isOwner = invoice.getOwnerId().equals(currentUserId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Only owner or admin can cancel invoice");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setUpdatedBy(currentUserId);

        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    private Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
    }

    private Invoice buildInvoice(InvoiceRequest request,
                                 LeaseResponse lease,
                                 BigDecimal amount,
                                 Long ownerId,
                                 InvoiceType type,
                                 Long createdBy) {
        return Invoice.builder()
                .leaseId(request.getLeaseId())
                .tenantId(lease.getTenantId())
                .ownerId(ownerId)
                .amount(amount)
                .billingPeriod(request.getBillingPeriod())
                .periodStart(request.getPeriodStart())
                .periodEnd(request.getPeriodEnd())
                .dueDate(request.getDueDate())
                .status(InvoiceStatus.PENDING)
                .type(type)
                .createdBy(createdBy)
                .build();
    }
}