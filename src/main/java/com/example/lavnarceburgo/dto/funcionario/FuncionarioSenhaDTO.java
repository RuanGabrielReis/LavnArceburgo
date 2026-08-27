package com.example.lavnarceburgo.dto.funcionario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FuncionarioSenhaDTO(

        @NotBlank
        @Size(min = 6)
        String senha

) {
}