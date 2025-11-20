package com.example.acess_request_manager.application.access.dto;

import com.example.acess_request_manager.application.module.dto.ModuleDto;
import com.example.acess_request_manager.domain.access.model.AccessRequest;
import com.example.acess_request_manager.domain.access.model.Status;
import com.example.acess_request_manager.domain.request.model.RequestHistory;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class AccessRequestResponseDto {

  private UUID id;
  private String protocol;
  private Set<ModuleDto> modules;
  private String justification;
  private boolean urgent;
  private Status status;
  private LocalDateTime requestDate;
  private LocalDateTime expirationDate;
  private String denialReason;
  private List<String> history;

  public static AccessRequestResponseDto map(AccessRequest accessRequest) {
    AccessRequestResponseDto accessRequestResponseDto = new AccessRequestResponseDto();

    accessRequestResponseDto.setId(accessRequest.getId());
    accessRequestResponseDto.setProtocol(accessRequest.getProtocol());
    accessRequestResponseDto.setModules(
        accessRequest.getModules().stream()
            .map(ModuleDto::map)
            .collect(java.util.stream.Collectors.toSet()));
    accessRequestResponseDto.setJustification(accessRequest.getJustification());
    accessRequestResponseDto.setUrgent(accessRequest.isUrgent());
    accessRequestResponseDto.setStatus(accessRequest.getStatus());
    accessRequestResponseDto.setRequestDate(accessRequest.getRequestDate());
    accessRequestResponseDto.setExpirationDate(accessRequest.getExpirationDate());
    accessRequestResponseDto.setDenialReason(accessRequest.getDenialReason());
    accessRequestResponseDto.setHistory(
        accessRequest.getHistory().stream()
            .map(RequestHistory::getDescription)
            .collect(Collectors.toList()));

    return accessRequestResponseDto;
  }
}
