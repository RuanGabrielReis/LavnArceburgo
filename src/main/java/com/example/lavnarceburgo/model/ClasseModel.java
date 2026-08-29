package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "classe")
@Data
public class ClasseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codclasse")
    private Long codclasse;

    @Column(name = "nivel", nullable = false)
    private String nivel;

    @ManyToOne
    @JoinColumn(name = "codprofessor", nullable = false)
    private FuncionarioModel professor;
}