package az.beenaport.propertyservice.repository;

import az.beenaport.propertyservice.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findByBuildingId(Long buildingId);
    boolean existsByBuildingIdAndFloorNumber(Long buildingId, int floorNumber);
    boolean existsByBuildingIdAndFloorNumberAndIdNot(Long buildingId, int floorNumber, Long id);
}