package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "funcionario")
@Data
public class FuncionarioModel {

    @Id
    private Long codfuncionario;

    @OneToOne
    @MapsId
    @JoinColumn(name = "codfuncionario")
    private UsuarioModel usuario;

    @Column(name = "cargo", nullable = false)
    private String cargo;

    @Column(name = "salario", nullable = false)
    private Float salario;
}