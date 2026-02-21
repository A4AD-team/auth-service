package com.authservice.iam.controller;

import com.authservice.iam.dto.CreateDepartmentRequest;
import com.authservice.iam.dto.DepartmentResponse;
import com.authservice.iam.entity.Department;
import com.authservice.iam.service.DepartmentService;
import com.authservice.iam.util.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<DepartmentResponse> listDepartments() {
        return departmentService.listDepartments().stream()
            .map(DtoMapper::toDepartmentResponse)
            .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponse createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        Department department = departmentService.createDepartment(request);
        return DtoMapper.toDepartmentResponse(department);
    }
}
