package az.beenaport.propertyservice.repository;

import az.beenaport.propertyservice.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwnerId(Long ownerId);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);   // ← update zamanı eyni adı yoxlamaq üçün
    boolean existsByOwnerIdAndId(Long ownerId, Long propertyId);
}