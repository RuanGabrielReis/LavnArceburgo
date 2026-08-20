package com.example.lavnarceburgo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // LOGIN
                        .requestMatchers("/api/auth/login")
                        .permitAll()

                        // FUNCIONÁRIOS
                        .requestMatchers(HttpMethod.GET, "/api/funcionarios/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.POST, "/api/funcionarios/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.PUT, "/api/funcionarios/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.DELETE, "/api/funcionarios/**")
                        .hasRole("SECRETARIA")

                        // ALUNOS
                        .requestMatchers(HttpMethod.GET, "/api/alunos/**")
                        .hasAnyRole("SECRETARIA", "PROFESSOR")

                        .requestMatchers(HttpMethod.POST, "/api/alunos/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.PUT, "/api/alunos/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.DELETE, "/api/alunos/**")
                        .hasRole("SECRETARIA")

                        // CLASSES
                        .requestMatchers(HttpMethod.GET, "/api/classes/**")
                        .hasAnyRole("SECRETARIA", "PROFESSOR")

                        .requestMatchers(HttpMethod.POST, "/api/classes/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.PUT, "/api/classes/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.DELETE, "/api/classes/**")
                        .hasRole("SECRETARIA")

                        // HORÁRIOS
                        .requestMatchers(HttpMethod.GET, "/api/horarios/**")
                        .hasAnyRole("SECRETARIA", "PROFESSOR")

                        .requestMatchers(HttpMethod.POST, "/api/horarios/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.PUT, "/api/horarios/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.DELETE, "/api/horarios/**")
                        .hasRole("SECRETARIA")

                        // AULAS
                        .requestMatchers(HttpMethod.GET, "/api/aulas/**")
                        .hasAnyRole("SECRETARIA", "PROFESSOR")

                        .requestMatchers(HttpMethod.POST, "/api/aulas/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.PUT, "/api/aulas/**")
                        .hasRole("SECRETARIA")

                        .requestMatchers(HttpMethod.DELETE, "/api/aulas/**")
                        .hasRole("SECRETARIA")

                        // PRESENÇAS
                        .requestMatchers("/api/presencas/**")
                        .hasRole("PROFESSOR")

                        // ANOTAÇÕES
                        .requestMatchers("/api/anotacoes/**")
                        .hasRole("PROFESSOR")

                        // outras rotas exigem autenticação
                        .anyRequest()
                        .authenticated()
                );

        http.addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
        );


        return http.build();
    }
}