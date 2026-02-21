package com.authservice.iam.service;

import com.authservice.iam.dto.CreatePermissionRequest;
import com.authservice.iam.entity.Permission;
import com.authservice.iam.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<Permission> listPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional
    public Permission createPermission(CreatePermissionRequest request) {
        if (permissionRepository.existsByName(request.name())) {
            throw new ResponseStatusException(CONFLICT, "Permission already exists");
        }

        Permission permission = new Permission();
        permission.setName(request.name());
        permission.setDescription(request.description());
        return permissionRepository.save(permission);
    }
}
