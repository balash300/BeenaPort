package az.beenaport.propertyservice.mapper;

import az.beenaport.propertyservice.dto.request.LeaseRequest;
import az.beenaport.propertyservice.dto.response.LeaseResponse;
import az.beenaport.propertyservice.entity.Lease;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LeaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    Lease toEntity(LeaseRequest request);

    @Mapping(target = "unitId", source = "unit.id")
    LeaseResponse toResponse(Lease lease);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(LeaseRequest request, @MappingTarget Lease lease);
}