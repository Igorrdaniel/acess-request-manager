package com.example.acess_request_manager.application.module.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import com.example.acess_request_manager.domain.user.model.Department;

import java.util.*;

import org.junit.jupiter.api.Test;

class ModuleDtoTest {

  @Test
  void testModuleMapping() {
    UUID id = UUID.randomUUID();
    List<Department> departments = new ArrayList<>();
    departments.add(Department.TI);
    Set<ModuleEntity> incompatibleModules = new HashSet<>();
    ModuleEntity incompatibleModule = new ModuleEntity();
    incompatibleModule.setId(UUID.randomUUID());
    incompatibleModules.add(incompatibleModule);

    ModuleEntity moduleEntity = new ModuleEntity();
    moduleEntity.setId(id);
    moduleEntity.setName("Test Module");
    moduleEntity.setDescription("Test Description");
    moduleEntity.setActive(true);
    moduleEntity.setAllowedDepartments(departments);
    moduleEntity.setIncompatibleModules(incompatibleModules);

    ModuleDto moduleDto = ModuleDto.map(moduleEntity);

    assertEquals(id, moduleDto.getId());
    assertEquals("Test Module", moduleDto.getName());
    assertEquals("Test Description", moduleDto.getDescription());
    assertTrue(moduleDto.getActive());
    assertEquals(departments, moduleDto.getAllowedDepartments());
    assertTrue(moduleDto.getIncompatibleModuleIds().contains(incompatibleModule.getId()));
  }

  @Test
  void testDtoGettersAndSetters() {
    ModuleDto moduleDto = new ModuleDto();
    UUID id = UUID.randomUUID();
    List<Department> departments = new ArrayList<>();
    Set<UUID> incompatibleIds = new HashSet<>();

    moduleDto.setId(id);
    moduleDto.setName("Test");
    moduleDto.setDescription("Description");
    moduleDto.setActive(true);
    moduleDto.setAllowedDepartments(departments);
    moduleDto.setIncompatibleModuleIds(incompatibleIds);

    assertEquals(id, moduleDto.getId());
    assertEquals("Test", moduleDto.getName());
    assertEquals("Description", moduleDto.getDescription());
    assertTrue(moduleDto.getActive());
    assertEquals(departments, moduleDto.getAllowedDepartments());
    assertEquals(incompatibleIds, moduleDto.getIncompatibleModuleIds());
  }
}
