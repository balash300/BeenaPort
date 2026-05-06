package az.beenaport.propertyservice.mapper;

import az.beenaport.propertyservice.dto.request.FloorRequest;
import az.beenaport.propertyservice.dto.response.FloorResponse;
import az.beenaport.propertyservice.entity.Floor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FloorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "units", ignore = true)
    Floor toEntity(FloorRequest request);

    @Mapping(target = "buildingId", source = "building.id")
    @Mapping(target = "units", source = "units")
    FloorResponse toResponse(Floor floor);
}