package com.example.acess_request_manager.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.acess_request_manager.security.jwt.impl.UserDetailsImpl;
import com.example.acess_request_manager.security.jwt.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

  @Mock private JwtConfig jwtConfig;

  @Mock private UserDetailsServiceImpl userDetailsService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @InjectMocks private JwtFilter jwtFilter;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldAuthenticateWhenValidToken() throws ServletException, IOException {
    String token = "valid_token";
    String username = "test_user";
    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtConfig.extractUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtConfig.isTokenValid(token, userDetails)).thenReturn(true);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldNotAuthenticateWhenInvalidToken() throws ServletException, IOException {
    String token = "invalid_token";
    String username = "test_user";
    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtConfig.extractUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtConfig.isTokenValid(token, userDetails)).thenReturn(false);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldContinueFilterChainWhenNoAuthorizationHeader() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
