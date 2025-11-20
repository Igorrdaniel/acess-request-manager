package com.example.acess_request_manager.domain.access.model;

import com.example.acess_request_manager.domain.module.model.Module;
import com.example.acess_request_manager.domain.request.model.RequestHistory;
import com.example.acess_request_manager.domain.user.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Entity
@Table(name = "access_requests")
@Data
public class AccessRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String protocol;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToMany
  @JoinTable(
      name = "access_request_modules",
      joinColumns = @JoinColumn(name = "access_request_id"),
      inverseJoinColumns = @JoinColumn(name = "module_id"))
  private Set<Module> modules = new HashSet<>();

  @Column(nullable = false)
  private String justification;

  private boolean urgent;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(nullable = false)
  private LocalDateTime requestDate;

  private LocalDateTime expirationDate;

  private String denialReason;

  @OneToMany(mappedBy = "accessRequest", cascade = CascadeType.ALL)
  private List<RequestHistory> history = new ArrayList<>();
}
