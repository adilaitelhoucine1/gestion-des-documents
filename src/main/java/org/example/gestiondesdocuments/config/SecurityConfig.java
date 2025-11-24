package org.example.gestiondesdocuments.config;

import org.example.gestiondesdocuments.security.JwtRequestFilter;
import org.example.gestiondesdocuments.security.JwtTokenService;
import org.example.gestiondesdocuments.security.LoginAuthenticationFilter;
import org.example.gestiondesdocuments.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final JwtTokenService jwtTokenService;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                         JwtRequestFilter jwtRequestFilter,
                         JwtTokenService jwtTokenService) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        // Create login authentication filter
        LoginAuthenticationFilter loginAuthenticationFilter =
                new LoginAuthenticationFilter("/api/auth/login", authenticationManager, jwtTokenService);

        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/comptable/**").hasRole("COMPTABLE")
                        .requestMatchers("/api/societe/**").hasRole("SOCIETE")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add login filter first to handle /api/auth/login endpoint
        http.addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Add JWT filter to handle token validation for other endpoints
        http.addFilterAfter(jwtRequestFilter, LoginAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

