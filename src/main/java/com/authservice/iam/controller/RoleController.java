package com.authservice.iam.controller;

import com.authservice.iam.dto.CreateRoleRequest;
import com.authservice.iam.dto.RoleResponse;
import com.authservice.iam.entity.Role;
import com.authservice.iam.service.RoleService;
import com.authservice.iam.util.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> listRoles() {
        return roleService.listRoles().stream()
            .map(DtoMapper::toRoleResponse)
            .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse createRole(@Valid @RequestBody CreateRoleRequest request) {
        Role role = roleService.createRole(request);
        return DtoMapper.toRoleResponse(role);
    }
}
