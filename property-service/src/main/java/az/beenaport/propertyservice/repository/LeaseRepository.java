package az.beenaport.propertyservice.repository;

import az.beenaport.propertyservice.entity.Lease;
import az.beenaport.propertyservice.enums.LeaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {
    List<Lease> findByUnitId(Long unitId);
    List<Lease> findByTenantId(Long tenantId);
    Optional<Lease> findByUnitIdAndStatus(Long unitId, LeaseStatus status);
    boolean existsByUnitIdAndStatus(Long unitId, LeaseStatus status);
}