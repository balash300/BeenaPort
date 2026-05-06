package az.beenaport.propertyservice.service.impl;

import az.beenaport.propertyservice.dto.request.LeaseRequest;
import az.beenaport.propertyservice.dto.response.LeaseResponse;
import az.beenaport.propertyservice.entity.Lease;
import az.beenaport.propertyservice.entity.Property;
import az.beenaport.propertyservice.entity.Unit;
import az.beenaport.propertyservice.enums.LeaseStatus;
import az.beenaport.propertyservice.enums.PaymentResponsibility;
import az.beenaport.propertyservice.enums.UnitStatus;
import az.beenaport.propertyservice.exception.AccessDeniedException;
import az.beenaport.propertyservice.exception.BusinessException;
import az.beenaport.propertyservice.exception.ResourceNotFoundException;
import az.beenaport.propertyservice.mapper.LeaseMapper;
import az.beenaport.propertyservice.repository.LeaseRepository;
import az.beenaport.propertyservice.repository.PropertyManagerRepository;
import az.beenaport.propertyservice.repository.UnitRepository;
import az.beenaport.propertyservice.auth.CurrentUserUtil;
import az.beenaport.propertyservice.service.LeaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaseServiceImpl implements LeaseService {

    private final LeaseRepository leaseRepository;
    private final UnitRepository unitRepository;
    private final PropertyManagerRepository propertyManagerRepository;
    private final LeaseMapper leaseMapper;
    private final CurrentUserUtil currentUserUtil;

    @Override
    @Transactional
    public LeaseResponse create(Long unitId, LeaseRequest request) {
        log.info("Creating lease for unitId={}", unitId);
        Unit unit = findUnitById(unitId);
        Property property = unit.getFloor().getBuilding().getProperty();
        validateAccess(property, true);

        // Unit boşdurmu yoxla
        if (leaseRepository.existsByUnitIdAndStatus(unitId, LeaseStatus.ACTIVE)) {
            throw new BusinessException("Unit already has an active lease");
        }

        // Tarix yoxlaması
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date cannot be before start date");
        }

        // SPLIT olduqda ownerSharePercent məcburidir
        if (request.getPaymentResponsibility() == PaymentResponsibility.SPLIT
                && request.getOwnerSharePercent() == null) {
            throw new BusinessException("Owner share percent is required for SPLIT payment");
        }

        Lease lease = leaseMapper.toEntity(request);
        lease.setUnit(unit);
        lease.setOwnerId(resolveOwnerId(unit, property));

        // Unit statusunu yenilə
        updateUnitStatus(unit, true);

        return leaseMapper.toResponse(leaseRepository.save(lease));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaseResponse> getByUnit(Long unitId) {
        log.info("Fetching leases for unitId={}", unitId);
        findUnitById(unitId);

        return leaseRepository.findByUnitId(unitId)
                .stream()
                .map(leaseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaseResponse> getMyLeases() {
        Long tenantId = currentUserUtil.getCurrentUserId();
        log.info("Fetching leases for tenantId={}", tenantId);

        return leaseRepository.findByTenantId(tenantId)
                .stream()
                .map(leaseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LeaseResponse getById(Long id) {
        log.info("Fetching lease id={}", id);
        return leaseMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public LeaseResponse update(Long id, LeaseRequest request) {
        log.info("Updating lease id={}", id);
        Lease lease = findById(id);
        Property property = lease.getUnit().getFloor().getBuilding().getProperty();
        validateAccess(property, true);

        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new BusinessException("Only active leases can be updated");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date cannot be before start date");
        }

        if (request.getPaymentResponsibility() == PaymentResponsibility.SPLIT
                && request.getOwnerSharePercent() == null) {
            throw new BusinessException("Owner share percent is required for SPLIT payment");
        }

        leaseMapper.updateEntity(request, lease);
        return leaseMapper.toResponse(leaseRepository.save(lease));
    }

    @Override
    @Transactional
    public void terminate(Long id) {
        log.info("Terminating lease id={}", id);
        Lease lease = findById(id);
        Property property = lease.getUnit().getFloor().getBuilding().getProperty();
        validateAccess(property, true);

        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new BusinessException("Only active leases can be terminated");
        }

        lease.setStatus(LeaseStatus.TERMINATED);
        leaseRepository.save(lease);

        // Unit statusunu yenilə
        updateUnitStatus(lease.getUnit(), false);
    }

    // ── Private helpers ───────────────────────────────────────────

    private Lease findById(Long id) {
        return getOrThrow(leaseRepository.findById(id), "Lease not found: " + id);
    }

    private Unit findUnitById(Long id) {
        return getOrThrow(unitRepository.findById(id), "Unit not found: " + id);
    }

    private <T> T getOrThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(message));
    }

    /**
     * Commercial unit-də tenant commercial owner-ə ödəyir,
     * adi unit-də isə property owner-ə.
     */
    private Long resolveOwnerId(Unit unit, Property property) {
        if (unit.getCommercialOwnerId() != null) {
            return unit.getCommercialOwnerId();
        }
        return property.getOwnerId();
    }

    private void updateUnitStatus(Unit unit, boolean occupied) {
        if (occupied) {
            unit.setStatus(unit.getCommercialOwnerId() != null
                    ? UnitStatus.PARTIALLY_OCCUPIED
                    : UnitStatus.OCCUPIED);
        } else {
            unit.setStatus(unit.getCommercialOwnerId() != null
                    ? UnitStatus.PARTIALLY_OCCUPIED
                    : UnitStatus.VACANT);
        }
        unitRepository.save(unit);
    }

    private void validateAccess(Property property, boolean allowManager) {
        Long userId = currentUserUtil.getCurrentUserId();
        boolean isOwner = property.getOwnerId().equals(userId);
        boolean isAdmin = currentUserUtil.hasRole("ADMIN");

        if (isOwner || isAdmin) return;

        if (allowManager) {
            boolean isManager = propertyManagerRepository
                    .existsByPropertyIdAndManagerIdAndActiveTrue(property.getId(), userId);
            if (isManager) return;
        }

        throw new AccessDeniedException("Access denied");
    }
}
