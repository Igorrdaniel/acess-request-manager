package com.example.acess_request_manager.application.module.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.example.acess_request_manager.application.module.dto.ModuleDto;
import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import com.example.acess_request_manager.domain.module.repository.ModuleRepository;
import com.example.acess_request_manager.domain.user.model.Department;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuleServiceImplTest {

  @Mock private ModuleRepository moduleRepository;

  @InjectMocks private ModuleServiceImpl moduleService;

  private ModuleEntity module;

  @BeforeEach
  void setUp() {
    module = new ModuleEntity();

    module.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    module.setName("Portal");
    module.setDescription("Desc");
    module.setActive(true);
    module.setAllowedDepartments(List.of(Department.TI));
    module.setIncompatibleModules(Set.of());
  }

  @Test
  void getAllModules_Success() {
    when(moduleRepository.findAllWithDetails()).thenReturn(List.of(module));

    List<ModuleDto> dtos = moduleService.getAllModules();

    assertEquals(1, dtos.size());
    assertEquals("Portal", dtos.getFirst().getName());
  }

  @Test
  void getAllModules_Empty() {
    when(moduleRepository.findAllWithDetails()).thenReturn(List.of());

    List<ModuleDto> dtos = moduleService.getAllModules();

    assertEquals(0, dtos.size());
  }
}
