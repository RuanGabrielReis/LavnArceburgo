package com.example.lavnarceburgo.dto.presenca;

import jakarta.validation.constraints.NotNull;

public record PresencaRequestDTO(

        @NotNull(message = "O aluno é obrigatório")
        Long codaluno,

        @NotNull(message = "A aula é obrigatória")
        Long codaula,

        @NotNull(message = "A presença é obrigatória")
        Boolean presente,

        String observacao

) {
}