package com.example.lavnarceburgo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> campos = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(erro ->
                        campos.put(
                                erro.getField(),
                                erro.getDefaultMessage()
                        )
                );

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", 400);
        resposta.put("erro", "Erro de validação");
        resposta.put("campos", campos);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex
    ) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", 404);
        resposta.put("erro", "Recurso não encontrado");
        resposta.put("mensagem", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(resposta);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException ex
    ) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", 400);
        resposta.put("erro", "Requisição inválida");
        resposta.put("mensagem", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonInvalido(
            HttpMessageNotReadableException ex
    ) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", 400);
        resposta.put("erro", "Requisição inválida");
        resposta.put(
                "mensagem",
                "O corpo da requisição está inválido ou possui valores incompatíveis"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex
    ) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", 409);
        resposta.put("erro", "Conflito de dados");
        resposta.put(
                "mensagem",
                "A operação não pode ser concluída porque existem dados vinculados ou valores duplicados"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resposta);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(
            SecurityException ex
    ) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", 403);
        resposta.put("erro", "Acesso negado");
        resposta.put("mensagem", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(resposta);
    }
}