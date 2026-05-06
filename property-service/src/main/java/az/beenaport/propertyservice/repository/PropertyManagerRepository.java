package az.beenaport.propertyservice.repository;

import az.beenaport.propertyservice.entity.PropertyManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyManagerRepository extends JpaRepository<PropertyManager, Long> {
    List<PropertyManager> findByPropertyIdAndActiveTrue(Long propertyId);
    Optional<PropertyManager> findByPropertyIdAndManagerId(Long propertyId, Long managerId);
    boolean existsByPropertyIdAndManagerIdAndActiveTrue(Long propertyId, Long managerId);
}