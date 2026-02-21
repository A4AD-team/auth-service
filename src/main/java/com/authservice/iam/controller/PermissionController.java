package com.authservice.iam.controller;

import com.authservice.iam.dto.CreatePermissionRequest;
import com.authservice.iam.dto.PermissionResponse;
import com.authservice.iam.entity.Permission;
import com.authservice.iam.service.PermissionService;
import com.authservice.iam.util.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public List<PermissionResponse> listPermissions() {
        return permissionService.listPermissions().stream()
            .map(DtoMapper::toPermissionResponse)
            .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        Permission permission = permissionService.createPermission(request);
        return DtoMapper.toPermissionResponse(permission);
    }
}
