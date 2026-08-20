package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.presenca.PresencaRequestDTO;
import com.example.lavnarceburgo.dto.presenca.PresencaResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.service.PresencaService;
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

@WebMvcTest(PresencaController.class)
@AutoConfigureMockMvc(addFilters = false)
class PresencaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private PresencaService presencaService;

    @Test
    void deveCadastrarPresenca() throws Exception {

        PresencaRequestDTO request = new PresencaRequestDTO(
                2L,
                1L,
                true,
                "Aluno presente"
        );

        PresencaResponseDTO response = new PresencaResponseDTO(
                2L,
                "Aluno Teste",
                1L,
                1L,
                "INTERMEDIARIO",
                true,
                "Aluno presente"
        );

        when(presencaService.cadastrar(any(PresencaRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/presencas")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codaluno").value(2))
                .andExpect(jsonPath("$.codaula").value(1))
                .andExpect(jsonPath("$.presente").value(true))
                .andExpect(jsonPath("$.nomeAluno").value("Aluno Teste"));
    }

    @Test
    void deveListarPresencas() throws Exception {

        PresencaResponseDTO response = new PresencaResponseDTO(
                2L,
                "Aluno Teste",
                1L,
                1L,
                "INTERMEDIARIO",
                true,
                "Aluno presente"
        );

        when(presencaService.listarTodas())
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/presencas")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codaluno").value(2))
                .andExpect(jsonPath("$[0].codaula").value(1))
                .andExpect(jsonPath("$[0].presente").value(true));
    }

    @Test
    void deveBuscarPresencaPorId() throws Exception {

        PresencaResponseDTO response = new PresencaResponseDTO(
                2L,
                "Aluno Teste",
                1L,
                1L,
                "INTERMEDIARIO",
                true,
                "Aluno presente"
        );

        when(presencaService.buscarPorId(2L, 1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/presencas/2/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codaluno").value(2))
                .andExpect(jsonPath("$.codaula").value(1));
    }

    @Test
    void deveAtualizarPresenca() throws Exception {

        PresencaRequestDTO request = new PresencaRequestDTO(
                2L,
                1L,
                false,
                "Aluno faltou"
        );

        PresencaResponseDTO response = new PresencaResponseDTO(
                2L,
                "Aluno Teste",
                1L,
                1L,
                "INTERMEDIARIO",
                false,
                "Aluno faltou"
        );

        when(presencaService.atualizar(
                eq(2L),
                eq(1L),
                any(PresencaRequestDTO.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/presencas/2/1")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.presente").value(false))
                .andExpect(jsonPath("$.observacao").value("Aluno faltou"));
    }

    @Test
    void deveExcluirPresenca() throws Exception {

        mockMvc.perform(
                        delete("/api/presencas/2/1")
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404QuandoPresencaNaoExistir() throws Exception {

        when(presencaService.buscarPorId(999L, 999L))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Presença não encontrada"
                        )
                );

        mockMvc.perform(
                        get("/api/presencas/999/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar400ParaPresencaDeOutraClasse() throws Exception {

        PresencaRequestDTO request = new PresencaRequestDTO(
                2L,
                10L,
                true,
                "Teste inválido"
        );

        when(presencaService.cadastrar(any(PresencaRequestDTO.class)))
                .thenThrow(
                        new IllegalArgumentException(
                                "O aluno não pertence à mesma classe da aula"
                        )
                );

        mockMvc.perform(
                        post("/api/presencas")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}