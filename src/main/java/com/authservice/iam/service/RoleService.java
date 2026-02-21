package com.authservice.iam.service;

import com.authservice.iam.dto.CreateRoleRequest;
import com.authservice.iam.entity.Permission;
import com.authservice.iam.entity.Role;
import com.authservice.iam.repository.PermissionRepository;
import com.authservice.iam.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public Role createRole(CreateRoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new ResponseStatusException(CONFLICT, "Role already exists");
        }

        Role role = new Role();
        role.setName(request.name());
        role.setDescription(request.description());

        if (request.permissions() != null && !request.permissions().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllByNameIn(request.permissions());
            if (permissions.size() != request.permissions().size()) {
                throw new ResponseStatusException(NOT_FOUND, "Some permissions not found");
            }
            role.setPermissions(new HashSet<>(permissions));
        }

        return roleRepository.save(role);
    }
}
