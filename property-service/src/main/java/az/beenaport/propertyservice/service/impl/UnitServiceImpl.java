package az.beenaport.propertyservice.service.impl;

import az.beenaport.propertyservice.dto.request.UnitRequest;
import az.beenaport.propertyservice.dto.response.UnitResponse;
import az.beenaport.propertyservice.entity.Floor;
import az.beenaport.propertyservice.entity.Property;
import az.beenaport.propertyservice.entity.Unit;
import az.beenaport.propertyservice.exception.AccessDeniedException;
import az.beenaport.propertyservice.exception.BusinessException;
import az.beenaport.propertyservice.exception.ResourceNotFoundException;
import az.beenaport.propertyservice.mapper.UnitMapper;
import az.beenaport.propertyservice.repository.FloorRepository;
import az.beenaport.propertyservice.repository.PropertyManagerRepository;
import az.beenaport.propertyservice.repository.UnitRepository;
import az.beenaport.propertyservice.auth.CurrentUserUtil;
import az.beenaport.propertyservice.service.UnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final FloorRepository floorRepository;
    private final PropertyManagerRepository propertyManagerRepository;
    private final UnitMapper unitMapper;
    private final CurrentUserUtil currentUserUtil;

    @Override
    @Transactional
    public UnitResponse create(Long floorId, UnitRequest request) {
        log.info("Creating unit for floorId={}", floorId);
        Floor floor = findFloorById(floorId);
        validateAccess(floor.getBuilding().getProperty(), true);

        if (unitRepository.existsByFloorIdAndUnitNumber(floorId, request.getUnitNumber())) {
            throw new BusinessException("Unit number already exists: " + request.getUnitNumber());
        }

        Unit unit = unitMapper.toEntity(request);
        unit.setFloor(floor);

        try {
            return unitMapper.toResponse(unitRepository.save(unit));
        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while saving unit", ex);
            throw new BusinessException("Unit number already exists");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitResponse> getByFloor(Long floorId) {
        log.info("Fetching units for floorId={}", floorId);
        findFloorById(floorId);

        return unitRepository.findByFloorId(floorId)
                .stream()
                .map(unitMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnitResponse getById(Long id) {
        log.info("Fetching unit id={}", id);
        return unitMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public UnitResponse update(Long id, UnitRequest request) {
        log.info("Updating unit id={}", id);
        Unit unit = findById(id);
        validateAccess(unit.getFloor().getBuilding().getProperty(), true);

        if (unitRepository.existsByFloorIdAndUnitNumberAndIdNot(
                unit.getFloor().getId(), request.getUnitNumber(), id)) {
            throw new BusinessException("Unit number already exists: " + request.getUnitNumber());
        }

        unitMapper.updateEntity(request, unit);
        return unitMapper.toResponse(unitRepository.save(unit));
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        log.info("Soft deleting unit id={}", id);
        Unit unit = findById(id);
        validateAccess(unit.getFloor().getBuilding().getProperty(), true);
        unit.setActive(false);
        unitRepository.save(unit);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        log.info("Hard deleting unit id={}", id);
        if (!currentUserUtil.hasRole("ADMIN")) {
            throw new AccessDeniedException("Only admin can hard delete");
        }
        Unit unit = findById(id);
        unitRepository.delete(unit);
    }

    private Unit findById(Long id) {
        return getOrThrow(unitRepository.findById(id), "Unit not found: " + id);
    }

    private Floor findFloorById(Long id) {
        return getOrThrow(floorRepository.findById(id), "Floor not found: " + id);
    }

    private <T> T getOrThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(message));
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