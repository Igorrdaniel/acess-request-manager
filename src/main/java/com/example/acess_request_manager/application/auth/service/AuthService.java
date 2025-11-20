package com.example.acess_request_manager.application.auth.service;

import com.example.acess_request_manager.application.login.dto.LoginDto;

public interface AuthService {

  String login(LoginDto loginDto);
}
