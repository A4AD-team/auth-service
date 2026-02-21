package com.authservice.iam.service;

import com.authservice.iam.dto.CreateDepartmentRequest;
import com.authservice.iam.entity.Department;
import com.authservice.iam.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Department> listDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department createDepartment(CreateDepartmentRequest request) {
        if (departmentRepository.existsByName(request.name())) {
            throw new ResponseStatusException(CONFLICT, "Department already exists");
        }

        Department department = new Department();
        department.setName(request.name());
        return departmentRepository.save(department);
    }
}
