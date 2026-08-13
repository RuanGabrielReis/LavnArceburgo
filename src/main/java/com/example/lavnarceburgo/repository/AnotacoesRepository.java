package com.example.lavnarceburgo.repository;

import com.example.lavnarceburgo.model.AnotacoesModel;
import com.example.lavnarceburgo.model.enums.TipoAnotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnotacoesRepository extends JpaRepository<AnotacoesModel, Long> {

    List<AnotacoesModel> findByTipo(TipoAnotacao tipo);

    List<AnotacoesModel> findByClasseCodclasse(Long codclasse);

    List<AnotacoesModel> findByAlunoCodaluno(Long codaluno);

    List<AnotacoesModel> findByAulaCodaula(Long codaula);
}
