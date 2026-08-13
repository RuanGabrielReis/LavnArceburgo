package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "anotacoes")
@Data
public class AnotacoesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codanotacao")
    private Long codanotacao;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "texto", nullable = false, columnDefinition = "TEXT")
    private String texto;

    @ManyToOne
    @JoinColumn(name = "codaluno", nullable = false)
    private AlunoModel aluno;

    @ManyToOne
    @JoinColumn(name = "codaula", nullable = false)
    private AulaModel aula;

    @Column(name = "codclasse", nullable = false)
    private Long codclasse;
}