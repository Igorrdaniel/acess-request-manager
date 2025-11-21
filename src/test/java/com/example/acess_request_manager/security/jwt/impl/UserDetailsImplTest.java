package com.example.acess_request_manager.security.jwt.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.acess_request_manager.domain.user.model.Department;
import com.example.acess_request_manager.domain.user.model.User;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class UserDetailsImplTest {

  @Mock private User user;

  @InjectMocks private UserDetailsImpl userDetails;

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_PASSWORD = "password123";

  @Mock
  private Department department;

  @BeforeEach
  void setUp() {
    user = mock(User.class);
    department = mock(Department.class);
    when(user.getEmail()).thenReturn(TEST_EMAIL);
    when(user.getPassword()).thenReturn(TEST_PASSWORD);
    when(user.getDepartment()).thenReturn(department);
    userDetails = new UserDetailsImpl(user);
  }

  @Test
  void testConstructor() {
    assertEquals(TEST_EMAIL, userDetails.getUsername());
    assertEquals(TEST_PASSWORD, userDetails.getPassword());
    assertEquals(department, userDetails.getDepartment());
  }

  @Test
  void testGetAuthorities() {
    assertEquals(Collections.emptyList(), userDetails.getAuthorities());
  }

  @Test
  void testIsAccountNonExpired() {
    assertTrue(userDetails.isAccountNonExpired());
  }

  @Test
  void testIsAccountNonLocked() {
    assertTrue(userDetails.isAccountNonLocked());
  }

  @Test
  void testIsCredentialsNonExpired() {
    assertTrue(userDetails.isCredentialsNonExpired());
  }

  @Test
  void testIsEnabled() {
    assertTrue(userDetails.isEnabled());
  }
}
