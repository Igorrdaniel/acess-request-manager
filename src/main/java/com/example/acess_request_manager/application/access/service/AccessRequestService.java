package com.example.acess_request_manager.application.access.service;

import com.example.acess_request_manager.application.access.dto.AccessRequestCreateDto;
import com.example.acess_request_manager.application.access.dto.AccessRequestResponseDto;
import com.example.acess_request_manager.domain.access.model.Status;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface AccessRequestService {

  String createRequest(@Valid AccessRequestCreateDto accessRequestCreateDto);
  
  Page<AccessRequestResponseDto> getUserRequest(
      String protocol,
      Status status,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Boolean urgent,
      int page);

  AccessRequestResponseDto getRequestDetails(UUID id);

  String renewRequest(UUID id);

  void cancelRequest(UUID id, String motivo);
}
