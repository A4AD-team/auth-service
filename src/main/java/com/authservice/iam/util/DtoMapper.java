package com.authservice.iam.util;

import com.authservice.iam.dto.PermissionResponse;
import com.authservice.iam.dto.RoleResponse;
import com.authservice.iam.dto.UserResponse;
import com.authservice.iam.entity.Permission;
import com.authservice.iam.entity.Role;
import com.authservice.iam.entity.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class DtoMapper {
    private DtoMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .sorted()
            .toList();

        Set<String> permissions = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .collect(Collectors.toSet());

        List<String> permissionsList = permissions.stream().sorted().toList();

        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            roles,
            permissionsList,
            user.getCustomClaims()
        );
    }

    public static RoleResponse toRoleResponse(Role role) {
        List<String> permissions = role.getPermissions().stream()
            .map(Permission::getName)
            .sorted()
            .toList();
        return new RoleResponse(role.getId(), role.getName(), role.getDescription(), permissions);
    }

    public static PermissionResponse toPermissionResponse(Permission permission) {
        return new PermissionResponse(permission.getId(), permission.getName(), permission.getDescription());
    }
}
