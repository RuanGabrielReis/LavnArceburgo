package com.example.lavnarceburgo.repository;

import com.example.lavnarceburgo.model.PresencaModel;
import com.example.lavnarceburgo.model.PresencaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresencaRepository extends JpaRepository<PresencaModel, PresencaId> {

    List<PresencaModel> findByAulaCodaula(Long codaula);

    List<PresencaModel> findByAlunoCodaluno(Long codaluno);
}