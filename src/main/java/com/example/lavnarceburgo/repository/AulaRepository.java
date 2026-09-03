package com.example.lavnarceburgo.repository;

import com.example.lavnarceburgo.model.AulaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AulaRepository extends JpaRepository<AulaModel, Long> {

    List<AulaModel> findByClasseCodclasse(Long codclasse);

    List<AulaModel> findByClasseCodclasseAndDiahoraBetween(
            Long codclasse,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    boolean existsByClasseCodclasseAndDiahora(
            Long codclasse,
            LocalDateTime diahora
    );

    boolean existsByClasseCodclasseAndDiahoraAndCodaulaNot(
            Long codclasse,
            LocalDateTime diahora,
            Long codaula
    );
}