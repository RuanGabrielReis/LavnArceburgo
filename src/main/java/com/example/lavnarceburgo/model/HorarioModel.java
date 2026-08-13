package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

    @Column(name = "sala")
    private String sala;

    @Column(name = "diahora", nullable = false)
    private LocalDateTime diahora;
}




