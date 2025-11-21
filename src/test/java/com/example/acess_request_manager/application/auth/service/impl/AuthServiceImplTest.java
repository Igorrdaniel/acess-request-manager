package com.example.acess_request_manager.application.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.acess_request_manager.application.login.dto.LoginDto;
import com.example.acess_request_manager.domain.user.model.Department;
import com.example.acess_request_manager.domain.user.model.User;
import com.example.acess_request_manager.security.jwt.JwtConfig;
import com.example.acess_request_manager.security.jwt.impl.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock private AuthenticationManager authenticationManager;

  @Mock private JwtConfig jwtConfig;

  @InjectMocks private AuthServiceImpl authService;

    @Test
    void login_Success() {
        LoginDto dto = new LoginDto("test@user.com", "password");
        User user = new User();
        user.setEmail("test@user.com");
        user.setPassword("hashed");
        user.setDepartment(Department.TI);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(eq(new UsernamePasswordAuthenticationToken("test@user.com", "password")))).thenReturn(auth);
        when(jwtConfig.generateToken(eq(userDetails))).thenReturn("jwt.token");

        String token = authService.login(dto);

        assertEquals("jwt.token", token);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("test@user.com", captor.getValue().getPrincipal());
        assertEquals("password", captor.getValue().getCredentials());
    }

    @Test
    void login_InvalidCredentials() {
        LoginDto dto = new LoginDto("invalid@user.com", "wrong");

        when(authenticationManager.authenticate(eq(new UsernamePasswordAuthenticationToken("invalid@user.com", "wrong"))))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.login(dto));
        assertEquals("Credenciais inválidas", exception.getMessage());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("invalid@user.com", captor.getValue().getPrincipal());
    }
}
