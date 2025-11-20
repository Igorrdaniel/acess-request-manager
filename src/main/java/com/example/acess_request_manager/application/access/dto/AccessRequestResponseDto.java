package com.example.acess_request_manager.application.access.dto;

import com.example.acess_request_manager.application.module.dto.ModuleDto;
import com.example.acess_request_manager.domain.access.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record AccessRequestResponseDto(
    UUID id,
    String protocol,
    Set<ModuleDto> modules,
    String justification,
    boolean urgent,
    Status status,
    LocalDateTime requestDate,
    LocalDateTime expirationDate,
    String denialReason,
    List<String> history) {}
