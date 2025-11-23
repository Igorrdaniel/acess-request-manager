package com.example.acess_request_manager.domain.access.repository;

import com.example.acess_request_manager.domain.access.model.AccessRequest;
import com.example.acess_request_manager.domain.access.model.Status;
import com.example.acess_request_manager.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, UUID> {

  @Query(
      "SELECT ar FROM AccessRequest ar WHERE ar.user = :user "
          + "AND (:protocol IS NULL OR ar.protocol LIKE %:protocol%) "
          + "AND (:status IS NULL OR ar.status = :status) "
          + "AND (cast(:startDate as timestamp) IS NULL OR ar.requestDate >= :startDate) "
          + "AND (cast(:endDate as timestamp) IS NULL OR ar.requestDate <= :endDate) "
          + "AND (:urgent IS NULL OR ar.urgent = :urgent)")
  Page<AccessRequest> findByUserWithFilter(
      @Param("user") User user,
      @Param("protocol") String protocol,
      @Param("status") Status status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("urgent") Boolean urgent,
      Pageable pageable);
}
