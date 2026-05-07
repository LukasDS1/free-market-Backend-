package com.freemarket.auth_service.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.freemarket.auth_service.jwt.JwtAuthenticationFilter;
import com.freemarket.auth_service.service.JwtService;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {


  private final AuthenticationProvider authProvider;

  @Bean
public JwtAuthenticationFilter jwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsService userDetailsService
) {
    return new JwtAuthenticationFilter(jwtService, userDetailsService);
}

@Bean
SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

    return http

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(
                        "/auth-swagger-ui",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                         "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/webjars/**"
                    ).permitAll()

                    .requestMatchers(
                            "/api-v1/auth/login",
                            "/api-v1/auth/register",
                            "/api-v1/auth/role/**"
                    ).permitAll()

                    .requestMatchers(
                            "/api-v1/auth/update/**",
                            "/api-v1/auth/refresh",
                            "/api-v1/auth/logout"
                    ).authenticated()
                    .requestMatchers(
                            "/api-v1/auth/setState/**"
                    ).hasAuthority("ADMIN")

                    .anyRequest().authenticated()
            )

            .sessionManagement(sessionManager ->
                    sessionManager.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .authenticationProvider(authProvider)

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            )

            .build();
}


@Bean
  PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }   

}
