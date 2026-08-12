package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "funcionario")
@Data

public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codfuncionario;
    private String cargo;
    private float salario;

    @OneToOne
    @JoinColumn(name = "codusuario")
    private Usuario fk_funcionario_usuario;
}
