package org.example.gestiondesdocuments.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.gestiondesdocuments.dto.Auth.LoginRequest;
import org.example.gestiondesdocuments.dto.Auth.LoginResponse;
import org.example.gestiondesdocuments.dto.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;

    public LoginAuthenticationFilter(String loginUrl,
                                     AuthenticationManager authenticationManager,
                                     JwtTokenService jwtTokenService) {
        super(new AntPathRequestMatcher(loginUrl, "POST"));
        setAuthenticationManager(authenticationManager);
        this.jwtTokenService = jwtTokenService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                               HttpServletResponse response)
            throws AuthenticationException, IOException {

         LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

         UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                );

         return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                           HttpServletResponse response,
                                           FilterChain chain,
                                           Authentication authResult) throws IOException {

         UserDetails userDetails = (UserDetails) authResult.getPrincipal();

         String jwt = jwtTokenService.generateToken(userDetails.getUsername());

         List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

         LoginResponse loginResponse = new LoginResponse(jwt, userDetails.getUsername(), roles);

         SecurityContextHolder.getContext().setAuthentication(authResult);

         response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), loginResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                             HttpServletResponse response,
                                             AuthenticationException failed) throws IOException {

         SecurityContextHolder.clearContext();

         ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_CREDENTIALS",
                "Email ou mot de passe incorrect"
        );

         response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}

