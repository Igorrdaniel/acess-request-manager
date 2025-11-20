package com.example.acess_request_manager.domain.module.model;

import com.example.acess_request_manager.domain.user.model.Department;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_modules")
@Data
public class ModuleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  private String description;

  private Boolean active = true;

  @ElementCollection
  @CollectionTable(
      name = "module_allowed_departments",
      joinColumns = @JoinColumn(name = "module_id"))
  @Enumerated(EnumType.STRING)
  private Set<Department> allowedDepartments = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "module_incompatibles",
      joinColumns = @JoinColumn(name = "module_id"),
      inverseJoinColumns = @JoinColumn(name = "incompatible_module_id"))
  private Set<ModuleEntity> incompatibleModules = new HashSet<>();

  public Boolean isActive() {
    return active;
  }
}
