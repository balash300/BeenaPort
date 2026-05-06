package az.beenaport.billingservice.controller;

import az.beenaport.billingservice.dto.request.BillingScheduleRequest;
import az.beenaport.billingservice.dto.response.BillingScheduleResponse;
import az.beenaport.billingservice.service.BillingScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class BillingScheduleController {

    private final BillingScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<BillingScheduleResponse> create(
            @Valid @RequestBody BillingScheduleRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(scheduleService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<BillingScheduleResponse>> getAll() {
        return ResponseEntity.ok(scheduleService.getAll());
    }

    @GetMapping("/lease/{leaseId}")
    public ResponseEntity<BillingScheduleResponse> getByLeaseId(
            @PathVariable Long leaseId) {
        return ResponseEntity.ok(scheduleService.getByLeaseId(leaseId));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<BillingScheduleResponse> deactivate(
            @PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.deactivate(id));
    }
}