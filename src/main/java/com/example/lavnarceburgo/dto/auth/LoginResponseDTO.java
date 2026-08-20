package com.example.lavnarceburgo.dto.auth;

import com.example.lavnarceburgo.model.enums.Cargo;

public record LoginResponseDTO(

        Long codfuncionario,
        String nome,
        String email,
        Cargo cargo

) {
}