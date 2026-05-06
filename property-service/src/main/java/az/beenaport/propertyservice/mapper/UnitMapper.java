package az.beenaport.propertyservice.mapper;


import az.beenaport.propertyservice.dto.request.UnitRequest;
import az.beenaport.propertyservice.dto.response.UnitResponse;
import az.beenaport.propertyservice.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "floor", ignore = true)
    @Mapping(target = "leases", ignore = true)
    @Mapping(target = "status", constant = "VACANT")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Unit toEntity(UnitRequest request);

    @Mapping(target = "floorId", source = "floor.id")
    @Mapping(target = "leases", source = "leases")
    UnitResponse toResponse(Unit unit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "floor", ignore = true)
    @Mapping(target = "leases", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UnitRequest request, @MappingTarget Unit unit);
}