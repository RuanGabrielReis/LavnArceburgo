package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.config.JwtAuthFilter;
import com.example.lavnarceburgo.dto.aula.AulaRequestDTO;
import com.example.lavnarceburgo.dto.aula.AulaResponseDTO;
import com.example.lavnarceburgo.service.AulaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AulaController.class)
@AutoConfigureMockMvc(addFilters = false)
class AulaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AulaService aulaService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void deveCadastrarAula() throws Exception {

        LocalDateTime diaHora =
                LocalDateTime.of(
                        2026,
                        8,
                        29,
                        19,
                        0
                );

        AulaRequestDTO dto =
                new AulaRequestDTO(
                        1L,
                        diaHora
                );

        AulaResponseDTO resposta =
                new AulaResponseDTO(
                        1L,
                        1L,
                        "INTERMEDIARIO",
                        diaHora
                );

        when(aulaService.cadastrar(dto))
                .thenReturn(resposta);

        mockMvc.perform(
                        post("/api/aulas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codaula").value(1L))
                .andExpect(jsonPath("$.codclasse").value(1L))
                .andExpect(jsonPath("$.nivel")
                        .value("INTERMEDIARIO"));

        verify(aulaService).cadastrar(dto);
    }

    @Test
    void deveListarAulas() throws Exception {

        LocalDateTime diaHora =
                LocalDateTime.of(
                        2026,
                        8,
                        29,
                        19,
                        0
                );

        AulaResponseDTO aula =
                new AulaResponseDTO(
                        1L,
                        1L,
                        "INTERMEDIARIO",
                        diaHora
                );

        when(aulaService.listarTodos())
                .thenReturn(List.of(aula));

        mockMvc.perform(
                        get("/api/aulas")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codaula").value(1L))
                .andExpect(jsonPath("$[0].codclasse").value(1L))
                .andExpect(jsonPath("$[0].nivel")
                        .value("INTERMEDIARIO"));

        verify(aulaService).listarTodos();
    }

    @Test
    void deveBuscarAulaPorId() throws Exception {

        LocalDateTime diaHora =
                LocalDateTime.of(
                        2026,
                        8,
                        29,
                        19,
                        0
                );

        AulaResponseDTO aula =
                new AulaResponseDTO(
                        1L,
                        1L,
                        "INTERMEDIARIO",
                        diaHora
                );

        when(aulaService.buscarPorId(1L))
                .thenReturn(aula);

        mockMvc.perform(
                        get("/api/aulas/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codaula").value(1L))
                .andExpect(jsonPath("$.codclasse").value(1L));

        verify(aulaService).buscarPorId(1L);
    }

    @Test
    void deveAtualizarAula() throws Exception {

        LocalDateTime novaDataHora =
                LocalDateTime.of(
                        2026,
                        8,
                        30,
                        20,
                        0
                );

        AulaRequestDTO dto =
                new AulaRequestDTO(
                        1L,
                        novaDataHora
                );

        AulaResponseDTO resposta =
                new AulaResponseDTO(
                        1L,
                        1L,
                        "INTERMEDIARIO",
                        novaDataHora
                );

        when(aulaService.atualizar(1L, dto))
                .thenReturn(resposta);

        mockMvc.perform(
                        put("/api/aulas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codaula").value(1L))
                .andExpect(jsonPath("$.codclasse").value(1L));

        verify(aulaService)
                .atualizar(1L, dto);
    }

    @Test
    void deveExcluirAula() throws Exception {

        mockMvc.perform(
                        delete("/api/aulas/1")
                )
                .andExpect(status().isNoContent());

        verify(aulaService).excluir(1L);
    }

    @Test
    void deveRetornarBadRequestAoCadastrarAulaInvalida()
            throws Exception {

        mockMvc.perform(
                        post("/api/aulas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(aulaService, never())
                .cadastrar(any());
    }

    @Test
    void deveRetornarBadRequestAoAtualizarAulaInvalida()
            throws Exception {

        mockMvc.perform(
                        put("/api/aulas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(aulaService, never())
                .atualizar(anyLong(), any());
    }
}