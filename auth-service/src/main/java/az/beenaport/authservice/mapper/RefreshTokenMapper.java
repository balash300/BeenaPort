package az.beenaport.authservice.mapper;

import az.beenaport.authservice.entity.RefreshToken;
import az.beenaport.authservice.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "revoked", constant = "false")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "token", source = "token")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "expiresAt", source = "expiresAt")
    RefreshToken toEntity(String token, Users user, java.time.LocalDateTime expiresAt);
}