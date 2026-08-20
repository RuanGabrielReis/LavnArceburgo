package com.example.lavnarceburgo.security;

import com.example.lavnarceburgo.controller.AlunoController;
import com.example.lavnarceburgo.service.AlunoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlunoController.class)
@Import(SecurityAuthorizationTest.TestSecurityConfig.class)
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlunoService alunoService;

    @Test
    void deveRetornar401SemAutenticacao() throws Exception {

        mockMvc.perform(
                        get("/api/alunos")
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    @WithMockUser(
            username = "professor@lavn.com",
            roles = "PROFESSOR"
    )
    void professorDeveConsultarAlunos() throws Exception {

        when(alunoService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/alunos")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    @WithMockUser(
            username = "professor@lavn.com",
            roles = "PROFESSOR"
    )
    void professorNaoDeveCadastrarAluno() throws Exception {

        mockMvc.perform(
                        post("/api/alunos")
                                .contentType("application/json")
                                .content("""
                                {
                                  "nome": "Aluno Teste",
                                  "cpf": "555.555.555-55",
                                  "email": "aluno@teste.com",
                                  "codclasse": 1
                                }
                                """)
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    @WithMockUser(
            username = "secretaria@lavn.com",
            roles = "SECRETARIA"
    )
    void secretariaPodeConsultarAlunos() throws Exception {

        when(alunoService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/alunos")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(
                HttpSecurity http
        ) throws Exception {

            http
                    .csrf(csrf -> csrf.disable())

                    .exceptionHandling(exception ->
                            exception.authenticationEntryPoint(
                                    (request, response, authException) ->
                                            response.setStatus(401)
                            )
                    )

                    .authorizeHttpRequests(auth -> auth

                            .requestMatchers(
                                    HttpMethod.GET,
                                    "/api/alunos/**"
                            )
                            .hasAnyRole(
                                    "SECRETARIA",
                                    "PROFESSOR"
                            )

                            .requestMatchers(
                                    HttpMethod.POST,
                                    "/api/alunos/**"
                            )
                            .hasRole("SECRETARIA")

                            .requestMatchers(
                                    HttpMethod.PUT,
                                    "/api/alunos/**"
                            )
                            .hasRole("SECRETARIA")

                            .requestMatchers(
                                    HttpMethod.DELETE,
                                    "/api/alunos/**"
                            )
                            .hasRole("SECRETARIA")

                            .anyRequest()
                            .authenticated()
                    );

            return http.build();
        }
    }
}