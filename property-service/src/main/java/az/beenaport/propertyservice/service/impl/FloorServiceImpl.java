package az.beenaport.propertyservice.service.impl;

import az.beenaport.propertyservice.dto.request.FloorRequest;
import az.beenaport.propertyservice.dto.response.FloorResponse;
import az.beenaport.propertyservice.entity.Building;
import az.beenaport.propertyservice.entity.Floor;
import az.beenaport.propertyservice.entity.Property;
import az.beenaport.propertyservice.exception.AccessDeniedException;
import az.beenaport.propertyservice.exception.BusinessException;
import az.beenaport.propertyservice.exception.ResourceNotFoundException;
import az.beenaport.propertyservice.mapper.FloorMapper;
import az.beenaport.propertyservice.repository.BuildingRepository;
import az.beenaport.propertyservice.repository.FloorRepository;
import az.beenaport.propertyservice.repository.PropertyManagerRepository;
import az.beenaport.propertyservice.auth.CurrentUserUtil;
import az.beenaport.propertyservice.service.FloorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;
    private final BuildingRepository buildingRepository;
    private final PropertyManagerRepository propertyManagerRepository;
    private final FloorMapper floorMapper;
    private final CurrentUserUtil currentUserUtil;

    @Override
    @Transactional
    public FloorResponse create(Long buildingId, FloorRequest request) {
        log.info("Creating floor for buildingId={}", buildingId);
        Building building = findBuildingById(buildingId);
        validateAccess(building.getProperty(), true);

        if (floorRepository.existsByBuildingIdAndFloorNumber(buildingId, request.getFloorNumber())) {
            throw new BusinessException("Floor number already exists: " + request.getFloorNumber());
        }

        if (request.getFloorNumber() > building.getTotalFloors()) {
            throw new BusinessException("Floor number cannot exceed total floors: " + building.getTotalFloors());
        }

        Floor floor = floorMapper.toEntity(request);
        floor.setBuilding(building);

        return floorMapper.toResponse(floorRepository.save(floor));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FloorResponse> getByBuilding(Long buildingId) {
        log.info("Fetching floors for buildingId={}", buildingId);
        findBuildingById(buildingId);

        return floorRepository.findByBuildingId(buildingId)
                .stream()
                .map(floorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FloorResponse getById(Long id) {
        log.info("Fetching floor id={}", id);
        return floorMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public FloorResponse update(Long id, FloorRequest request) {
        log.info("Updating floor id={}", id);
        Floor floor = findById(id);
        validateAccess(floor.getBuilding().getProperty(), true);

        if (floorRepository.existsByBuildingIdAndFloorNumberAndIdNot(
                floor.getBuilding().getId(), request.getFloorNumber(), id)) {
            throw new BusinessException("Floor number already exists: " + request.getFloorNumber());
        }

        if (request.getFloorNumber() > floor.getBuilding().getTotalFloors()) {
            throw new BusinessException("Floor number cannot exceed total floors: "
                    + floor.getBuilding().getTotalFloors());
        }

        floor.setFloorNumber(request.getFloorNumber());
        return floorMapper.toResponse(floorRepository.save(floor));
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        log.info("Soft deleting floor id={}", id);
        Floor floor = findById(id);
        validateAccess(floor.getBuilding().getProperty(), true);
        floor.setActive(false);
        floorRepository.save(floor);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        log.info("Hard deleting floor id={}", id);
        if (!currentUserUtil.hasRole("ADMIN")) {
            throw new AccessDeniedException("Only admin can hard delete");
        }
        Floor floor = findById(id);
        floorRepository.delete(floor);
    }

    private Floor findById(Long id) {
        return getOrThrow(floorRepository.findById(id), "Floor not found: " + id);
    }

    private Building findBuildingById(Long id) {
        return getOrThrow(buildingRepository.findById(id), "Building not found: " + id);
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