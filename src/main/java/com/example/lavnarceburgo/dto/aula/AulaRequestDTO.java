package com.example.lavnarceburgo.dto.aula;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AulaRequestDTO(

        @NotNull(message = "A classe é obrigatória")
        Long codclasse,

        @NotNull(message = "A data e hora da aula são obrigatórias")
        LocalDateTime diahora

) {
}