package com.example.lavnarceburgo.model;

import com.example.lavnarceburgo.model.enums.TipoAnotacao;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoAnotacao tipo;

    @Column(name = "texto", nullable = false, columnDefinition = "TEXT")
    private String texto;

    @ManyToOne
    @JoinColumn(name = "codclasse")
    private ClasseModel classe;

    @ManyToOne
    @JoinColumn(name = "codaluno")
    private AlunoModel aluno;

    @ManyToOne
    @JoinColumn(name = "codaula")
    private AulaModel aula;
}