package com.example.acess_request_manager.application.module.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.example.acess_request_manager.application.module.dto.ModuleDto;
import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import com.example.acess_request_manager.domain.module.repository.ModuleRepository;
import com.example.acess_request_manager.domain.user.model.Department;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ModuleServiceImplTest {

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private ModuleServiceImpl moduleService;

    @Test
    void getAllModules_Success() {
    ModuleEntity module = new ModuleEntity();
        module.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        module.setName("Portal");
        module.setDescription("Desc");
        module.setActive(true);
        module.setAllowedDepartments(Set.of(Department.TI));
        module.setIncompatibleModules(Set.of());

        when(moduleRepository.findAll()).thenReturn(List.of(module));

        List<ModuleDto> dtos = moduleService.getAllModules();

        assertEquals(1, dtos.size());
        assertEquals("Portal", dtos.getFirst().getName());
    }

    @Test
    void getAllModules_Empty() {
        when(moduleRepository.findAll()).thenReturn(List.of());

        List<ModuleDto> dtos = moduleService.getAllModules();

        assertEquals(0, dtos.size());
    }
}
