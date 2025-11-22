package com.example.acess_request_manager.application.module.dto;

import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import com.example.acess_request_manager.domain.user.model.Department;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class ModuleDto {

  private UUID id;
  private String name;
  private String description;
  private Boolean active;
  private List<Department> allowedDepartments;
  private Set<UUID> incompatibleModuleIds;

  public static ModuleDto map(ModuleEntity moduleEntity) {
    ModuleDto moduleDto = new ModuleDto();

    moduleDto.setId(moduleEntity.getId());
    moduleDto.setName(moduleEntity.getName());
    moduleDto.setDescription(moduleEntity.getDescription());
    moduleDto.setActive(moduleEntity.getActive());
    moduleDto.setAllowedDepartments(moduleEntity.getAllowedDepartments());

    moduleDto.setIncompatibleModuleIds(
        moduleEntity.getIncompatibleModules().stream()
            .map(ModuleEntity::getId)
            .collect(java.util.stream.Collectors.toSet()));

    return moduleDto;
  }
}
