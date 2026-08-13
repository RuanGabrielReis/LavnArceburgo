package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "aula")
@Data
public class AulaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codaula")
    private Long codaula;

    @ManyToOne
    @JoinColumn(name = "codclasse", nullable = false)
    private ClasseModel classe;

    @Column(name = "diahora", nullable = false)
    private LocalDateTime diahora;
}
