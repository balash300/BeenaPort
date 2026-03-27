package az.beenaport.authservice.mapper;

import az.beenaport.authservice.dto.request.RegisterRequest;
import az.beenaport.authservice.dto.response.UserResponse;
import az.beenaport.authservice.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    Users toEntity(RegisterRequest request);

    UserResponse toResponse(Users user);
}
