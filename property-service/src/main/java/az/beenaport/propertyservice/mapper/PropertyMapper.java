package az.beenaport.propertyservice.mapper;

import az.beenaport.propertyservice.entity.Property;
import az.beenaport.propertyservice.entity.PropertyManager;
import az.beenaport.propertyservice.dto.request.PropertyRequest;
import az.beenaport.propertyservice.dto.response.PropertyManagerResponse;
import az.beenaport.propertyservice.dto.response.PropertyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "buildings", ignore = true)
    @Mapping(target = "managers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Property toEntity(PropertyRequest request);

    @Mapping(target = "buildings", source = "buildings")
    PropertyResponse toResponse(Property property);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "buildings", ignore = true)
    @Mapping(target = "managers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PropertyRequest request, @MappingTarget Property property);

    @Mapping(target = "propertyId", source = "property.id")
    PropertyManagerResponse toManagerResponse(PropertyManager propertyManager);
}