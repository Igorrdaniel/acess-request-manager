package com.example.acess_request_manager.application.module.api;

import com.example.acess_request_manager.application.module.dto.ModuleDto;
import com.example.acess_request_manager.application.module.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import java.util.List;
import lombok.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated
@Tags(value = {@Tag(name = "Modulos", description = "Gerenciamento de Modulos")})
@RestController
@RequestMapping("/modules")
public class ModulesControlle {

  private final ModuleService moduleService;

  public ModulesControlle(ModuleService moduleService) {
    this.moduleService = moduleService;
  }

  @Operation(
      description = "Busca Todos os Modulos",
      tags = {"Modulos"})
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Modulos Encontrados com Sucesso")})
  @GetMapping
  public List<ModuleDto> getModules() {
    return moduleService.getAllModules();
  }
}
