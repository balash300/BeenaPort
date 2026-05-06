package az.beenaport.propertyservice.controller;

import az.beenaport.propertyservice.dto.request.AssignManagerRequest;
import az.beenaport.propertyservice.dto.request.PropertyRequest;
import az.beenaport.propertyservice.dto.response.PropertyManagerResponse;
import az.beenaport.propertyservice.dto.response.PropertyResponse;
import az.beenaport.propertyservice.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping("/create")
    public ResponseEntity<PropertyResponse> create(
            @Valid @RequestBody PropertyRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(propertyService.create(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PropertyResponse>> getAll() {
        return ResponseEntity.ok(propertyService.getAll());
    }

    @GetMapping("/my")
    public ResponseEntity<List<PropertyResponse>> getMyProperties() {
        return ResponseEntity.ok(propertyService.getMyProperties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequest request) {
        return ResponseEntity.ok(propertyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/managers")
    public ResponseEntity<PropertyManagerResponse> assignManager(
            @PathVariable Long id,
            @Valid @RequestBody AssignManagerRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(propertyService.assignManager(id, request));
    }

    @GetMapping("/{id}/managers")
    public ResponseEntity<List<PropertyManagerResponse>> getManagers(
            @PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getManagers(id));
    }

    @DeleteMapping("/{id}/managers/{managerId}")
    public ResponseEntity<Void> removeManager(
            @PathVariable Long id,
            @PathVariable Long managerId) {
        propertyService.removeManager(id, managerId);
        return ResponseEntity.noContent().build();
    }
}