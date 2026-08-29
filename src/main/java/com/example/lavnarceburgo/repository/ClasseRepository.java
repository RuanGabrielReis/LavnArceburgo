package com.example.lavnarceburgo.repository;

import com.example.lavnarceburgo.model.ClasseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClasseRepository extends JpaRepository<ClasseModel, Long> {

    List<ClasseModel> findByProfessorCodfuncionario(Long codprofessor);

    List<ClasseModel> findByNivel(String nivel);

    boolean existsByProfessorCodfuncionario(Long codfuncionario);
}