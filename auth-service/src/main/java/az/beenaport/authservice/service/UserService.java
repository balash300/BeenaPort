package az.beenaport.authservice.service;

import az.beenaport.authservice.dto.request.LoginRequest;
import az.beenaport.authservice.dto.request.RegisterRequest;
import az.beenaport.authservice.dto.response.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);
    UserResponse login(LoginRequest request);
}
