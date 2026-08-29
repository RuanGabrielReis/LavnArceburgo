package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.config.JwtAuthFilter;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioRequestDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioResponseDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioSenhaDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioUpdateDTO;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.service.FuncionarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FuncionarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FuncionarioService funcionarioService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void deveCadastrarFuncionario() throws Exception {

        FuncionarioRequestDTO dto =
                new FuncionarioRequestDTO(
                        "Professor Teste",
                        "123.456.789-00",
                        "35999999999",
                        "MG123456",
                        "Rua Teste, 100",
                        "Arceburgo",
                        "professor@teste.com",
                        Cargo.PROFESSOR,
                        "senha123"
                );

        FuncionarioResponseDTO resposta =
                new FuncionarioResponseDTO(
                        1L,
                        "Professor Teste",
                        "123.456.789-00",
                        "35999999999",
                        "MG123456",
                        "Rua Teste, 100",
                        "Arceburgo",
                        "professor@teste.com",
                        Cargo.PROFESSOR
                );

        when(funcionarioService.cadastrar(dto))
                .thenReturn(resposta);

        mockMvc.perform(
                        post("/api/funcionarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codfuncionario").value(1L))
                .andExpect(jsonPath("$.nome").value("Professor Teste"))
                .andExpect(jsonPath("$.email").value("professor@teste.com"))
                .andExpect(jsonPath("$.cargo").value("PROFESSOR"));

        verify(funcionarioService).cadastrar(dto);
    }

    @Test
    void deveListarFuncionarios() throws Exception {

        FuncionarioResponseDTO funcionario =
                new FuncionarioResponseDTO(
                        1L,
                        "Professor Teste",
                        "123.456.789-00",
                        "35999999999",
                        "MG123456",
                        "Rua Teste, 100",
                        "Arceburgo",
                        "professor@teste.com",
                        Cargo.PROFESSOR
                );

        when(funcionarioService.listarTodos())
                .thenReturn(List.of(funcionario));

        mockMvc.perform(
                        get("/api/funcionarios")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codfuncionario").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Professor Teste"))
                .andExpect(jsonPath("$[0].cargo").value("PROFESSOR"));

        verify(funcionarioService).listarTodos();
    }

    @Test
    void deveBuscarFuncionarioPorId() throws Exception {

        FuncionarioResponseDTO funcionario =
                new FuncionarioResponseDTO(
                        1L,
                        "Professor Teste",
                        "123.456.789-00",
                        "35999999999",
                        "MG123456",
                        "Rua Teste, 100",
                        "Arceburgo",
                        "professor@teste.com",
                        Cargo.PROFESSOR
                );

        when(funcionarioService.buscarPorId(1L))
                .thenReturn(funcionario);

        mockMvc.perform(
                        get("/api/funcionarios/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codfuncionario").value(1L))
                .andExpect(jsonPath("$.nome").value("Professor Teste"));

        verify(funcionarioService).buscarPorId(1L);
    }

    @Test
    void deveAtualizarFuncionarioSemAlterarSenha() throws Exception {

        FuncionarioUpdateDTO dto =
                new FuncionarioUpdateDTO(
                        "Professor Atualizado",
                        "123.456.789-00",
                        "35988888888",
                        "MG123456",
                        "Rua Atualizada, 200",
                        "Arceburgo",
                        "atualizado@teste.com",
                        Cargo.PROFESSOR
                );

        FuncionarioResponseDTO resposta =
                new FuncionarioResponseDTO(
                        1L,
                        "Professor Atualizado",
                        "123.456.789-00",
                        "35988888888",
                        "MG123456",
                        "Rua Atualizada, 200",
                        "Arceburgo",
                        "atualizado@teste.com",
                        Cargo.PROFESSOR
                );

        when(funcionarioService.atualizar(1L, dto))
                .thenReturn(resposta);

        mockMvc.perform(
                        put("/api/funcionarios/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Professor Atualizado"))
                .andExpect(jsonPath("$.email").value("atualizado@teste.com"));

        verify(funcionarioService)
                .atualizar(1L, dto);
    }

    @Test
    void deveAlterarSenhaDoFuncionario() throws Exception {

        FuncionarioSenhaDTO dto =
                new FuncionarioSenhaDTO(
                        "novaSenha123"
                );

        mockMvc.perform(
                        put("/api/funcionarios/1/senha")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isNoContent());

        verify(funcionarioService)
                .alterarSenha(1L, dto);
    }

    @Test
    void deveExcluirFuncionario() throws Exception {

        mockMvc.perform(
                        delete("/api/funcionarios/1")
                )
                .andExpect(status().isNoContent());

        verify(funcionarioService)
                .excluir(1L);
    }

    @Test
    void deveRetornarBadRequestAoCadastrarFuncionarioInvalido()
            throws Exception {

        mockMvc.perform(
                        post("/api/funcionarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(funcionarioService, never())
                .cadastrar(any());
    }

    @Test
    void deveRetornarBadRequestAoAlterarSenhaInvalida()
            throws Exception {

        FuncionarioSenhaDTO dto =
                new FuncionarioSenhaDTO("123");

        mockMvc.perform(
                        put("/api/funcionarios/1/senha")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(funcionarioService, never())
                .alterarSenha(anyLong(), any());
    }
}