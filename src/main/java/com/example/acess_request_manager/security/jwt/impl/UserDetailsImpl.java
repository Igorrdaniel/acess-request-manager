package com.example.acess_request_manager.security.jwt.impl;

import com.example.acess_request_manager.domain.user.model.Department;
import com.example.acess_request_manager.domain.user.model.User;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {

  private final String email;
  private final String password;
  private final Department department;

  public UserDetailsImpl(User user) {
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.department = user.getDepartment();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }

  public Department getDepartment() {
    return department;
  }
}
