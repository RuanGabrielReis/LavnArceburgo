package com.example.lavnarceburgo.repository;

import com.example.lavnarceburgo.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    Optional<UsuarioModel> findByCpf(String cpf);

    Optional<UsuarioModel> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}