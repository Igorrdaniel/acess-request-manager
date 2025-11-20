package com.example.acess_request_manager.application.module.service.impl;

import com.example.acess_request_manager.application.module.dto.ModuleDto;
import com.example.acess_request_manager.application.module.service.ModuleService;
import com.example.acess_request_manager.domain.module.repository.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {

  private final ModuleRepository moduleRepository;

  public ModuleServiceImpl(ModuleRepository moduleRepository) {
    this.moduleRepository = moduleRepository;
  }

  @Override
  public List<ModuleDto> getAllModules() {
    return moduleRepository.findAll().stream()
        .map(ModuleDto::map)
        .toList();
  }
}
