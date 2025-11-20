package com.example.acess_request_manager.application.user.dto;

import com.example.acess_request_manager.domain.user.model.Department;

import java.util.UUID;

public record UserDto(UUID id, String email, Department department) {}
