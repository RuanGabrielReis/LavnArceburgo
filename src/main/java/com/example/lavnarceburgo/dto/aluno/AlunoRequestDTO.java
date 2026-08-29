package com.example.lavnarceburgo.dto.aluno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AlunoRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O CPF é obrigatório")
        String cpf,

        String telefone,

        String rg,

        String endereco,

        String cidade,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @NotNull(message = "A classe é obrigatória")
        @Positive(message = "O código da classe deve ser maior que zero")
        Long codclasse
) {
}