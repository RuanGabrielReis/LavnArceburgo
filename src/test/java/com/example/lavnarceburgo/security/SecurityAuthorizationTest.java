package com.example.lavnarceburgo.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(
                        get("/api/alunos")
                )
                .andExpect(
                        status().isOk()
                );
    }
}