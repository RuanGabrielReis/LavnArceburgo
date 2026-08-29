package com.example.lavnarceburgo.dto.aula;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AulaRequestDTO(

        @NotNull(message = "A classe é obrigatória")
        @Positive(message = "O código da classe deve ser maior que zero")
        Long codclasse,

        @NotNull(message = "A data e hora da aula são obrigatórias")
        LocalDateTime diahora

) {
}