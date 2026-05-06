package az.beenaport.authservice.service;

import az.beenaport.authservice.dto.response.AuditLogResponse;
import az.beenaport.authservice.entity.Users;
import az.beenaport.authservice.enums.AuditAction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditLogService {

    void log(Users user, AuditAction action, HttpServletRequest request);
    Page<AuditLogResponse> getAllLogs(Pageable pageable);
    List<AuditLogResponse> getLogsByUser(Long userId);
}
