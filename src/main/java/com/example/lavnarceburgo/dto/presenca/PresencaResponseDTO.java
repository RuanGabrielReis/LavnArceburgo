package com.example.lavnarceburgo.dto.presenca;

public record PresencaResponseDTO(

        Long codaluno,
        String nomeAluno,

        Long codaula,

        Long codclasse,
        String nivel,

        Boolean presente,
        String observacao

) {
}