package com.example.lavnarceburgo.repository;

import com.example.lavnarceburgo.model.HorarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface HorarioRepository extends JpaRepository<HorarioModel, Long> {

    List<HorarioModel> findByClasseCodclasse(Long codclasse);

    List<HorarioModel> findByDiaSemana(DayOfWeek diaSemana);
}