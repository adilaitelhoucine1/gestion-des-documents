package org.example.gestiondesdocuments.service.imp;

import org.example.gestiondesdocuments.dto.Auth.LoginRequest;
import org.example.gestiondesdocuments.dto.Auth.LoginResponse;
import org.example.gestiondesdocuments.security.JwtTokenService;
import org.example.gestiondesdocuments.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();


        String jwt = jwtTokenService.generateToken(userDetails.getUsername());


        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        return new LoginResponse(jwt, userDetails.getUsername(), roles);
    }
}

