package com.example.lavnarceburgo.model;

import com.example.lavnarceburgo.model.UsuarioModel;
import jakarta.persistence.*;
import lombok.Data;
import com.example.lavnarceburgo.model.enums.Cargo;

import java.math.BigDecimal;

@Entity
@Table(name = "funcionario")
@Data
public class FuncionarioModel {

    @Id
    @Column(name = "codfuncionario")
    private Long codfuncionario;

    @OneToOne
    @MapsId
    @JoinColumn(name = "codfuncionario")
    private UsuarioModel usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo", nullable = false)
    private Cargo cargo;

    @Column(name = "senha", nullable = false)
    private String senha;
}