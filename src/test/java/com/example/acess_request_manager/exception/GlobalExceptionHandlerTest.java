package com.example.acess_request_manager.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void handleIllegalArgument_ShouldReturnBadRequest() {
    String message = "Invalid argument";
    IllegalArgumentException ex = new IllegalArgumentException(message);
    ResponseEntity<String> response = handler.handleIllegalArgument(ex);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(message, response.getBody());
  }

  @Test
  void handleBadCredentials_ShouldReturnUnauthorized() {
    String message = "Bad credentials";
    BadCredentialsException ex = new BadCredentialsException(message);
    ResponseEntity<String> response = handler.handleBadCredentials(ex);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(message, response.getBody());
  }

  @Test
  void handleGeneralException_ShouldReturnInternalServerError() {
    String message = "Test error";
    Exception ex = new Exception(message);
    ResponseEntity<String> response = handler.handleGeneralException(ex);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Erro interno: " + message, response.getBody());
  }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        String message = "validation error";

        when(ex.getMessage()).thenReturn(message);

        ResponseEntity<String> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(message, response.getBody());
    }
}
