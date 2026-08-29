package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.config.JwtAuthFilter;
import com.example.lavnarceburgo.dto.auth.LoginRequestDTO;
import com.example.lavnarceburgo.dto.auth.LoginResponseDTO;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void deveRealizarLoginComSucesso() throws Exception {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "master@lavn.com",
                        "master123"
                );

        LoginResponseDTO resposta =
                new LoginResponseDTO(
                        1L,
                        "Administrador Lavn",
                        "master@lavn.com",
                        Cargo.MASTER,
                        "token-jwt-teste"
                );

        when(authService.login(dto))
                .thenReturn(resposta);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codfuncionario").value(1L))
                .andExpect(jsonPath("$.nome")
                        .value("Administrador Lavn"))
                .andExpect(jsonPath("$.email")
                        .value("master@lavn.com"))
                .andExpect(jsonPath("$.cargo")
                        .value("MASTER"))
                .andExpect(jsonPath("$.token")
                        .value("token-jwt-teste"));

        verify(authService).login(dto);
    }

    @Test
    void deveRetornarBadRequestQuandoEmailEstiverVazio()
            throws Exception {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "",
                        "master123"
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(authService, never())
                .login(any());
    }

    @Test
    void deveRetornarBadRequestQuandoEmailForInvalido()
            throws Exception {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "email-invalido",
                        "master123"
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(authService, never())
                .login(any());
    }

    @Test
    void deveRetornarBadRequestQuandoSenhaEstiverVazia()
            throws Exception {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "master@lavn.com",
                        ""
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(authService, never())
                .login(any());
    }

    @Test
    void deveRetornarBadRequestQuandoCredenciaisForemInvalidas()
            throws Exception {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "master@lavn.com",
                        "senhaErrada"
                );

        when(authService.login(dto))
                .thenThrow(
                        new IllegalArgumentException(
                                "E-mail ou senha inválidos"
                        )
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro")
                        .value("Requisição inválida"))
                .andExpect(jsonPath("$.mensagem")
                        .value("E-mail ou senha inválidos"));

        verify(authService).login(dto);
    }
}