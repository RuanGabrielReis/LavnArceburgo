package com.example.lavnarceburgo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codusuario;
    private String nome;
    private String cpf;
    private String telefone;
    private String rg;
    private String endereco;
    private String cidade;
    private String email;
}
