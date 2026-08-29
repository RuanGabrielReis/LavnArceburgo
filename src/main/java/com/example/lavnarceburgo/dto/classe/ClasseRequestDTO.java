package com.example.lavnarceburgo.dto.classe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ClasseRequestDTO(

        @NotBlank(message = "O nível é obrigatório")
        String nivel,

        @NotNull(message = "O professor é obrigatório")
        @Positive(message = "O código do professor deve ser maior que zero")
        Long codprofessor
) {
}