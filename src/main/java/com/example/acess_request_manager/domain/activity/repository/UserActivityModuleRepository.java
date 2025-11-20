package com.example.acess_request_manager.domain.activity.repository;

import com.example.acess_request_manager.domain.activity.model.UserActiveModuleId;
import com.example.acess_request_manager.domain.activity.model.UserActiviteModule;
import com.example.acess_request_manager.domain.module.model.ModuleEntity;
import com.example.acess_request_manager.domain.user.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityModuleRepository
    extends JpaRepository<UserActiviteModule, UserActiveModuleId> {

  List<UserActiviteModule> findByUser(User user);

  @Query("SELECT COUNT(uam) FROM UserActiveModule uam WHERE uam.user = :user")
  int countByUser(@Param("user") User user);

  boolean existsByUserAndModule(User user, ModuleEntity moduleEntity);
}
