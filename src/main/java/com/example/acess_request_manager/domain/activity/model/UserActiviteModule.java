package com.example.acess_request_manager.domain.activity.model;

import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import com.example.acess_request_manager.domain.user.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table
@Data
@IdClass(UserActiveModuleId.class)
public class UserActiviteModule {
  @Id
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Id
  @ManyToOne
  @JoinColumn(name = "module_id")
  private ModuleEntity module;

  private LocalDateTime expirationDate;
}
