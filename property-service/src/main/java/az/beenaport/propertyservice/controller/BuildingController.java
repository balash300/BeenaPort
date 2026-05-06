package az.beenaport.propertyservice.controller;

import az.beenaport.propertyservice.dto.request.BuildingRequest;
import az.beenaport.propertyservice.dto.response.BuildingResponse;
import az.beenaport.propertyservice.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping("/{propertyId}/buildings/create")
    public ResponseEntity<BuildingResponse> create(
            @PathVariable Long propertyId,
            @Valid @RequestBody BuildingRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(buildingService.create(propertyId, request));
    }

    @GetMapping("/{propertyId}/buildings")
    public ResponseEntity<List<BuildingResponse>> getByProperty(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(buildingService.getByProperty(propertyId));
    }

    @GetMapping("/buildings/{id}")
    public ResponseEntity<BuildingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(buildingService.getById(id));
    }

    @PutMapping("/buildings/{id}")
    public ResponseEntity<BuildingResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BuildingRequest request) {
        return ResponseEntity.ok(buildingService.update(id, request));
    }

    @DeleteMapping("/soft/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        buildingService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/hard/{id}")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        buildingService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}