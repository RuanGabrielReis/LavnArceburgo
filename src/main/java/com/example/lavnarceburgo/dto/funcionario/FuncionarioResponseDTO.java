package com.example.lavnarceburgo.dto.funcionario;

import com.example.lavnarceburgo.model.enums.Cargo;

import java.math.BigDecimal;

public record FuncionarioResponseDTO(

        Long codfuncionario,
        String nome,
        String cpf,
        String telefone,
        String rg,
        String endereco,
        String cidade,
        String email,
        Cargo cargo,
        BigDecimal salario
) {
}