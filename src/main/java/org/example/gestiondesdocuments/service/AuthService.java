package org.example.gestiondesdocuments.service;

import org.example.gestiondesdocuments.dto.Auth.LoginRequest;
import org.example.gestiondesdocuments.dto.Auth.LoginResponse;

public interface AuthService {

    LoginResponse authenticate(LoginRequest loginRequest);
}
