package com.example.acess_request_manager.application.auth.api;

import com.example.acess_request_manager.application.auth.service.AuthService;
import com.example.acess_request_manager.application.login.dto.LoginDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import lombok.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated
@Tags(value = {@Tag(name = "Login", description = "Gerenciamento de Login")})
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @Operation(
      description = "Faz Login",
      tags = {"Login"})
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Login Feito com Sucesso")})
  @PostMapping("/login")
  public ResponseEntity<String> login(
      @Parameter(description = "Dados de Login") @Valid @RequestBody LoginDto loginDto) {
    String token = authService.login(loginDto);
    return ResponseEntity.ok(token);
  }
}
