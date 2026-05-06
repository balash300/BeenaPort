package az.beenaport.billingservice.service.impl;

import az.beenaport.billingservice.client.PropertyServiceClient;
import az.beenaport.billingservice.client.response.LeaseResponse;
import az.beenaport.billingservice.dto.request.BillingScheduleRequest;
import az.beenaport.billingservice.dto.response.BillingScheduleResponse;
import az.beenaport.billingservice.entity.BillingSchedule;
import az.beenaport.billingservice.exception.AccessDeniedException;
import az.beenaport.billingservice.exception.BusinessException;
import az.beenaport.billingservice.exception.ResourceNotFoundException;
import az.beenaport.billingservice.mapper.BillingScheduleMapper;
import az.beenaport.billingservice.repository.BillingScheduleRepository;
import az.beenaport.billingservice.auth.CurrentUserUtil;
import az.beenaport.billingservice.service.BillingScheduleService;
import az.beenaport.billingservice.service.InvoiceCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingScheduleServiceImpl implements BillingScheduleService {

    private final BillingScheduleRepository scheduleRepository;
    private final PropertyServiceClient propertyServiceClient;
    private final InvoiceCalculatorService calculatorService;
    private final BillingScheduleMapper scheduleMapper;
    private final CurrentUserUtil currentUserUtil;

    @Override
    @Transactional
    public BillingScheduleResponse create(BillingScheduleRequest request) {
        log.info("Creating billing schedule for leaseId={}", request.getLeaseId());

        LeaseResponse lease = propertyServiceClient.getLeaseById(request.getLeaseId());

        if (!"ACTIVE".equals(lease.getStatus())) {
            throw new BusinessException("Lease is not active: " + request.getLeaseId());
        }

        if (scheduleRepository.existsByLeaseIdAndActiveTrue(request.getLeaseId())) {
            throw new BusinessException("Active schedule already exists for lease: "
                    + request.getLeaseId());
        }

        java.time.LocalDate nextBillingDate = request.getStartDate() != null
                ? request.getStartDate()
                : lease.getStartDate();

        BillingSchedule schedule = BillingSchedule.builder()
                .leaseId(request.getLeaseId())
                .billingPeriod(request.getBillingPeriod())
                .nextBillingDate(nextBillingDate)
                .leaseEndDate(lease.getEndDate())
                .active(true)
                .createdBy(currentUserUtil.getCurrentUserId())
                .build();

        return scheduleMapper.toResponse(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingScheduleResponse> getAll() {
        if (!currentUserUtil.hasRole("ADMIN")) {
            throw new AccessDeniedException("Only admin can view all schedules");
        }
        return scheduleRepository.findAll()
                .stream()
                .map(scheduleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BillingScheduleResponse getByLeaseId(Long leaseId) {
        return scheduleMapper.toResponse(
                scheduleRepository.findByLeaseId(leaseId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Schedule not found for lease: " + leaseId))
        );
    }

    @Override
    @Transactional
    public BillingScheduleResponse deactivate(Long id) {
        log.info("Deactivating schedule id={}", id);
        BillingSchedule schedule = findById(id);

        if (!currentUserUtil.hasRole("ADMIN") && !currentUserUtil.hasRole("OWNER")) {
            throw new AccessDeniedException("Only admin or owner can deactivate schedule");
        }

        if (!schedule.isActive()) {
            throw new BusinessException("Schedule is already inactive");
        }

        schedule.setActive(false);
        return scheduleMapper.toResponse(scheduleRepository.save(schedule));
    }


    private BillingSchedule findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + id));
    }
}