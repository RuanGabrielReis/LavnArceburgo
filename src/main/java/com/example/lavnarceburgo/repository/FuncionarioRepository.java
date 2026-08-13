package com.example.lavnarceburgo.repository;

import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuncionarioRepository extends JpaRepository<FuncionarioModel, Long> {

    List<FuncionarioModel> findByCargo(Cargo cargo);
}