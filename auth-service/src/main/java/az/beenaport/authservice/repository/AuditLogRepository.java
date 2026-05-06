package az.beenaport.authservice.repository;

import az.beenaport.authservice.entity.AuditLog;
import az.beenaport.authservice.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findAllByOrderByCreatedDateDesc(Pageable pageable);
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByAction(AuditAction action);
}