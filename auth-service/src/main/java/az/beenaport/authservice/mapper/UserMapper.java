package az.beenaport.authservice.mapper;

import az.beenaport.authservice.dto.request.RegisterRequest;
import az.beenaport.authservice.dto.response.UserResponse;
import az.beenaport.authservice.entity.Users;
import az.beenaport.authservice.enums.Roles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", source = "roles")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    Users toEntity(RegisterRequest request);

    @Mapping(target = "id", expression = "java(user.getId().toString())")
    @Mapping(target = "roles", source = "role")
    UserResponse toResponse(Users user);

    default Set<Roles> mapRolesToRole(Set<Roles> roles) {
        return roles == null ? new HashSet<>() : new HashSet<>(roles);
    }
}
