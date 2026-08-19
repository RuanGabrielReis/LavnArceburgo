package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.aluno.AlunoRequestDTO;
import com.example.lavnarceburgo.dto.aluno.AlunoResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.service.AlunoService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlunoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlunoService alunoService;

    @Test
    void deveCadastrarAluno() throws Exception {

        AlunoRequestDTO request = new AlunoRequestDTO(
                "Aluno Teste",
                "123.456.789-00",
                "35999999999",
                "MG123456",
                "Rua Teste, 100",
                "Arceburgo",
                "aluno@teste.com",
                1L
        );

        AlunoResponseDTO response = new AlunoResponseDTO(
                2L,
                "Aluno Teste",
                "123.456.789-00",
                "35999999999",
                "MG123456",
                "Rua Teste, 100",
                "Arceburgo",
                "aluno@teste.com",
                1L,
                "INTERMEDIARIO"
        );

        when(alunoService.cadastrar(any(AlunoRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/alunos")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codaluno").value(2))
                .andExpect(jsonPath("$.nome").value("Aluno Teste"))
                .andExpect(jsonPath("$.codclasse").value(1))
                .andExpect(jsonPath("$.nivel").value("INTERMEDIARIO"));
    }

    @Test
    void deveListarAlunos() throws Exception {

        AlunoResponseDTO aluno = new AlunoResponseDTO(
                2L,
                "Aluno Teste",
                "123.456.789-00",
                "35999999999",
                "MG123456",
                "Rua Teste, 100",
                "Arceburgo",
                "aluno@teste.com",
                1L,
                "INTERMEDIARIO"
        );

        when(alunoService.listarTodos())
                .thenReturn(List.of(aluno));

        mockMvc.perform(
                        get("/api/alunos")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codaluno").value(2))
                .andExpect(jsonPath("$[0].nome").value("Aluno Teste"));
    }

    @Test
    void deveBuscarAlunoPorId() throws Exception {

        AlunoResponseDTO aluno = new AlunoResponseDTO(
                2L,
                "Aluno Teste",
                "123.456.789-00",
                "35999999999",
                "MG123456",
                "Rua Teste, 100",
                "Arceburgo",
                "aluno@teste.com",
                1L,
                "INTERMEDIARIO"
        );

        when(alunoService.buscarPorId(2L))
                .thenReturn(aluno);

        mockMvc.perform(
                        get("/api/alunos/2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codaluno").value(2))
                .andExpect(jsonPath("$.nome").value("Aluno Teste"));
    }

    @Test
    void deveAtualizarAluno() throws Exception {

        AlunoRequestDTO request = new AlunoRequestDTO(
                "Aluno Atualizado",
                "123.456.789-00",
                "35988888888",
                "MG123456",
                "Rua Nova, 200",
                "Arceburgo",
                "aluno@teste.com",
                1L
        );

        AlunoResponseDTO response = new AlunoResponseDTO(
                2L,
                "Aluno Atualizado",
                "123.456.789-00",
                "35988888888",
                "MG123456",
                "Rua Nova, 200",
                "Arceburgo",
                "aluno@teste.com",
                1L,
                "INTERMEDIARIO"
        );

        when(alunoService.atualizar(
                eq(2L),
                any(AlunoRequestDTO.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/alunos/2")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Aluno Atualizado"))
                .andExpect(jsonPath("$.telefone").value("35988888888"));
    }

    @Test
    void deveExcluirAluno() throws Exception {

        mockMvc.perform(
                        delete("/api/alunos/2")
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar400QuandoDadosForemInvalidos() throws Exception {

        AlunoRequestDTO request = new AlunoRequestDTO(
                "",
                "",
                "35999999999",
                "MG123456",
                "Rua Teste, 100",
                "Arceburgo",
                "email-invalido",
                null
        );

        mockMvc.perform(
                        post("/api/alunos")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Erro de validação"))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.cpf").exists())
                .andExpect(jsonPath("$.campos.email").exists())
                .andExpect(jsonPath("$.campos.codclasse").exists());
    }

    @Test
    void deveRetornar404QuandoAlunoNaoExistir() throws Exception {

        when(alunoService.buscarPorId(999L))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Aluno não encontrado"
                        )
                );

        mockMvc.perform(
                        get("/api/alunos/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro")
                        .value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Aluno não encontrado"));
    }
}