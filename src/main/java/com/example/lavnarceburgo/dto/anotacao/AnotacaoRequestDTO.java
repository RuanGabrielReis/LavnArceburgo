package com.example.lavnarceburgo.dto.anotacao;

import com.example.lavnarceburgo.model.enums.TipoAnotacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AnotacaoRequestDTO(

        @NotNull(message = "O tipo da anotação é obrigatório")
        TipoAnotacao tipo,

        @NotBlank(message = "O texto da anotação é obrigatório")
        String texto,

        @Positive(message = "O código da classe deve ser maior que zero")
        Long codclasse,

        @Positive(message = "O código do aluno deve ser maior que zero")
        Long codaluno,

        @Positive(message = "O código da aula deve ser maior que zero")
        Long codaula

) {
}