package com.example.lavnarceburgo.dto.presenca;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PresencaRequestDTO(

        @NotNull(message = "O aluno é obrigatório")
        @Positive(message = "O código do aluno deve ser maior que zero")
        Long codaluno,

        @NotNull(message = "A aula é obrigatória")
        @Positive(message = "O código da aula deve ser maior que zero")
        Long codaula,

        @NotNull(message = "A presença é obrigatória")
        Boolean presente,

        String observacao

) {
}