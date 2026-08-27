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
import jakarta.servlet.http.HttpServletResponse;

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

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            "application/json;charset=UTF-8"
                                    );

                                    response.getWriter().write(
                                            """
                                            {
                                              "status": 401,
                                              "erro": "Não autenticado"
                                            }
                                            """
                                    );
                                }
                        )

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );

                                    response.setContentType(
                                            "application/json;charset=UTF-8"
                                    );

                                    response.getWriter().write(
                                            """
                                            {
                                              "status": 403,
                                              "erro": "Acesso negado"
                                            }
                                            """
                                    );
                                }
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

                         // PRESENÇAS - consulta
                        .requestMatchers(HttpMethod.GET, "/api/presencas/**")
                        .hasAnyRole("SECRETARIA", "PROFESSOR")

                        // PRESENÇAS - lançamento e alteração
                        .requestMatchers(HttpMethod.POST, "/api/presencas/**")
                        .hasRole("PROFESSOR")

                        .requestMatchers(HttpMethod.PUT, "/api/presencas/**")
                        .hasRole("PROFESSOR")

                        .requestMatchers(HttpMethod.DELETE, "/api/presencas/**")
                        .hasRole("PROFESSOR")

                        // ANOTAÇÕES - consulta
                        .requestMatchers(HttpMethod.GET, "/api/anotacoes/**")
                        .hasAnyRole("SECRETARIA", "PROFESSOR")

                        // ANOTAÇÕES - professor
                        .requestMatchers(HttpMethod.POST, "/api/anotacoes/**")
                        .hasRole("PROFESSOR")

                        .requestMatchers(HttpMethod.PUT, "/api/anotacoes/**")
                        .hasRole("PROFESSOR")

                        .requestMatchers(HttpMethod.DELETE, "/api/anotacoes/**")
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