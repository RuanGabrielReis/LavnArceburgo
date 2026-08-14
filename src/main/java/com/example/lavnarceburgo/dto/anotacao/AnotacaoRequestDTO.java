package com.example.lavnarceburgo.dto.anotacao;

import com.example.lavnarceburgo.model.enums.TipoAnotacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnotacaoRequestDTO(

        @NotNull(message = "O tipo da anotação é obrigatório")
        TipoAnotacao tipo,

        @NotBlank(message = "O texto da anotação é obrigatório")
        String texto,

        Long codclasse,

        Long codaluno,

        Long codaula

) {
}