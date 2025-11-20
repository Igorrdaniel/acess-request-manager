package com.example.acess_request_manager.domain.module.repository;

import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, UUID> {}
