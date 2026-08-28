package com.example.lavnarceburgo.dto.funcionario;

import com.example.lavnarceburgo.model.enums.Cargo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record FuncionarioUpdateDTO(

        @NotBlank
        String nome,

        @NotBlank
        String cpf,

        String telefone,

        String rg,

        String endereco,

        String cidade,

        @NotBlank
        @Email
        String email,

        @NotNull
        Cargo cargo

) {
}