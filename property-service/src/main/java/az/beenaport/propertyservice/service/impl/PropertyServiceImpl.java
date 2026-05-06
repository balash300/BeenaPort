package az.beenaport.propertyservice.service.impl;

import az.beenaport.propertyservice.dto.request.AssignManagerRequest;
import az.beenaport.propertyservice.dto.request.PropertyRequest;
import az.beenaport.propertyservice.dto.response.PropertyManagerResponse;
import az.beenaport.propertyservice.dto.response.PropertyResponse;
import az.beenaport.propertyservice.entity.Property;
import az.beenaport.propertyservice.entity.PropertyManager;
import az.beenaport.propertyservice.exception.AccessDeniedException;
import az.beenaport.propertyservice.exception.BusinessException;
import az.beenaport.propertyservice.exception.ResourceNotFoundException;
import az.beenaport.propertyservice.mapper.PropertyMapper;
import az.beenaport.propertyservice.repository.PropertyManagerRepository;
import az.beenaport.propertyservice.repository.PropertyRepository;
import az.beenaport.propertyservice.auth.CurrentUserUtil;
import az.beenaport.propertyservice.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyManagerRepository propertyManagerRepository;
    private final PropertyMapper propertyMapper;
    private final CurrentUserUtil currentUserUtil;

    @Override
    @Transactional
    public PropertyResponse create(PropertyRequest request) {
        Long ownerId = currentUserUtil.getCurrentUserId();

        if (!currentUserUtil.hasRole("OWNER")) {
            throw new AccessDeniedException("Only owners can create properties");
        }

        if (propertyRepository.existsByName(request.getName())) {
            throw new BusinessException("Property already exists: " + request.getName());
        }

        Property property = propertyMapper.toEntity(request);
        property.setOwnerId(ownerId);

        return propertyMapper.toResponse(propertyRepository.save(property));
    }

    @Override
    public List<PropertyResponse> getAll() {
        if (!currentUserUtil.hasRole("ADMIN")) {
            throw new AccessDeniedException("Only admins can view all properties");
        }

        return propertyRepository.findAll()
                .stream()
                .map(propertyMapper::toResponse)
                .toList();
    }

    @Override
    public List<PropertyResponse> getMyProperties() {
        if (!currentUserUtil.hasRole("OWNER")) {
            throw new AccessDeniedException("Only owners can view their properties");
        }

        Long ownerId = currentUserUtil.getCurrentUserId();
        return propertyRepository.findByOwnerId(ownerId)
                .stream()
                .map(propertyMapper::toResponse)
                .toList();
    }

    @Override
    public PropertyResponse getById(Long id) {
        return propertyMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public PropertyResponse update(Long id, PropertyRequest request) {
        Property property = findById(id);
        checkOwnership(property);

        if (propertyRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException("Property name already in use: " + request.getName());
        }

        propertyMapper.updateEntity(request, property);
        return propertyMapper.toResponse(propertyRepository.save(property));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Property property = findById(id);
        checkOwnershipOrAdmin(property);
        property.setActive(false);
        propertyRepository.save(property);
    }

    @Override
    @Transactional
    public PropertyManagerResponse assignManager(Long propertyId, AssignManagerRequest request) {
        Property property = findById(propertyId);
        checkOwnership(property);

        if (propertyManagerRepository.existsByPropertyIdAndManagerIdAndActiveTrue(
                propertyId, request.getManagerId())) {
            throw new BusinessException("Manager already assigned to this property");
        }

        PropertyManager manager = PropertyManager.builder()
                .property(property)
                .managerId(request.getManagerId())
                .assignedBy(currentUserUtil.getCurrentUserId())
                .active(true)
                .build();

        return propertyMapper.toManagerResponse(propertyManagerRepository.save(manager));
    }

    @Override
    public List<PropertyManagerResponse> getManagers(Long propertyId) {
        findById(propertyId);
        checkOwnershipOrAdmin(findById(propertyId));

        return propertyManagerRepository.findByPropertyIdAndActiveTrue(propertyId)
                .stream()
                .map(propertyMapper::toManagerResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeManager(Long propertyId, Long managerId) {
        Property property = findById(propertyId);
        checkOwnership(property);

        PropertyManager manager = propertyManagerRepository
                .findByPropertyIdAndManagerId(propertyId, managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        manager.setActive(false);
        propertyManagerRepository.save(manager);
    }

    private Property findById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + id));
    }

    private void checkOwnership(Property property) {
        Long currentUserId = currentUserUtil.getCurrentUserId();
        if (!property.getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("Access denied — you are not the owner");
        }
    }

    private void checkOwnershipOrAdmin(Property property) {
        Long currentUserId = currentUserUtil.getCurrentUserId();
        boolean isAdmin = currentUserUtil.hasRole("ADMIN");
        if (!property.getOwnerId().equals(currentUserId) && !isAdmin) {
            throw new AccessDeniedException("Access denied");
        }
    }
}