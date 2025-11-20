package com.example.acess_request_manager.application.access.service.impl;

import com.example.acess_request_manager.application.access.dto.AccessRequestCreateDto;
import com.example.acess_request_manager.application.access.dto.AccessRequestResponseDto;
import com.example.acess_request_manager.application.access.service.AccessRequestService;
import com.example.acess_request_manager.domain.access.model.AccessRequest;
import com.example.acess_request_manager.domain.access.model.Status;
import com.example.acess_request_manager.domain.access.repository.AccessRequestRepository;
import com.example.acess_request_manager.domain.activity.model.UserActiviteModule;
import com.example.acess_request_manager.domain.activity.repository.UserActivityModuleRepository;
import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import com.example.acess_request_manager.domain.module.repository.ModuleRepository;
import com.example.acess_request_manager.domain.request.model.RequestHistory;
import com.example.acess_request_manager.domain.user.model.Department;
import com.example.acess_request_manager.domain.user.model.User;
import com.example.acess_request_manager.domain.user.repository.UserRepository;
import com.example.acess_request_manager.security.jwt.impl.UserDetailsImpl;
import jakarta.validation.Valid;
import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessRequestServiceImpl implements AccessRequestService {

  private final AccessRequestRepository accessRequestRepository;
  private final ModuleRepository moduleRepository;
  private final UserRepository userRepository;
  private final UserActivityModuleRepository userActivityModuleRepository;

  public AccessRequestServiceImpl(
      AccessRequestRepository accessRequestRepository,
      ModuleRepository moduleRepository,
      UserRepository userRepository,
      UserActivityModuleRepository userActivityModuleRepository) {
    this.accessRequestRepository = accessRequestRepository;
    this.moduleRepository = moduleRepository;
    this.userRepository = userRepository;
    this.userActivityModuleRepository = userActivityModuleRepository;
  }

  @Transactional
  @Override
  public String createRequest(@Valid AccessRequestCreateDto accessRequestCreateDto) {
    User user = getCurrentUser();

    Set<ModuleEntity> modules =
        accessRequestCreateDto.moduleIds().stream()
            .map(
                id ->
                    moduleRepository
                        .findById(id)
                        .orElseThrow(
                            () -> new IllegalArgumentException("Módulo não encontrado: " + id)))
            .collect(Collectors.toSet());

    validateCreateRequest(user, modules, accessRequestCreateDto.justification());

    AccessRequest accessRequest = new AccessRequest();

    accessRequest.setUser(user);
    accessRequest.setModules(modules);
    accessRequest.setJustification(accessRequestCreateDto.justification());
    accessRequest.setUrgent(accessRequestCreateDto.urgent());
    accessRequest.setRequestDate(LocalDateTime.now());
    accessRequest.setProtocol(generateProtocol());

    String denialReason = applyBusinessRules(accessRequest);
    if (denialReason == null) {
      accessRequest.setStatus(Status.ATIVO);
      accessRequest.setExpirationDate(LocalDateTime.now().plusDays(180));
      grantAccess(user, modules, accessRequest.getExpirationDate());
      addHistory(accessRequest, "Solicitação aprovada automaticamente");
      accessRequestRepository.save(accessRequest);
      return "Solicitação criada com sucesso! Protocolo: "
          + accessRequest.getProtocol()
          + ". Seus acessos já estão disponíveis!";
    } else {
      accessRequest.setStatus(Status.NEGADO);
      accessRequest.setDenialReason(denialReason);
      addHistory(accessRequest, "Solicitação negada: " + denialReason);
      accessRequestRepository.save(accessRequest);
      return "Solicitação negada. Motivo: " + denialReason;
    }
  }

  @Override
  public Page<AccessRequestResponseDto> getUserRequest(
      String protocol,
      Status status,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Boolean urgent,
      int page) {
    User user = getCurrentUser();
    Pageable pageable = (Pageable) PageRequest.of(page, 10, Sort.by("requestDate").descending());
    Page<AccessRequest> accessRequests =
        accessRequestRepository.findByUserWithFilter(
            user, protocol, status, startDate, endDate, urgent, pageable);

    return accessRequests.map(AccessRequestResponseDto::map);
  }

  @Override
  public AccessRequestResponseDto getRequestDetails(UUID id) {
    User user = getCurrentUser();
    AccessRequest request =
        accessRequestRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
    if (!request.getUser().equals(user)) {
      throw new IllegalArgumentException("Acesso negado: não é sua solicitação");
    }
    return AccessRequestResponseDto.map(request);
  }

  @Transactional
  @Override
  public String renewRequest(UUID id) {
    User user = getCurrentUser();
    AccessRequest oldRequest =
        accessRequestRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
    if (!oldRequest.getUser().equals(user) || oldRequest.getStatus() != Status.ATIVO) {
      throw new IllegalArgumentException("Não pode renovar");
    }
    if (oldRequest.getExpirationDate().isAfter(LocalDateTime.now().plusDays(30))) {
      throw new IllegalArgumentException("Renovação apenas nos últimos 30 dias");
    }

    AccessRequest newRequest = new AccessRequest();
    newRequest.setUser(user);
    newRequest.setModules(oldRequest.getModules());
    newRequest.setJustification("Renovação de " + oldRequest.getProtocol());
    newRequest.setUrgent(false);
    newRequest.setRequestDate(LocalDateTime.now());
    newRequest.setProtocol(generateProtocol());

    String denialReason = applyBusinessRules(newRequest);
    if (denialReason == null) {
      newRequest.setStatus(Status.ATIVO);
      newRequest.setExpirationDate(LocalDateTime.now().plusDays(180));
      grantAccess(user, newRequest.getModules(), newRequest.getExpirationDate());
      addHistory(newRequest, "Renovação aprovada");
      // Revoga antigo? Não especificado, mas atualiza expiração nos ativos
      userActivityModuleRepository.findByUser(user).stream()
          .filter(uam -> oldRequest.getModules().contains(uam.getModule()))
          .forEach(uam -> uam.setExpirationDate(newRequest.getExpirationDate()));
      accessRequestRepository.save(newRequest);
      return "Renovação criada com sucesso! Protocolo: " + newRequest.getProtocol();
    } else {
      newRequest.setStatus(Status.NEGADO);
      newRequest.setDenialReason(denialReason);
      addHistory(newRequest, "Renovação negada: " + denialReason);
      accessRequestRepository.save(newRequest);
      return "Renovação negada. Motivo: " + denialReason;
    }
  }

  @Transactional
  @Override
  public void cancelRequest(UUID id, String motivo) {
    if (motivo.length() < 10 || motivo.length() > 200) {
      throw new IllegalArgumentException("Motivo deve ter entre 10 e 200 caracteres");
    }
    User user = getCurrentUser();
    AccessRequest request =
        accessRequestRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
    if (!request.getUser().equals(user) || request.getStatus() != Status.ATIVO) {
      throw new IllegalArgumentException("Não pode cancelar");
    }
    request.setStatus(Status.CANCELADO);
    addHistory(request, "Cancelado: " + motivo);

    userActivityModuleRepository.findByUser(user).stream()
        .filter(uam -> request.getModules().contains(uam.getModule()))
        .forEach(userActivityModuleRepository::delete);

    accessRequestRepository.save(request);
  }

  private User getCurrentUser() {
    UserDetailsImpl userDetails =
        (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userRepository
        .findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));
  }

  private void validateCreateRequest(
      User user, Set<ModuleEntity> moduleEntities, String justification) {
    if (moduleEntities.isEmpty() || moduleEntities.size() > 3) {
      throw new IllegalArgumentException("Selecione entre 1 e 3 módulos");
    }
    if (justification.length() < 20 || justification.length() > 500) {
      throw new IllegalArgumentException("Justificativa deve ter entre 20 e 500 caracteres");
    }
    List<String> genericTexts = Arrays.asList("teste", "aaa", "preciso");
    if (genericTexts.stream().anyMatch(justification.toLowerCase()::equals)) {
      throw new IllegalArgumentException("Justificativa insuficiente ou genérica");
    }
    moduleEntities.forEach(
        module -> {
          if (!module.isActive()) {
            throw new IllegalArgumentException("Módulo inativo: " + module.getName());
          }
          if (userActivityModuleRepository.existsByUserAndModule(user, module)) {
            throw new IllegalArgumentException("Já possui acesso ao módulo: " + module.getName());
          }
          // Verifica solicitação ativa para mesmo módulo (simplificado: checa requests ATIVAS)
          if (accessRequestRepository.findAll().stream()
              .anyMatch(
                  ar ->
                      ar.getUser().equals(user)
                          && ar.getModules().contains(module)
                          && ar.getStatus() == Status.ATIVO)) {
            throw new IllegalArgumentException(
                "Solicitação ativa existente para módulo: " + module.getName());
          }
        });
  }

  private void grantAccess(User user, Set<ModuleEntity> modules, LocalDateTime expiration) {
    modules.forEach(
        module -> {
          UserActiviteModule activiteModule = new UserActiviteModule();
          activiteModule.setUser(user);
          activiteModule.setModule(module);
          activiteModule.setExpirationDate(expiration);
          userActivityModuleRepository.save(activiteModule);
        });
  }

  private String generateProtocol() {
    LocalDate now = LocalDate.now();
    String datePart = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
    Random random = new Random();
    String numPart = String.format("%04d", random.nextInt(10000));
    return "SOL-" + datePart + "-" + numPart;
  }

  private void addHistory(AccessRequest request, String description) {
    RequestHistory history = new RequestHistory();
    history.setAccessRequest(request);
    history.setChangeDate(LocalDateTime.now());
    history.setDescription(description);
    request.getHistory().add(history);
  }

  private String applyBusinessRules(AccessRequest request) {
    User user = request.getUser();
    Set<ModuleEntity> requestedModules = request.getModules();

    for (ModuleEntity module : requestedModules) {
      if (!module.getAllowedDepartments().contains(user.getDepartment())) {
        return "Departamento sem permissão para acessar este módulo: " + module.getName();
      }
    }

    List<UserActiviteModule> activeModules = userActivityModuleRepository.findByUser(user);
    for (ModuleEntity reqModule : requestedModules) {
      for (UserActiviteModule active : activeModules) {
        if (reqModule.getIncompatibleModules().contains(active.getModule())
            || active.getModule().getIncompatibleModules().contains(reqModule)) {
          return "Módulo incompatível com outro módulo já ativo: " + reqModule.getName();
        }
      }
    }

    int currentCount = userActivityModuleRepository.countByUser(user);
    int limit = user.getDepartment() == Department.TI ? 10 : 5;
    if (currentCount + requestedModules.size() > limit) {
      return "Limite de módulos ativos atingido";
    }

    return null;
  }
}
