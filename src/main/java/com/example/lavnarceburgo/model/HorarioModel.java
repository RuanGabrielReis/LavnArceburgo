package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "horario")
@Data
public class HorarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codhorario")
    private Long codhorario;

    @Column(name = "duracaoaula", nullable = false)
    private Integer duracaoaula;

    @ManyToOne
    @JoinColumn(name = "codclasse", nullable = false)
    private ClasseModel classe;

    @Column(name = "sala", nullable = false)
    private String sala;

    @Enumerated(EnumType.STRING)
    @Column(name = "diasemana", nullable = false)
    private DayOfWeek diaSemana;

    @Column(name = "horainicio", nullable = false)
    private LocalTime horaInicio;
}