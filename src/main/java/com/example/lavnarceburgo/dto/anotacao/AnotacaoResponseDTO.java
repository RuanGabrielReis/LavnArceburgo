package com.example.lavnarceburgo.dto.anotacao;

import com.example.lavnarceburgo.model.enums.TipoAnotacao;

public record AnotacaoResponseDTO(

        Long codanotacao,
        TipoAnotacao tipo,
        String texto,

        Long codclasse,
        Long codaluno,
        Long codaula

) {
}
