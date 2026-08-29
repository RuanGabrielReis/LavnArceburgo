package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private FuncionarioModel funcionario;

    @BeforeEach
    void prepararDados() {

        jwtService = new JwtService(
                "lavn-chave-jwt-local-de-desenvolvimento-com-32-caracteres",
                28800000L
        );

        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail("professor@lavn.com");
        usuario.setNome("Professor Teste");

        funcionario = new FuncionarioModel();
        funcionario.setCodfuncionario(1L);
        funcionario.setUsuario(usuario);
        funcionario.setCargo(Cargo.PROFESSOR);
    }

    @Test
    void deveGerarToken() {

        String token = jwtService.gerarToken(funcionario);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void deveExtrairEmailDoToken() {

        String token = jwtService.gerarToken(funcionario);

        String email = jwtService.extrairEmail(token);

        assertEquals(
                "professor@lavn.com",
                email
        );
    }

    @Test
    void deveConsiderarTokenValido() {

        String token = jwtService.gerarToken(funcionario);

        assertTrue(
                jwtService.tokenValido(token)
        );
    }

    @Test
    void deveConsiderarTokenInvalido() {

        String tokenInvalido =
                "token.invalido.teste";

        assertFalse(
                jwtService.tokenValido(tokenInvalido)
        );
    }

    @Test
    void deveExtrairCodFuncionarioDoToken() {

        String token =
                jwtService.gerarToken(funcionario);

        Long codFuncionario =
                jwtService.extrairCodFuncionario(token);

        assertEquals(
                1L,
                codFuncionario
        );
    }
}