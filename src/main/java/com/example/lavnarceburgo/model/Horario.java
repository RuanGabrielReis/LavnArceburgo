package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "horario")
@Data


public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codhorario;
    private int duracaoaula;

    @OneToOne
    @JoinColumn(name = "codusuario")
    private Usuario fk_funcionario_usuario;




}
