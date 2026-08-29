package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.config.JwtAuthFilter;
import com.example.lavnarceburgo.dto.classe.ClasseRequestDTO;
import com.example.lavnarceburgo.dto.classe.ClasseResponseDTO;
import com.example.lavnarceburgo.service.ClasseService;
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

@WebMvcTest(ClasseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClasseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClasseService classeService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void deveCadastrarClasse() throws Exception {

        ClasseRequestDTO dto =
                new ClasseRequestDTO(
                        "INTERMEDIARIO",
                        1L
                );

        ClasseResponseDTO resposta =
                new ClasseResponseDTO(
                        1L,
                        "INTERMEDIARIO",
                        1L,
                        "Professor Teste"
                );

        when(classeService.cadastrar(dto))
                .thenReturn(resposta);

        mockMvc.perform(
                        post("/api/classes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codclasse").value(1L))
                .andExpect(jsonPath("$.nivel").value("INTERMEDIARIO"))
                .andExpect(jsonPath("$.codprofessor").value(1L))
                .andExpect(jsonPath("$.nomeProfessor")
                        .value("Professor Teste"));

        verify(classeService).cadastrar(dto);
    }

    @Test
    void deveListarClasses() throws Exception {

        ClasseResponseDTO classe =
                new ClasseResponseDTO(
                        1L,
                        "INTERMEDIARIO",
                        1L,
                        "Professor Teste"
                );

        when(classeService.listarTodas())
                .thenReturn(List.of(classe));

        mockMvc.perform(
                        get("/api/classes")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codclasse").value(1L))
                .andExpect(jsonPath("$[0].nivel")
                        .value("INTERMEDIARIO"))
                .andExpect(jsonPath("$[0].nomeProfessor")
                        .value("Professor Teste"));

        verify(classeService).listarTodas();
    }

    @Test
    void deveBuscarClassePorId() throws Exception {

        ClasseResponseDTO classe =
                new ClasseResponseDTO(
                        1L,
                        "INTERMEDIARIO",
                        1L,
                        "Professor Teste"
                );

        when(classeService.buscarPorId(1L))
                .thenReturn(classe);

        mockMvc.perform(
                        get("/api/classes/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codclasse").value(1L))
                .andExpect(jsonPath("$.nivel")
                        .value("INTERMEDIARIO"));

        verify(classeService).buscarPorId(1L);
    }

    @Test
    void deveAtualizarClasse() throws Exception {

        ClasseRequestDTO dto =
                new ClasseRequestDTO(
                        "AVANCADO",
                        1L
                );

        ClasseResponseDTO resposta =
                new ClasseResponseDTO(
                        1L,
                        "AVANCADO",
                        1L,
                        "Professor Teste"
                );

        when(classeService.atualizar(1L, dto))
                .thenReturn(resposta);

        mockMvc.perform(
                        put("/api/classes/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codclasse").value(1L))
                .andExpect(jsonPath("$.nivel").value("AVANCADO"));

        verify(classeService)
                .atualizar(1L, dto);
    }

    @Test
    void deveExcluirClasse() throws Exception {

        mockMvc.perform(
                        delete("/api/classes/1")
                )
                .andExpect(status().isNoContent());

        verify(classeService).excluir(1L);
    }

    @Test
    void deveRetornarBadRequestAoCadastrarClasseInvalida()
            throws Exception {

        mockMvc.perform(
                        post("/api/classes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(classeService, never())
                .cadastrar(any());
    }

    @Test
    void deveRetornarBadRequestAoAtualizarClasseInvalida()
            throws Exception {

        mockMvc.perform(
                        put("/api/classes/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(classeService, never())
                .atualizar(anyLong(), any());
    }
}