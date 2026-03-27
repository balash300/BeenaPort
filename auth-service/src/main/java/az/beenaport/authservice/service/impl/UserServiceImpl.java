package az.beenaport.authservice.service.impl;

import az.beenaport.authservice.dto.request.LoginRequest;
import az.beenaport.authservice.dto.request.RegisterRequest;
import az.beenaport.authservice.dto.response.UserResponse;
import az.beenaport.authservice.entity.Users;
import az.beenaport.authservice.enums.Roles;
import az.beenaport.authservice.mapper.UserMapper;
import az.beenaport.authservice.repository.UserRepository;
import az.beenaport.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Users user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Roles.USER);
        user.setActive(true);

        Users savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse login(LoginRequest request) {
        return null;
    }


}
