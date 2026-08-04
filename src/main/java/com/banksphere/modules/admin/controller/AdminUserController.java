package com.banksphere.modules.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User API")
@Slf4j
public class AdminUserController {

    @GetMapping("/all")
    @Operation(summary = "Get all users")
    public ResponseEntity<Void> getAllUsers() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<Void> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/lock")
    @Operation(summary = "Lock user")
    public ResponseEntity<Void> lockUser(@PathVariable UUID userId) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/unlock")
    @Operation(summary = "Unlock user")
    public ResponseEntity<Void> unlockUser(@PathVariable UUID userId) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        return ResponseEntity.ok().build();
    }
}
