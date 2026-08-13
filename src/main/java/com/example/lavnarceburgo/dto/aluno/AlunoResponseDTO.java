package com.example.lavnarceburgo.dto.aluno;

public record AlunoResponseDTO(

        Long codaluno,
        String nome,
        String cpf,
        String telefone,
        String rg,
        String endereco,
        String cidade,
        String email,
        Long codclasse,
        String nivel
) {
}