package com.example.lavnarceburgo.dto.funcionario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FuncionarioSenhaDTO(

        @NotBlank(message = "A senha é obrigatória")
        @Size(
                min = 6,
                message = "A senha deve possuir no mínimo 6 caracteres"
        )
        String senha

) {
}