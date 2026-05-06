package az.beenaport.authservice.service.impl;

import az.beenaport.authservice.dto.response.UserResponse;
import az.beenaport.authservice.entity.Users;
import az.beenaport.authservice.enums.Roles;
import az.beenaport.authservice.exception.UserNotFoundException;
import az.beenaport.authservice.mapper.UserMapper;
import az.beenaport.authservice.repository.UserRepository;
import az.beenaport.authservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse assignRole(Long userId, Roles role) {
        Users user = findUser(userId);

        if (user.getRole().contains(role)) {
            throw new RuntimeException("User already has role: " + role);
        }

        user.getRole().add(role);
        Users saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse removeRole(Long userId, Roles role) {
        Users user = findUser(userId);

        if (!user.getRole().contains(role)) {
            throw new RuntimeException("User does not have role: " + role);
        }

        if (user.getRole().size() == 1) {
            throw new RuntimeException("User must have at least one role");
        }

        user.getRole().remove(role);
        Users saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    public List<Roles> getUserRoles(Long userId) {
        Users user = findUser(userId);
        return List.copyOf(user.getRole());
    }

    private Users findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }
}