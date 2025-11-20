package com.example.acess_request_manager.application.auth.service.impl;

import com.example.acess_request_manager.application.auth.service.AuthService;
import com.example.acess_request_manager.application.login.dto.LoginDto;
import com.example.acess_request_manager.security.jwt.JwtConfig;
import com.example.acess_request_manager.security.jwt.impl.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtConfig jwtConfig;

  public AuthServiceImpl(AuthenticationManager authenticationManager, JwtConfig jwtConfig) {
    this.authenticationManager = authenticationManager;
    this.jwtConfig = jwtConfig;
  }

  @Override
  public String login(LoginDto loginDto) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      return jwtConfig.generateToken(userDetails);
    } catch (BadCredentialsException e) {
      throw new BadCredentialsException("Credenciais inválidas");
    }
  }
}
