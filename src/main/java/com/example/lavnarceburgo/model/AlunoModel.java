package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "aluno")
@Data
public class AlunoModel {

    @Id
    @Column(name = "codaluno")
    private Long codaluno;

    @OneToOne
    @MapsId
    @JoinColumn(name = "codaluno")
    private UsuarioModel usuario;

    @ManyToOne
    @JoinColumn(name = "codclasse")
    private ClasseModel classe;
}
