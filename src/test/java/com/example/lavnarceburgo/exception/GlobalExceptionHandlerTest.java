package com.example.lavnarceburgo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void deveRetornar404ParaResourceNotFound() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "Aluno não encontrado"
                );

        ResponseEntity<Map<String, Object>> resposta =
                handler.handleNotFound(exception);

        assertEquals(
                HttpStatus.NOT_FOUND,
                resposta.getStatusCode()
        );

        assertNotNull(resposta.getBody());

        assertEquals(
                404,
                resposta.getBody().get("status")
        );

        assertEquals(
                "Recurso não encontrado",
                resposta.getBody().get("erro")
        );

        assertEquals(
                "Aluno não encontrado",
                resposta.getBody().get("mensagem")
        );
    }

    @Test
    void deveRetornar400ParaIllegalArgumentException() {

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "Operação inválida"
                );

        ResponseEntity<Map<String, Object>> resposta =
                handler.handleBadRequest(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                resposta.getStatusCode()
        );

        assertNotNull(resposta.getBody());

        assertEquals(
                400,
                resposta.getBody().get("status")
        );

        assertEquals(
                "Requisição inválida",
                resposta.getBody().get("erro")
        );

        assertEquals(
                "Operação inválida",
                resposta.getBody().get("mensagem")
        );
    }

    @Test
    void deveRetornar403ParaSecurityException() {

        SecurityException exception =
                new SecurityException(
                        "Você não possui acesso a esta turma"
                );

        ResponseEntity<Map<String, Object>> resposta =
                handler.handleSecurityException(exception);

        assertEquals(
                HttpStatus.FORBIDDEN,
                resposta.getStatusCode()
        );

        assertNotNull(resposta.getBody());

        assertEquals(
                403,
                resposta.getBody().get("status")
        );

        assertEquals(
                "Acesso negado",
                resposta.getBody().get("erro")
        );

        assertEquals(
                "Você não possui acesso a esta turma",
                resposta.getBody().get("mensagem")
        );
    }

    @Test
    void deveRetornar409ParaViolacaoDeIntegridade() {

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "erro técnico do banco"
                );

        ResponseEntity<Map<String, Object>> resposta =
                handler.handleDataIntegrityViolation(
                        exception
                );

        assertEquals(
                HttpStatus.CONFLICT,
                resposta.getStatusCode()
        );

        assertNotNull(resposta.getBody());

        assertEquals(
                409,
                resposta.getBody().get("status")
        );

        assertEquals(
                "Conflito de dados",
                resposta.getBody().get("erro")
        );

        assertEquals(
                "A operação não pode ser concluída porque existem dados vinculados ou valores duplicados",
                resposta.getBody().get("mensagem")
        );

        // Garante que erro interno do banco não foi exposto
        assertFalse(
                resposta.getBody()
                        .toString()
                        .contains("erro técnico do banco")
        );
    }
}