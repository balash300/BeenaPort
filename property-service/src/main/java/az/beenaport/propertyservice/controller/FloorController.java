package az.beenaport.propertyservice.controller;

import az.beenaport.propertyservice.dto.request.FloorRequest;
import az.beenaport.propertyservice.dto.response.FloorResponse;
import az.beenaport.propertyservice.service.FloorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    @PostMapping("/{buildingId}/floors")
    public ResponseEntity<FloorResponse> create(
            @PathVariable Long buildingId,
            @Valid @RequestBody FloorRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(floorService.create(buildingId, request));
    }

    @GetMapping("/{buildingId}/floors")
    public ResponseEntity<List<FloorResponse>> getByBuilding(
            @PathVariable Long buildingId) {
        return ResponseEntity.ok(floorService.getByBuilding(buildingId));
    }

    @GetMapping("/floors/{id}")
    public ResponseEntity<FloorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(floorService.getById(id));
    }

    @PutMapping("/floors/{id}")
    public ResponseEntity<FloorResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FloorRequest request) {
        return ResponseEntity.ok(floorService.update(id, request));
    }

    @DeleteMapping("/floors/soft/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        floorService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/floors/hard/{id}")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        floorService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}