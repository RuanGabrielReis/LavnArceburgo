package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.model.AulaModel;
import com.example.lavnarceburgo.model.HorarioModel;
import com.example.lavnarceburgo.repository.AulaRepository;
import com.example.lavnarceburgo.repository.HorarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class GeradorAulasService {

    private static final ZoneId ZONA =
            ZoneId.of("America/Sao_Paulo");

    private final HorarioRepository horarioRepository;
    private final AulaRepository aulaRepository;

    public GeradorAulasService(
            HorarioRepository horarioRepository,
            AulaRepository aulaRepository
    ) {
        this.horarioRepository = horarioRepository;
        this.aulaRepository = aulaRepository;
    }

    @Transactional
    public void gerarAulasDoDia() {

        LocalDate hoje = LocalDate.now(ZONA);

        List<HorarioModel> horarios =
                horarioRepository.findByDiaSemana(
                        hoje.getDayOfWeek()
                );

        for (HorarioModel horario : horarios) {

            LocalDateTime diahora =
                    LocalDateTime.of(
                            hoje,
                            horario.getHoraInicio()
                    );

            Long codclasse =
                    horario.getClasse().getCodclasse();

            boolean aulaJaExiste =
                    aulaRepository
                            .existsByClasseCodclasseAndDiahora(
                                    codclasse,
                                    diahora
                            );

            if (aulaJaExiste) {
                continue;
            }

            AulaModel aula = new AulaModel();

            aula.setClasse(horario.getClasse());
            aula.setDiahora(diahora);

            aulaRepository.save(aula);
        }
    }
}