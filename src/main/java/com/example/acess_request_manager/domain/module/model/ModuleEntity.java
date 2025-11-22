package com.example.acess_request_manager.domain.module.model;

import com.example.acess_request_manager.domain.user.model.Department;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "tb_modules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModuleEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  private String description;

  private Boolean active = true;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "tb_module_allowed_departments",
      joinColumns = @JoinColumn(name = "module_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "department")
  private List<Department> allowedDepartments;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "tb_module_incompatibles",
      joinColumns = @JoinColumn(name = "module_id"),
      inverseJoinColumns = @JoinColumn(name = "incompatible_module_id"))
  @Column(name = "incompatible_module_id")
  private Set<ModuleEntity> incompatibleModules;

  public Boolean isActive() {
    return active;
  }
}
