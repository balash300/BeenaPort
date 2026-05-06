package az.beenaport.authservice.mapper;

import az.beenaport.authservice.dto.response.AuditLogResponse;
import az.beenaport.authservice.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    AuditLogResponse toResponse(AuditLog auditLog);
}