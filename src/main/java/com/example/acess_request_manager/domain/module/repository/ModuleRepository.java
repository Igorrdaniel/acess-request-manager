package com.example.acess_request_manager.domain.module.repository;

import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, UUID> {

  @EntityGraph(
      attributePaths = {"allowedDepartments", "incompatibleModules"},
      type = EntityGraph.EntityGraphType.LOAD)
  @Query(
      "SELECT DISTINCT m FROM ModuleEntity m LEFT JOIN FETCH m.allowedDepartments LEFT JOIN FETCH m.incompatibleModules")
  List<ModuleEntity> findAllWithDetails();
}
