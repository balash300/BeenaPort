package az.beenaport.propertyservice.repository;

import az.beenaport.propertyservice.entity.Unit;
import az.beenaport.propertyservice.enums.UnitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByFloorId(Long floorId);
    List<Unit> findByFloorIdAndStatus(Long floorId, UnitStatus status);
    boolean existsByFloorIdAndUnitNumber(Long floorId, String unitNumber);
    boolean existsByFloorIdAndUnitNumberAndIdNot(Long floorId, String unitNumber, Long id);
}