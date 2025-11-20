package com.example.acess_request_manager.domain.activity.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserActiveModuleId implements Serializable {
  private UUID user;
  private UUID module;
}
