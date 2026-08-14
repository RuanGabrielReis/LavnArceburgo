package com.example.lavnarceburgo.dto.horario;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioResponseDTO(

        Long codhorario,
        Integer duracaoaula,
        Long codclasse,
        String nivel,
        String sala,
        DayOfWeek diaSemana,
        LocalTime horaInicio

) {
}
