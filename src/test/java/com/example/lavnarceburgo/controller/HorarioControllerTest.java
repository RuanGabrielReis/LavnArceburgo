package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.config.JwtAuthFilter;
import com.example.lavnarceburgo.dto.horario.HorarioRequestDTO;
import com.example.lavnarceburgo.dto.horario.HorarioResponseDTO;
import com.example.lavnarceburgo.service.HorarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HorarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class HorarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HorarioService horarioService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void deveCadastrarHorario() throws Exception {

        HorarioRequestDTO dto =
                new HorarioRequestDTO(
                        60,
                        1L,
                        "Sala 1",
                        DayOfWeek.MONDAY,
                        LocalTime.of(19, 0)
                );

        HorarioResponseDTO resposta =
                new HorarioResponseDTO(
                        1L,
                        60,
                        1L,
                        "INTERMEDIARIO",
                        "Sala 1",
                        DayOfWeek.MONDAY,
                        LocalTime.of(19, 0)
                );

        when(horarioService.cadastrar(dto))
                .thenReturn(resposta);

        mockMvc.perform(
                        post("/api/horarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codhorario").value(1L))
                .andExpect(jsonPath("$.duracaoaula").value(60))
                .andExpect(jsonPath("$.codclasse").value(1L))
                .andExpect(jsonPath("$.nivel").value("INTERMEDIARIO"))
                .andExpect(jsonPath("$.sala").value("Sala 1"))
                .andExpect(jsonPath("$.diaSemana").value("MONDAY"))
                .andExpect(jsonPath("$.horaInicio").value("19:00:00"));

        verify(horarioService).cadastrar(dto);
    }

    @Test
    void deveListarHorarios() throws Exception {

        HorarioResponseDTO horario =
                new HorarioResponseDTO(
                        1L,
                        60,
                        1L,
                        "INTERMEDIARIO",
                        "Sala 1",
                        DayOfWeek.MONDAY,
                        LocalTime.of(19, 0)
                );

        when(horarioService.listarTodos())
                .thenReturn(List.of(horario));

        mockMvc.perform(
                        get("/api/horarios")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codhorario").value(1L))
                .andExpect(jsonPath("$[0].duracaoaula").value(60))
                .andExpect(jsonPath("$[0].sala").value("Sala 1"))
                .andExpect(jsonPath("$[0].diaSemana").value("MONDAY"));

        verify(horarioService).listarTodos();
    }

    @Test
    void deveBuscarHorarioPorId() throws Exception {

        HorarioResponseDTO horario =
                new HorarioResponseDTO(
                        1L,
                        60,
                        1L,
                        "INTERMEDIARIO",
                        "Sala 1",
                        DayOfWeek.MONDAY,
                        LocalTime.of(19, 0)
                );

        when(horarioService.buscarPorId(1L))
                .thenReturn(horario);

        mockMvc.perform(
                        get("/api/horarios/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codhorario").value(1L))
                .andExpect(jsonPath("$.codclasse").value(1L))
                .andExpect(jsonPath("$.sala").value("Sala 1"));

        verify(horarioService).buscarPorId(1L);
    }

    @Test
    void deveAtualizarHorario() throws Exception {

        HorarioRequestDTO dto =
                new HorarioRequestDTO(
                        90,
                        1L,
                        "Sala 2",
                        DayOfWeek.TUESDAY,
                        LocalTime.of(20, 0)
                );

        HorarioResponseDTO resposta =
                new HorarioResponseDTO(
                        1L,
                        90,
                        1L,
                        "INTERMEDIARIO",
                        "Sala 2",
                        DayOfWeek.TUESDAY,
                        LocalTime.of(20, 0)
                );

        when(horarioService.atualizar(1L, dto))
                .thenReturn(resposta);

        mockMvc.perform(
                        put("/api/horarios/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codhorario").value(1L))
                .andExpect(jsonPath("$.duracaoaula").value(90))
                .andExpect(jsonPath("$.sala").value("Sala 2"))
                .andExpect(jsonPath("$.diaSemana").value("TUESDAY"));

        verify(horarioService)
                .atualizar(1L, dto);
    }

    @Test
    void deveExcluirHorario() throws Exception {

        mockMvc.perform(
                        delete("/api/horarios/1")
                )
                .andExpect(status().isNoContent());

        verify(horarioService).excluir(1L);
    }

    @Test
    void deveRetornarBadRequestAoCadastrarHorarioInvalido()
            throws Exception {

        mockMvc.perform(
                        post("/api/horarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(horarioService, never())
                .cadastrar(any());
    }

    @Test
    void deveRetornarBadRequestAoCadastrarHorarioComDuracaoZero()
            throws Exception {

        HorarioRequestDTO dto =
                new HorarioRequestDTO(
                        0,
                        1L,
                        "Sala 1",
                        DayOfWeek.MONDAY,
                        LocalTime.of(19, 0)
                );

        mockMvc.perform(
                        post("/api/horarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(horarioService, never())
                .cadastrar(any());
    }

    @Test
    void deveRetornarBadRequestAoCadastrarHorarioSemSala()
            throws Exception {

        HorarioRequestDTO dto =
                new HorarioRequestDTO(
                        60,
                        1L,
                        "",
                        DayOfWeek.MONDAY,
                        LocalTime.of(19, 0)
                );

        mockMvc.perform(
                        post("/api/horarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(horarioService, never())
                .cadastrar(any());
    }
}