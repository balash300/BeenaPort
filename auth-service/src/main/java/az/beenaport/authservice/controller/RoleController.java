package az.beenaport.authservice.controller;

import az.beenaport.authservice.dto.response.UserResponse;
import az.beenaport.authservice.enums.Roles;
import az.beenaport.authservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Roles>> getAllRoles() {
        return ResponseEntity.ok(List.of(Roles.values()));
    }

    @PostMapping("/users/{userId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable Long userId,
            @RequestParam Roles role) {
        return ResponseEntity.ok(roleService.assignRole(userId, role));
    }

    @DeleteMapping("/users/{userId}/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> removeRole(
            @PathVariable Long userId,
            @RequestParam Roles role) {
        return ResponseEntity.ok(roleService.removeRole(userId, role));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Roles>> getUserRoles(@PathVariable Long userId) {
        return ResponseEntity.ok(roleService.getUserRoles(userId));
    }
}