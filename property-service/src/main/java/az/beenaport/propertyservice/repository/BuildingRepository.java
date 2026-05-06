package az.beenaport.propertyservice.repository;

import az.beenaport.propertyservice.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByPropertyId(Long propertyId);
    boolean existsByPropertyIdAndName(Long propertyId, String name);
    boolean existsByPropertyIdAndNameAndIdNot(Long propertyId, String name, Long id);
}