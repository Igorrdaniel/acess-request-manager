package com.example.acess_request_manager.application.access.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.acess_request_manager.application.access.dto.AccessRequestCreateDto;
import com.example.acess_request_manager.application.access.dto.AccessRequestResponseDto;
import com.example.acess_request_manager.domain.access.model.AccessRequest;
import com.example.acess_request_manager.domain.access.model.Status;
import com.example.acess_request_manager.domain.access.repository.AccessRequestRepository;
import com.example.acess_request_manager.domain.activity.model.UserActiveModule;
import com.example.acess_request_manager.domain.activity.repository.UserActiveModuleRepository;
import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import com.example.acess_request_manager.domain.module.repository.ModuleRepository;
import com.example.acess_request_manager.domain.user.model.Department;
import com.example.acess_request_manager.domain.user.model.User;
import com.example.acess_request_manager.domain.user.repository.UserRepository;
import com.example.acess_request_manager.security.jwt.impl.UserDetailsImpl;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AccessRequestServiceImplTest {

  @Mock private AccessRequestRepository accessRequestRepository;

  @Mock private ModuleRepository moduleRepository;

  @Mock private UserRepository userRepository;

  @Mock private UserActiveModuleRepository userActiveModuleRepository;

  @InjectMocks private AccessRequestServiceImpl service;

  private User user;

  private ModuleEntity module1;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    user.setEmail("test@user.com");
    user.setDepartment(Department.TI);

    module1 = new ModuleEntity();
    module1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    module1.setName("Portal");
    module1.setActive(true);
    module1.setAllowedDepartments(List.of(Department.TI));
    module1.setIncompatibleModules(new HashSet<>());

    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    Authentication authentication = mock(Authentication.class);
    lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
    SecurityContext securityContext = mock(SecurityContext.class);
    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    lenient().when(userRepository.findByEmail(eq("test@user.com"))).thenReturn(Optional.of(user));
  }

  @Test
  void createRequest_Success_Approved() {
    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto =
        new AccessRequestCreateDto(
            moduleIds, "Justificativa válida com mais de 20 caracteres", true);

    when(moduleRepository.findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(module1));
    when(userActiveModuleRepository.existsByUserAndModule(eq(user), eq(module1)))
        .thenReturn(false);
    when(accessRequestRepository.findAll()).thenReturn(List.of());
    when(userActiveModuleRepository.countByUser(eq(user))).thenReturn(0);

    String result = service.createRequest(dto);

    assertTrue(result.contains("Solicitação criada com sucesso"));

    ArgumentCaptor<AccessRequest> requestCaptor = ArgumentCaptor.forClass(AccessRequest.class);
    verify(accessRequestRepository).save(requestCaptor.capture());
    AccessRequest savedRequest = requestCaptor.getValue();
    assertEquals(Status.ATIVO, savedRequest.getStatus());
    assertTrue(savedRequest.getUrgent());
    assertEquals("Justificativa válida com mais de 20 caracteres", savedRequest.getJustification());

    ArgumentCaptor<UserActiveModule> uamCaptor =
        ArgumentCaptor.forClass(UserActiveModule.class);
    verify(userActiveModuleRepository).save(uamCaptor.capture());
    assertEquals(module1, uamCaptor.getValue().getModule());
  }

  @Test
  void createRequest_Denied_DepartmentIncompatibility() {
    user.setDepartment(Department.OUTROS);
    module1.setAllowedDepartments(List.of(Department.TI));

    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto =
        new AccessRequestCreateDto(
            moduleIds, "Justificativa válida com mais de 20 caracteres", false);

    when(moduleRepository.findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(module1));
    when(userActiveModuleRepository.existsByUserAndModule(eq(user), eq(module1)))
        .thenReturn(false);
    when(accessRequestRepository.findAll()).thenReturn(List.of());

    String result = service.createRequest(dto);

    assertTrue(result.contains("Departamento sem permissão"));

    ArgumentCaptor<AccessRequest> captor = ArgumentCaptor.forClass(AccessRequest.class);
    verify(accessRequestRepository).save(captor.capture());
    assertEquals(Status.NEGADO, captor.getValue().getStatus());
    verify(userActiveModuleRepository, never()).save(any());
  }

  @Test
  void createRequest_Denied_ModuleIncompatibility() {
    ModuleEntity incompatible = new ModuleEntity();
    incompatible.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    module1.getIncompatibleModules().add(incompatible);

    UserActiveModule active = new UserActiveModule();
    active.setModule(incompatible);

    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto =
        new AccessRequestCreateDto(
            moduleIds, "Justificativa válida com mais de 20 caracteres", false);

    when(moduleRepository.findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(module1));
    when(userActiveModuleRepository.existsByUserAndModule(eq(user), eq(module1)))
        .thenReturn(false);
    when(accessRequestRepository.findAll()).thenReturn(List.of());
    when(userActiveModuleRepository.findByUser(eq(user))).thenReturn(List.of(active));

    String result = service.createRequest(dto);

    assertTrue(result.contains("Módulo incompatível"));

    verify(accessRequestRepository).save(any(AccessRequest.class));
    verify(userActiveModuleRepository, never()).save(any());
  }

  @Test
  void createRequest_Denied_LimitExceeded() {
    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto =
        new AccessRequestCreateDto(
            moduleIds, "Justificativa válida com mais de 20 caracteres", false);

    user.setDepartment(Department.FINANCEIRO);

    when(moduleRepository.findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(module1));
    when(userActiveModuleRepository.existsByUserAndModule(eq(user), eq(module1)))
        .thenReturn(false);
    when(accessRequestRepository.findAll()).thenReturn(List.of());

    String result = service.createRequest(dto);

    assertFalse(result.contains("Limite de módulos ativos atingido"));

    verify(accessRequestRepository).save(any(AccessRequest.class));
    verify(userActiveModuleRepository, never()).save(any());
  }

  @Test
  void createRequest_Denied_GenericJustification() {
    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto = new AccessRequestCreateDto(moduleIds, "teste", false);

    assertThrows(
        IllegalArgumentException.class,
        () -> service.createRequest(dto),
        "Justificativa insuficiente ou genérica");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void createRequest_Denied_ModuleInactive() {
    module1.setActive(false);

    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto =
        new AccessRequestCreateDto(
            moduleIds, "Justificativa válida com mais de 20 caracteres", false);

    when(moduleRepository.findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(module1));

    assertThrows(
        IllegalArgumentException.class, () -> service.createRequest(dto), "Módulo inativo");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void createRequest_Denied_ExistingAccess() {
    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto =
        new AccessRequestCreateDto(
            moduleIds, "Justificativa válida com mais de 20 caracteres", false);

    when(moduleRepository.findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(module1));
    when(userActiveModuleRepository.existsByUserAndModule(eq(user), eq(module1)))
        .thenReturn(true);

    assertThrows(
        IllegalArgumentException.class,
        () -> service.createRequest(dto),
        "Já possui acesso ao módulo");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void createRequest_Denied_ExistingActiveRequest() {
    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto =
        new AccessRequestCreateDto(
            moduleIds, "Justificativa válida com mais de 20 caracteres", false);

    AccessRequest existing = new AccessRequest();
    existing.setUser(user);
    existing.setModules(Set.of(module1));
    existing.setStatus(Status.ATIVO);

    when(moduleRepository.findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(module1));
    when(userActiveModuleRepository.existsByUserAndModule(eq(user), eq(module1)))
        .thenReturn(false);
    when(accessRequestRepository.findAll()).thenReturn(List.of(existing));

    assertThrows(
        IllegalArgumentException.class,
        () -> service.createRequest(dto),
        "Solicitação ativa existente");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void createRequest_Denied_InvalidModuleCount() {
    Set<UUID> moduleIds = new HashSet<>(); // 0 módulos
    AccessRequestCreateDto dto =
        new AccessRequestCreateDto(
            moduleIds, "Justificativa válida com mais de 20 caracteres", false);

    assertThrows(
        IllegalArgumentException.class,
        () -> service.createRequest(dto),
        "Selecione entre 1 e 3 módulos");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void createRequest_Denied_InvalidJustificationLength() {
    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto = new AccessRequestCreateDto(moduleIds, "curta", false);

    assertThrows(
        IllegalArgumentException.class,
        () -> service.createRequest(dto),
        "Justificativa deve ter entre 20 e 500 caracteres");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void createRequest_ModuleNotFound() {
    Set<UUID> moduleIds = Set.of(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    AccessRequestCreateDto dto =
        new AccessRequestCreateDto(
            moduleIds, "Justificativa válida com mais de 20 caracteres", false);

    when(moduleRepository.findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class, () -> service.createRequest(dto), "Módulo não encontrado");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void getUserRequests_Success() {
    AccessRequest request = new AccessRequest();
    request.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    request.setProtocol("SOL-20251120-1234");
    request.setStatus(Status.ATIVO);
    request.setUrgent(true);

    Page<AccessRequest> page = new PageImpl<>(List.of(request));

    // usa matchers para não depender da instância exata de Pageable criada no service
    when(accessRequestRepository.findByUserWithFilter(
            eq(user),
            eq("teste"),
            eq(Status.ATIVO),
            eq(LocalDateTime.of(2025, 11, 20, 22, 10)),
            eq(LocalDateTime.of(2025, 11, 30, 22, 10)),
            eq(true),
            any(Pageable.class)))
        .thenReturn(page);

    Page<AccessRequestResponseDto> result =
        service.getUserRequest(
            "teste",
            Status.ATIVO,
            LocalDateTime.of(2025, 11, 20, 22, 10),
            LocalDateTime.of(2025, 11, 30, 22, 10),
            true,
            10);

    assertEquals(1, result.getTotalElements());
    assertEquals("SOL-20251120-1234", result.getContent().getFirst().getProtocol());

    verify(accessRequestRepository)
        .findByUserWithFilter(
            eq(user),
            eq("teste"),
            eq(Status.ATIVO),
            eq(LocalDateTime.of(2025, 11, 20, 22, 10)),
            eq(LocalDateTime.of(2025, 11, 30, 22, 10)),
            eq(true),
            any(Pageable.class));
  }

  @Test
  void getUserRequests_WithFilters() {
    Status status = Status.ATIVO;
    Boolean urgent = true;
    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now();

    Pageable pageable = PageRequest.of(0, 10, Sort.by("requestDate").descending());

    when(accessRequestRepository.findByUserWithFilter(
            eq(user), eq("protocol"), eq(status), eq(start), eq(end), eq(urgent), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of()));

    service.getUserRequest("protocol", status, start, end, urgent, 0);

    verify(accessRequestRepository)
        .findByUserWithFilter(
            eq(user), eq("protocol"), eq(status), eq(start), eq(end), eq(urgent), eq(pageable));
  }

  @Test
  void getRequestDetails_Success() {
    AccessRequest request = new AccessRequest();
    request.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    request.setUser(user);
    request.setProtocol("SOL-20251120-1234");
    request.setUrgent(true);

    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(request));

    AccessRequestResponseDto dto =
        service.getRequestDetails(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

    assertEquals("SOL-20251120-1234", dto.getProtocol());

    verify(accessRequestRepository)
        .findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));
  }

  @Test
  void getRequestDetails_NotFound() {
    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> service.getRequestDetails(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")),
        "Solicitação não encontrada");

    verify(accessRequestRepository)
        .findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));
  }

  @Test
  void getRequestDetails_NotOwnRequest() {
    AccessRequest request = new AccessRequest();
    request.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    request.setUser(new User());

    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(request));

    assertThrows(
        IllegalArgumentException.class,
        () -> service.getRequestDetails(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")),
        "Acesso negado");

    verify(accessRequestRepository)
        .findById(eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));
  }

  @Test
  void renewRequest_Success() {
    AccessRequest oldRequest = new AccessRequest();
    oldRequest.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    oldRequest.setUser(user);
    oldRequest.setStatus(Status.ATIVO);
    oldRequest.setModules(Set.of(module1));
    oldRequest.setExpirationDate(LocalDateTime.now().plusDays(29)); // Dentro de 30 dias
    oldRequest.setProtocol("old");

    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(oldRequest));
    when(userActiveModuleRepository.countByUser(eq(user))).thenReturn(0);
    when(userActiveModuleRepository.findByUser(eq(user))).thenReturn(List.of());

    String result = service.renewRequest(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

    assertTrue(result.contains("Renovação criada com sucesso"));

    ArgumentCaptor<AccessRequest> captor = ArgumentCaptor.forClass(AccessRequest.class);
    verify(accessRequestRepository).save(captor.capture());
    assertEquals(Status.ATIVO, captor.getValue().getStatus());
    assertEquals("Renovação de old", captor.getValue().getJustification());
  }

  @Test
  void renewRequest_Denied_NotActive() {
    AccessRequest oldRequest = new AccessRequest();
    oldRequest.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    oldRequest.setUser(user);
    oldRequest.setStatus(Status.NEGADO);

    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(oldRequest));

    assertThrows(
        IllegalArgumentException.class,
        () -> service.renewRequest(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")),
        "Não pode renovar");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void renewRequest_Denied_TooEarly() {
    AccessRequest oldRequest = new AccessRequest();
    oldRequest.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    oldRequest.setUser(user);
    oldRequest.setStatus(Status.ATIVO);
    oldRequest.setExpirationDate(LocalDateTime.now().plusDays(31));

    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(oldRequest));

    assertThrows(
        IllegalArgumentException.class,
        () -> service.renewRequest(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")),
        "Renovação apenas nos últimos 30 dias");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void renewRequest_Denied_BusinessRule() {
    AccessRequest oldRequest = new AccessRequest();
    oldRequest.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    oldRequest.setUser(user);
    oldRequest.setStatus(Status.ATIVO);
    oldRequest.setModules(Set.of(module1));
    oldRequest.setExpirationDate(LocalDateTime.now().plusDays(29));

    user.setDepartment(Department.OUTROS);
    module1.setAllowedDepartments(List.of(Department.TI));

    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(oldRequest));

    String result = service.renewRequest(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

    assertTrue(result.contains("negada. Motivo: Departamento sem permissão"));

    verify(accessRequestRepository).save(any(AccessRequest.class));
  }

  @Test
  void cancelRequest_Success() {
    AccessRequest request = new AccessRequest();
    request.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    request.setUser(user);
    request.setStatus(Status.ATIVO);
    request.setModules(Set.of(module1));

    UserActiveModule uam = new UserActiveModule();
    uam.setUser(user);
    uam.setModule(module1);

    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(request));
    when(userActiveModuleRepository.findByUser(eq(user))).thenReturn(List.of(uam));

    service.cancelRequest(
        UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
        "Motivo válido com mais de 10 caracteres");

    ArgumentCaptor<AccessRequest> captor = ArgumentCaptor.forClass(AccessRequest.class);
    verify(accessRequestRepository).save(captor.capture());
    assertEquals(Status.CANCELADO, captor.getValue().getStatus());
    verify(userActiveModuleRepository).delete(eq(uam));
  }

  @Test
  void cancelRequest_Denied_NotActive() {
    AccessRequest request = new AccessRequest();
    request.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    request.setUser(user);
    request.setStatus(Status.NEGADO);

    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(request));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            service.cancelRequest(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Motivo válido"),
        "Não pode cancelar");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void cancelRequest_Denied_InvalidMotivoLength() {
    AccessRequest request = new AccessRequest();
    request.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    request.setUser(user);
    request.setStatus(Status.ATIVO);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            service.cancelRequest(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "curto"),
        "Motivo deve ter entre 10 e 200 caracteres");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void cancelRequest_NotFound() {
    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () ->
            service.cancelRequest(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Motivo válido"),
        "Solicitação não encontrada");

    verify(accessRequestRepository, never()).save(any());
  }

  @Test
  void cancelRequest_NotOwn() {
    AccessRequest request = new AccessRequest();
    request.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    request.setUser(new User()); // Outro

    when(accessRequestRepository.findById(
            eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
        .thenReturn(Optional.of(request));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            service.cancelRequest(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Motivo válido"),
        "Não pode cancelar");

    verify(accessRequestRepository, never()).save(any());
  }
}
