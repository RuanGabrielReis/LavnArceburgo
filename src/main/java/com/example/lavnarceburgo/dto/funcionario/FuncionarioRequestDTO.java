package com.example.lavnarceburgo.dto.funcionario;

import com.example.lavnarceburgo.model.enums.Cargo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FuncionarioRequestDTO(

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

        @NotNull(message = "O cargo é obrigatório")
        Cargo cargo,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve possuir no mínimo 6 caracteres")
                String senha
) {
}