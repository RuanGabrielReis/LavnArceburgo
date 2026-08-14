package com.example.lavnarceburgo.dto.aula;

import java.time.LocalDateTime;

public record AulaResponseDTO(

        Long codaula,
        Long codclasse,
        String nivel,
        LocalDateTime diahora

) {
}