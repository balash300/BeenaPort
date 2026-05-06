package az.beenaport.propertyservice.service.impl;

import az.beenaport.propertyservice.dto.request.BuildingRequest;
import az.beenaport.propertyservice.dto.response.BuildingResponse;
import az.beenaport.propertyservice.entity.Building;
import az.beenaport.propertyservice.entity.Property;
import az.beenaport.propertyservice.exception.AccessDeniedException;
import az.beenaport.propertyservice.exception.BusinessException;
import az.beenaport.propertyservice.exception.ResourceNotFoundException;
import az.beenaport.propertyservice.mapper.BuildingMapper;
import az.beenaport.propertyservice.repository.BuildingRepository;
import az.beenaport.propertyservice.repository.PropertyManagerRepository;
import az.beenaport.propertyservice.repository.PropertyRepository;
import az.beenaport.propertyservice.auth.CurrentUserUtil;
import az.beenaport.propertyservice.service.BuildingService;
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
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyManagerRepository propertyManagerRepository;
    private final BuildingMapper buildingMapper;
    private final CurrentUserUtil currentUserUtil;

    @Override
    @Transactional
    public BuildingResponse create(Long propertyId, BuildingRequest request) {
        log.info("Creating building for propertyId={}", propertyId);
        Property property = findPropertyById(propertyId);
        validateAccess(property, true);

        if (buildingRepository.existsByPropertyIdAndName(propertyId, request.getName())) {
            throw new BusinessException("Building name already exists: " + request.getName());
        }

        Building building = buildingMapper.toEntity(request);
        building.setProperty(property);

        return saveBuilding(building);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingResponse> getByProperty(Long propertyId) {
        log.info("Fetching buildings for propertyId={}", propertyId);
        findPropertyById(propertyId);

        return buildingRepository.findByPropertyId(propertyId)
                .stream()
                .map(buildingMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingResponse getById(Long id) {
        log.info("Fetching building id={}", id);
        return buildingMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public BuildingResponse update(Long id, BuildingRequest request) {
        log.info("Updating building id={}", id);
        Building building = findById(id);
        validateAccess(building.getProperty(), true);

        if (buildingRepository.existsByPropertyIdAndNameAndIdNot(
                building.getProperty().getId(), request.getName(), id)) {
            throw new BusinessException("Building name already exists: " + request.getName());
        }

        buildingMapper.updateEntity(request, building);
        return saveBuilding(building);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        log.info("Soft deleting building id={}", id);
        Building building = findById(id);
        validateAccess(building.getProperty(), true);
        building.setActive(false);
        buildingRepository.save(building);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        log.info("Hard deleting building id={}", id);
        if (!currentUserUtil.hasRole("ADMIN")) {
            throw new AccessDeniedException("Only admin can hard delete");
        }
        Building building = findById(id);
        buildingRepository.delete(building);
    }

    private Building findById(Long id) {
        return getOrThrow(
                buildingRepository.findById(id),
                "Building not found: " + id
        );
    }

    private Property findPropertyById(Long id) {
        return getOrThrow(
                propertyRepository.findById(id),
                "Property not found: " + id
        );
    }

    private <T> T getOrThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(message));
    }

    /**
     * allowManager = true  → owner + manager + admin
     * allowManager = false → only owner + admin
     */
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

    private BuildingResponse saveBuilding(Building building) {
        try {
            return buildingMapper.toResponse(buildingRepository.save(building));
        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while saving building", ex);
            throw new BusinessException("Building name already exists");
        }
    }
}