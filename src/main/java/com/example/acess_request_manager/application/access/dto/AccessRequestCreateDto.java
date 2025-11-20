package com.example.acess_request_manager.application.access.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record AccessRequestCreateDto(
    @NotEmpty @Size(min = 1, max = 3) Set<UUID> moduleIds,
    @NotBlank @Size(min = 20, max = 500) String justification,
    Boolean urgent) {}
