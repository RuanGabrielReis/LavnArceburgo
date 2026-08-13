package com.example.lavnarceburgo.dto.classe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClasseRequestDTO(

        @NotBlank(message = "O nível é obrigatório")
        String nivel,

        @NotNull(message = "O professor é obrigatório")
        Long codprofessor
) {
}