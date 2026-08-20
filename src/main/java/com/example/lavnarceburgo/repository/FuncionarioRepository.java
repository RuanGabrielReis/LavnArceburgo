package com.example.lavnarceburgo.repository;

import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<FuncionarioModel, Long> {

    List<FuncionarioModel> findByCargo(Cargo cargo);

    Optional<FuncionarioModel> findByUsuario_Email(String email);
}