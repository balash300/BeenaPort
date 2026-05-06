package az.beenaport.propertyservice.mapper;

import az.beenaport.propertyservice.dto.request.BuildingRequest;
import az.beenaport.propertyservice.dto.response.BuildingResponse;
import az.beenaport.propertyservice.entity.Building;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "floors", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Building toEntity(BuildingRequest request);

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "floors", source = "floors")
    BuildingResponse toResponse(Building building);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "floors", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(BuildingRequest request, @MappingTarget Building building);
}