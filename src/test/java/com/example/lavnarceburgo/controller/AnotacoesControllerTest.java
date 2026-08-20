package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.anotacao.AnotacaoRequestDTO;
import com.example.lavnarceburgo.dto.anotacao.AnotacaoResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.enums.TipoAnotacao;
import com.example.lavnarceburgo.service.AnotacoesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import com.example.lavnarceburgo.config.JwtAuthFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnotacoesController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnotacoesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private AnotacoesService anotacoesService;

    @Test
    void deveCadastrarAnotacaoDeTurma() throws Exception {

        AnotacaoRequestDTO request = new AnotacaoRequestDTO(
                TipoAnotacao.TURMA,
                "Turma precisa revisar o conteúdo",
                1L,
                null,
                null
        );

        AnotacaoResponseDTO response = new AnotacaoResponseDTO(
                1L,
                TipoAnotacao.TURMA,
                "Turma precisa revisar o conteúdo",
                1L,
                null,
                null
        );

        when(anotacoesService.cadastrar(any(AnotacaoRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/anotacoes")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codanotacao").value(1))
                .andExpect(jsonPath("$.tipo").value("TURMA"))
                .andExpect(jsonPath("$.codclasse").value(1))
                .andExpect(jsonPath("$.codaluno").doesNotExist())
                .andExpect(jsonPath("$.codaula").doesNotExist());
    }

    @Test
    void deveListarAnotacoes() throws Exception {

        AnotacaoResponseDTO response = new AnotacaoResponseDTO(
                1L,
                TipoAnotacao.ALUNO,
                "Aluno apresentou melhora",
                null,
                2L,
                null
        );

        when(anotacoesService.listarTodas())
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/anotacoes")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codanotacao").value(1))
                .andExpect(jsonPath("$[0].tipo").value("ALUNO"))
                .andExpect(jsonPath("$[0].codaluno").value(2));
    }

    @Test
    void deveBuscarAnotacaoPorId() throws Exception {

        AnotacaoResponseDTO response = new AnotacaoResponseDTO(
                1L,
                TipoAnotacao.AULA,
                "Conteúdo revisado nesta aula",
                null,
                null,
                1L
        );

        when(anotacoesService.buscarPorId(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/anotacoes/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codanotacao").value(1))
                .andExpect(jsonPath("$.tipo").value("AULA"))
                .andExpect(jsonPath("$.codaula").value(1));
    }

    @Test
    void deveAtualizarAnotacao() throws Exception {

        AnotacaoRequestDTO request = new AnotacaoRequestDTO(
                TipoAnotacao.ALUNO,
                "Aluno apresentou melhora significativa",
                null,
                2L,
                null
        );

        AnotacaoResponseDTO response = new AnotacaoResponseDTO(
                1L,
                TipoAnotacao.ALUNO,
                "Aluno apresentou melhora significativa",
                null,
                2L,
                null
        );

        when(anotacoesService.atualizar(
                eq(1L),
                any(AnotacaoRequestDTO.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/anotacoes/1")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("ALUNO"))
                .andExpect(jsonPath("$.texto")
                        .value("Aluno apresentou melhora significativa"))
                .andExpect(jsonPath("$.codaluno").value(2));
    }

    @Test
    void deveExcluirAnotacao() throws Exception {

        mockMvc.perform(
                        delete("/api/anotacoes/1")
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404QuandoAnotacaoNaoExistir() throws Exception {

        when(anotacoesService.buscarPorId(999L))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Anotação não encontrada"
                        )
                );

        mockMvc.perform(
                        get("/api/anotacoes/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar400ParaRelacionamentoInvalido() throws Exception {

        AnotacaoRequestDTO request = new AnotacaoRequestDTO(
                TipoAnotacao.TURMA,
                "Anotação inválida",
                1L,
                2L,
                null
        );

        when(anotacoesService.cadastrar(any(AnotacaoRequestDTO.class)))
                .thenThrow(
                        new IllegalArgumentException(
                                "Anotação de turma deve estar vinculada somente a uma classe"
                        )
                );

        mockMvc.perform(
                        post("/api/anotacoes")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}