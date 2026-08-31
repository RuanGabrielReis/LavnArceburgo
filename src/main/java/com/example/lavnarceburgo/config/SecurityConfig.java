package com.example.lavnarceburgo.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;


    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }


    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // =====================================================
    // CONFIGURAÇÃO DE SEGURANÇA
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf ->
                        csrf.disable()
                )


                // =================================================
                // SESSÃO
                // =================================================
                //
                // Como usamos JWT, o servidor não guarda sessão.
                //
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =================================================
                // TRATAMENTO DE ERROS DE SEGURANÇA
                // =================================================

                .exceptionHandling(exception -> exception


                        // -----------------------------------------
                        // 401 - usuário não autenticado
                        // -----------------------------------------

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


                        // -----------------------------------------
                        // 403 - usuário autenticado,
                        // mas sem permissão
                        // -----------------------------------------

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


                // =================================================
                // AUTORIZAÇÃO DAS ROTAS
                // =================================================

                .authorizeHttpRequests(auth -> auth


                        // =================================================
                        // LOGIN
                        // =================================================

                        .requestMatchers(
                                "/api/auth/login"
                        )
                        .permitAll()


                        // =================================================
                        // FUNCIONÁRIOS
                        // =================================================


                        // -----------------------------------------
                        // Consulta
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/funcionarios/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )


                        // -----------------------------------------
                        // Cadastro
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/funcionarios/**"
                        )
                        .hasAnyRole(
                                "MASTER"
                        )


                        // -----------------------------------------
                        // ALTERAR A PRÓPRIA SENHA
                        // -----------------------------------------
                        //
                        // IMPORTANTE:
                        //
                        // Essa regra precisa vir ANTES da regra:
                        //
                        // /api/funcionarios/**
                        //
                        // porque "me/senha" também combina com /**.
                        //

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/funcionarios/me/senha"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA",
                                "PROFESSOR"
                        )


                        // -----------------------------------------
                        // Outras alterações de funcionário
                        // -----------------------------------------
                        //
                        // Continuam exclusivas do MASTER.
                        //

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/funcionarios/**"
                        )
                        .hasAnyRole(
                                "MASTER"
                        )


                        // -----------------------------------------
                        // Exclusão
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/funcionarios/**"
                        )
                        .hasAnyRole(
                                "MASTER"
                        )


                        // =================================================
                        // ALUNOS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/alunos/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA",
                                "PROFESSOR"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/alunos/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/alunos/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/alunos/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )


                        // =================================================
                        // CLASSES
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/classes/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA",
                                "PROFESSOR"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/classes/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/classes/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/classes/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )


                        // =================================================
                        // HORÁRIOS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/horarios/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA",
                                "PROFESSOR"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/horarios/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/horarios/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/horarios/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )


                        // =================================================
                        // AULAS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/aulas/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA",
                                "PROFESSOR"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/aulas/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/aulas/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/aulas/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA"
                        )


                        // =================================================
                        // PRESENÇAS
                        // =================================================


                        // Consulta

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/presencas/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA",
                                "PROFESSOR"
                        )


                        // Lançamento

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/presencas/**"
                        )
                        .hasAnyRole(
                                "PROFESSOR",
                                "MASTER"
                        )


                        // Alteração

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/presencas/**"
                        )
                        .hasAnyRole(
                                "PROFESSOR",
                                "MASTER"
                        )


                        // Exclusão

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/presencas/**"
                        )
                        .hasAnyRole(
                                "PROFESSOR",
                                "MASTER"
                        )


                        // =================================================
                        // ANOTAÇÕES
                        // =================================================


                        // Consulta

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/anotacoes/**"
                        )
                        .hasAnyRole(
                                "MASTER",
                                "SECRETARIA",
                                "PROFESSOR"
                        )


                        // Cadastro

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/anotacoes/**"
                        )
                        .hasAnyRole(
                                "PROFESSOR",
                                "MASTER"
                        )


                        // Alteração

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/anotacoes/**"
                        )
                        .hasAnyRole(
                                "PROFESSOR",
                                "MASTER"
                        )


                        // Exclusão

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/anotacoes/**"
                        )
                        .hasAnyRole(
                                "PROFESSOR",
                                "MASTER"
                        )


                        // =================================================
                        // OUTRAS ROTAS
                        // =================================================
                        //
                        // Qualquer rota que não apareceu acima
                        // precisa pelo menos de autenticação.
                        //

                        .anyRequest()
                        .authenticated()
                );


        // =====================================================
        // FILTRO JWT
        // =====================================================
        //
        // Executa nosso JwtAuthFilter antes do filtro padrão
        // de usuário/senha do Spring Security.
        //

        http.addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
        );


        return http.build();
    }
}