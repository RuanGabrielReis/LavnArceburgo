package com.example.lavnarceburgo.dto.funcionario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record AlterarMinhaSenhaDTO(

        // Senha que o usuário utiliza atualmente.
        // O backend vai comparar esse valor com
        // o hash BCrypt salvo no banco.
        @NotBlank(message = "A senha atual é obrigatória")
        String senhaAtual,


        // Nova senha que será salva.
        // Mantemos a mesma regra mínima de 6 caracteres
        // que o projeto já utiliza.
        @NotBlank(message = "A nova senha é obrigatória")
        @Size(
                min = 6,
                message = "A nova senha deve possuir no mínimo 6 caracteres"
        )
        String novaSenha

) {
}
