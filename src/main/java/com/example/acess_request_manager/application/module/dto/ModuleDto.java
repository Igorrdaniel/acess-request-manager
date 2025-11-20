package com.example.acess_request_manager.application.module.dto;

import com.example.acess_request_manager.domain.user.model.Department;
import java.util.Set;
import java.util.UUID;

public record ModuleDto(
    UUID id,
    String name,
    String description,
    Boolean active,
    Set<Department> allowedDepartments,
    Set<UUID> incompatibleModuleIds) {}
