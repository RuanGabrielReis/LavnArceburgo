package com.example.lavnarceburgo.security;

import org.springframework.http.MediaType;
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

    @Test
    @WithMockUser(roles = "SECRETARIA")
    void secretariaDevePoderConsultarFuncionarios() throws Exception {

        mockMvc.perform(
                        get("/api/funcionarios")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESSOR")
    void professorNaoDevePoderConsultarFuncionarios() throws Exception {

        mockMvc.perform(
                        get("/api/funcionarios")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SECRETARIA")
    void secretariaNaoDevePoderCadastrarFuncionario() throws Exception {

        mockMvc.perform(
                        post("/api/funcionarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MASTER")
    void masterDevePoderConsultarFuncionarios() throws Exception {

        mockMvc.perform(
                        get("/api/funcionarios")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MASTER")
    void masterDevePoderAcessarCadastroDePresenca() throws Exception {

        mockMvc.perform(
                        post("/api/presencas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MASTER")
    void masterDevePoderAcessarCadastroDeAnotacao() throws Exception {

        mockMvc.perform(
                        post("/api/anotacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SECRETARIA")
    void secretariaNaoDeveCadastrarPresenca() throws Exception {

        mockMvc.perform(
                        post("/api/presencas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SECRETARIA")
    void secretariaNaoDeveCadastrarAnotacao() throws Exception {

        mockMvc.perform(
                        post("/api/anotacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isForbidden());
    }
}