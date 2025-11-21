package com.example.acess_request_manager.security.jwt.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.acess_request_manager.domain.user.model.User;
import com.example.acess_request_manager.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserDetailsServiceImpl userDetailsService;

  @Test
  void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
    User user = new User();
    user.setEmail("test@example.com");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

    assertNotNull(result);
    assertEquals("test@example.com", result.getUsername());
  }

  @Test
  void loadUserByUsername_WhenUserNotFound_ShouldThrowException() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername("nonexistent@example.com"));
  }
}
