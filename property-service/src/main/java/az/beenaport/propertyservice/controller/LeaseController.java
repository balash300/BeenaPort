package az.beenaport.propertyservice.controller;

import az.beenaport.propertyservice.dto.request.LeaseRequest;
import az.beenaport.propertyservice.dto.response.LeaseResponse;
import az.beenaport.propertyservice.service.LeaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buildings/floors/units")
@RequiredArgsConstructor
public class LeaseController {

    private final LeaseService leaseService;

    @PostMapping("/{unitId}/leases")
    public ResponseEntity<LeaseResponse> create(
            @PathVariable Long unitId,
            @Valid @RequestBody LeaseRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leaseService.create(unitId, request));
    }

    @GetMapping("/{unitId}/leases")
    public ResponseEntity<List<LeaseResponse>> getByUnit(
            @PathVariable Long unitId) {
        return ResponseEntity.ok(leaseService.getByUnit(unitId));
    }

    @GetMapping("/leases/my")
    public ResponseEntity<List<LeaseResponse>> getMyLeases() {
        return ResponseEntity.ok(leaseService.getMyLeases());
    }

    @GetMapping("/leases/{id}")
    public ResponseEntity<LeaseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(leaseService.getById(id));
    }

    @PutMapping("/leases/{id}")
    public ResponseEntity<LeaseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LeaseRequest request) {
        return ResponseEntity.ok(leaseService.update(id, request));
    }

    @DeleteMapping("/leases/{id}/terminate")
    public ResponseEntity<Void> terminate(@PathVariable Long id) {
        leaseService.terminate(id);
        return ResponseEntity.noContent().build();
    }
}