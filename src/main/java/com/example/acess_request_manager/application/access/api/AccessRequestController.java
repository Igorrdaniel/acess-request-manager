package com.example.acess_request_manager.application.access.api;

import com.example.acess_request_manager.application.access.dto.AccessRequestCreateDto;
import com.example.acess_request_manager.application.access.dto.AccessRequestResponseDto;
import com.example.acess_request_manager.application.access.service.AccessRequestService;
import com.example.acess_request_manager.domain.access.model.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Generated;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Generated
@Tags(value = {@Tag(name = "Requests", description = "Gerenciamento de Requests de Acesso")})
@RestController
@RequestMapping("/requests")
public class AccessRequestController {

  private final AccessRequestService accessRequestService;

  public AccessRequestController(AccessRequestService accessRequestService) {
    this.accessRequestService = accessRequestService;
  }

  @Operation(
      description = "Cria uma nova Solicitação de Acesso",
      tags = {"Requests"})
  @ApiResponses(
      value = {@ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso")})
  @PostMapping
  @SecurityRequirement(name = "bearer-key")
  public ResponseEntity<String> create(
      @Parameter(description = "Dados da Solicitação de Acesso") @Valid @RequestBody
          AccessRequestCreateDto dto) {
    return ResponseEntity.status(201).body(accessRequestService.createRequest(dto));
  }

  @Operation(
      description = "Lista Solicitações de Acesso do Usuário",
      tags = {"Requests"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Solicitações listadas com sucesso")
      })
  @GetMapping
  @SecurityRequirement(name = "bearer-key")
  public Page<AccessRequestResponseDto> list(
      @RequestParam(required = false) String protocol,
      @RequestParam(required = false) Status status,
      @RequestParam(required = false) LocalDateTime startDate,
      @RequestParam(required = false) LocalDateTime endDate,
      @RequestParam(required = false) Boolean urgent,
      @RequestParam(defaultValue = "0") int page) {
    return accessRequestService.getUserRequest(protocol, status, startDate, endDate, urgent, page);
  }

  @Operation(
      description = "Detalha uma Solicitação de Acesso",
      tags = {"Requests"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Detalhes da Solicitação retornados com sucesso")
      })
  @GetMapping("/{id}")
  @SecurityRequirement(name = "bearer-key")
  public AccessRequestResponseDto details(
      @Parameter(description = "ID da Solicitação de Acesso") @PathVariable UUID id) {
    return accessRequestService.getRequestDetails(id);
  }

  @Operation(
      description = "Renova uma Solicitação de Acesso",
      tags = {"Requests"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Solicitação renovada com sucesso")
      })
  @PutMapping("/{id}/renew")
  @SecurityRequirement(name = "bearer-key")
  public ResponseEntity<String> renew(
      @Parameter(description = "ID da Solicitação de Acesso") @PathVariable UUID id) {
    return ResponseEntity.ok(accessRequestService.renewRequest(id));
  }

  @Operation(
      description = "Cancela uma Solicitação de Acesso",
      tags = {"Requests"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Solicitação cancelada com sucesso")
      })
  @PutMapping("/{id}/cancel")
  @SecurityRequirement(name = "bearer-key")
  public ResponseEntity<Void> cancel(
      @Parameter(description = "ID da Solicitação de Acesso") @PathVariable UUID id,
      @Parameter(description = "Motivo do Cancelamento") @RequestParam String motivo) {
    accessRequestService.cancelRequest(id, motivo);
    return ResponseEntity.ok().build();
  }
}
