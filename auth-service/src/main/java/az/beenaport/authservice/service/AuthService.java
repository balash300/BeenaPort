package az.beenaport.authservice.service;

import az.beenaport.authservice.dto.request.LoginRequest;
import az.beenaport.authservice.dto.request.RefreshRequest;
import az.beenaport.authservice.dto.request.RegisterRequest;
import az.beenaport.authservice.dto.response.AuthResponse;
import az.beenaport.authservice.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest);
    AuthResponse login(LoginRequest request, HttpServletRequest httpRequest);
    AuthResponse refresh(RefreshRequest request);
    void logout(String authHeader, HttpServletRequest httpRequest);
    UserResponse getMe(String authHeader);
}
