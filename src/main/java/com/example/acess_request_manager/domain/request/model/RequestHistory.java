package com.example.acess_request_manager.domain.request.model;

import com.example.acess_request_manager.domain.access.model.AccessRequest;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Entity
@Table(name = "tb_request_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "access_request_id")
  private AccessRequest accessRequest;

  @Column(nullable = false)
  private LocalDateTime changeDate;

  @Column(nullable = false)
  private String description;
}
