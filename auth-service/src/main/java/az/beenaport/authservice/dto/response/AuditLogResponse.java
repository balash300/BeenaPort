package az.beenaport.authservice.dto.response;

import az.beenaport.authservice.enums.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private Long userId;
    private String email;
    private AuditAction action;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdDate;
}