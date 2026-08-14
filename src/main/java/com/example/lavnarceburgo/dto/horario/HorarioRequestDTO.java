package com.example.lavnarceburgo.dto.horario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioRequestDTO(

        @NotNull(message = "A duração da aula é obrigatória")
        @Positive(message = "A duração da aula deve ser maior que zero")
        Integer duracaoaula,

        @NotNull(message = "A classe é obrigatória")
        Long codclasse,

        @NotBlank(message = "A sala é obrigatória")
        String sala,

        @NotNull(message = "O dia da semana é obrigatório")
        DayOfWeek diaSemana,

        @NotNull(message = "O horário de início é obrigatório")
        LocalTime horaInicio

) {
}