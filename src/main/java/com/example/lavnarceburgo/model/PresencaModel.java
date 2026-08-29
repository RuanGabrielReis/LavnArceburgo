package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "presenca")
@Data
public class PresencaModel {

    @EmbeddedId
    private PresencaId id;

    @ManyToOne
    @MapsId("codaluno")
    @JoinColumn(name = "codaluno")
    private AlunoModel aluno;

    @ManyToOne
    @MapsId("codaula")
    @JoinColumn(name = "codaula")
    private AulaModel aula;

    @Column(name = "presente", nullable = false)
    private Boolean presente;

    @Column(name = "observacao")
    private String observacao;
}