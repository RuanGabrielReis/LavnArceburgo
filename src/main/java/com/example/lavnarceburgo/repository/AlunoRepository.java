package com.example.lavnarceburgo.repository;

import com.example.lavnarceburgo.model.AlunoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlunoRepository extends JpaRepository<AlunoModel, Long> {

    List<AlunoModel> findByClasseCodclasse(Long codclasse);

    boolean existsByClasseCodclasse(Long codclasse);
}