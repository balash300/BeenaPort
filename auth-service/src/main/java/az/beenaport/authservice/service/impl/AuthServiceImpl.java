package az.beenaport.authservice.service.impl;

import az.beenaport.authservice.auth.JwtService;
import az.beenaport.authservice.dto.request.LoginRequest;
import az.beenaport.authservice.dto.request.RefreshRequest;
import az.beenaport.authservice.dto.request.RegisterRequest;
import az.beenaport.authservice.dto.response.AuthResponse;
import az.beenaport.authservice.dto.response.UserResponse;
import az.beenaport.authservice.entity.RefreshToken;
import az.beenaport.authservice.entity.Users;
import az.beenaport.authservice.enums.AuditAction;
import az.beenaport.authservice.exception.EmailAlreadyExistsException;
import az.beenaport.authservice.mapper.AuthResponseMapper;
import az.beenaport.authservice.mapper.RefreshTokenMapper;
import az.beenaport.authservice.mapper.UserMapper;
import az.beenaport.authservice.repository.RefreshTokenRepository;
import az.beenaport.authservice.repository.UserRepository;
import az.beenaport.authservice.service.AuditLogService;
import az.beenaport.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final AuthResponseMapper authResponseMapper;
    private final AuditLogService auditLogService;

    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        Users user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Users saved = userRepository.save(user);

        auditLogService.log(saved, AuditAction.REGISTER, httpRequest);

        return generateAuthResponse(saved);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {

        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Account is blocked");
        }

        auditLogService.log(user, AuditAction.LOGIN, httpRequest);

        return generateAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {

        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (stored.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return generateAuthResponse(stored.getUser());
    }

    @Override
    @Transactional
    public void logout(String authHeader, HttpServletRequest httpRequest) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Token is expired or invalid");
        }

        Long userId = jwtService.extractUserId(token);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        auditLogService.log(user, AuditAction.LOGOUT, httpRequest);

        refreshTokenRepository.revokeAllUserTokens(userId);
    }

    @Override
    public UserResponse getMe(String authHeader) {

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Token is invalid or expired");
        }

        Long userId = jwtService.extractUserId(token);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toResponse(user);
    }

    private AuthResponse generateAuthResponse(Users user) {

        // revoke all previous refresh tokens
        refreshTokenRepository.revokeAllUserTokens(user.getId());

        String accessToken  = jwtService.generateAccessToken(user);
        String rawRefresh   = jwtService.generateRefreshToken(user);

        // save new refresh token to DB
        RefreshToken refreshToken = refreshTokenMapper.toEntity(
                rawRefresh,
                user,
                LocalDateTime.now().plusSeconds(refreshTokenExpiry)
        );

        refreshTokenRepository.save(refreshToken);

        return authResponseMapper.toResponse(accessToken, rawRefresh, accessTokenExpiry);
    }
}
