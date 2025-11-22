package com.example.acess_request_manager.application.module.service.impl;

import com.example.acess_request_manager.application.module.dto.ModuleDto;
import com.example.acess_request_manager.application.module.service.ModuleService;
import com.example.acess_request_manager.domain.module.repository.ModuleRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModuleServiceImpl implements ModuleService {

  private final ModuleRepository moduleRepository;

  public ModuleServiceImpl(ModuleRepository moduleRepository) {
    this.moduleRepository = moduleRepository;
  }

  @Transactional(readOnly = true)
  @Override
  public List<ModuleDto> getAllModules() {
    return moduleRepository.findAllWithDetails().stream().map(ModuleDto::map).toList();
  }
}
