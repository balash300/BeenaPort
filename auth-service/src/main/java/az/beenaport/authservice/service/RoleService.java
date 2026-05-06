package az.beenaport.authservice.service;

import az.beenaport.authservice.dto.response.UserResponse;
import az.beenaport.authservice.enums.Roles;

import java.util.List;

public interface RoleService {

    UserResponse assignRole(Long userId, Roles role);
    UserResponse removeRole(Long userId, Roles role);
    List<Roles> getUserRoles(Long userId);
}
