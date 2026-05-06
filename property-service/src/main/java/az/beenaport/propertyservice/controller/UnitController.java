package az.beenaport.propertyservice.controller;

import az.beenaport.propertyservice.dto.request.UnitRequest;
import az.beenaport.propertyservice.dto.response.UnitResponse;
import az.beenaport.propertyservice.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buildings/floors")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping("/{floorId}/units")
    public ResponseEntity<UnitResponse> create(
            @PathVariable Long floorId,
            @Valid @RequestBody UnitRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(unitService.create(floorId, request));
    }

    @GetMapping("/{floorId}/units")
    public ResponseEntity<List<UnitResponse>> getByFloor(
            @PathVariable Long floorId) {
        return ResponseEntity.ok(unitService.getByFloor(floorId));
    }

    @GetMapping("/units/{id}")
    public ResponseEntity<UnitResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getById(id));
    }

    @PutMapping("/units/{id}")
    public ResponseEntity<UnitResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UnitRequest request) {
        return ResponseEntity.ok(unitService.update(id, request));
    }

    @DeleteMapping("/units/{id}/soft")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        unitService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/units/{id}/hard")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        unitService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}