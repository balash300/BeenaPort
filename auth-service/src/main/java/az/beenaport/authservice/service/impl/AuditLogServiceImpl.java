package az.beenaport.authservice.service.impl;

import az.beenaport.authservice.dto.response.AuditLogResponse;
import az.beenaport.authservice.entity.AuditLog;
import az.beenaport.authservice.entity.Users;
import az.beenaport.authservice.enums.AuditAction;
import az.beenaport.authservice.mapper.AuditLogMapper;
import az.beenaport.authservice.repository.AuditLogRepository;
import az.beenaport.authservice.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional
    public void log(Users user, AuditAction action, HttpServletRequest request) {
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .ipAddress(getClientIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    public Page<AuditLogResponse> getAllLogs(Pageable pageable) {
        return auditLogRepository
                .findAllByOrderByCreatedDateDesc(pageable)
                .map(auditLogMapper::toResponse);
    }

    @Override
    public List<AuditLogResponse> getLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId)
                .stream()
                .map(auditLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
