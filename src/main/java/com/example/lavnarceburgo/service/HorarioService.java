package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.horario.HorarioRequestDTO;
import com.example.lavnarceburgo.dto.horario.HorarioResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.HorarioModel;
import com.example.lavnarceburgo.repository.ClasseRepository;
import com.example.lavnarceburgo.repository.HorarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final ClasseRepository classeRepository;

    public HorarioService(
            HorarioRepository horarioRepository,
            ClasseRepository classeRepository
    ) {
        this.horarioRepository = horarioRepository;
        this.classeRepository = classeRepository;
    }

    @Transactional
    public HorarioResponseDTO cadastrar(HorarioRequestDTO dto) {

        ClasseModel classe = classeRepository.findById(dto.codclasse())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        HorarioModel horario = new HorarioModel();

        horario.setDuracaoaula(dto.duracaoaula());
        horario.setClasse(classe);
        horario.setSala(dto.sala());
        horario.setDiaSemana(dto.diaSemana());
        horario.setHoraInicio(dto.horaInicio());

        horario = horarioRepository.save(horario);

        return converterParaDTO(horario);
    }

    public List<HorarioResponseDTO> listarTodos() {
        return horarioRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public HorarioResponseDTO buscarPorId(Long id) {

        HorarioModel horario = horarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Horário não encontrado")
                );

        return converterParaDTO(horario);
    }

    @Transactional
    public HorarioResponseDTO atualizar(
            Long id,
            HorarioRequestDTO dto
    ) {

        HorarioModel horario = horarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Horário não encontrado")
                );

        ClasseModel classe = classeRepository.findById(dto.codclasse())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        horario.setDuracaoaula(dto.duracaoaula());
        horario.setClasse(classe);
        horario.setSala(dto.sala());
        horario.setDiaSemana(dto.diaSemana());
        horario.setHoraInicio(dto.horaInicio());

        horario = horarioRepository.save(horario);

        return converterParaDTO(horario);
    }

    @Transactional
    public void excluir(Long id) {

        HorarioModel horario = horarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Horário não encontrado")
                );

        horarioRepository.delete(horario);
    }

    private HorarioResponseDTO converterParaDTO(HorarioModel horario) {

        ClasseModel classe = horario.getClasse();

        return new HorarioResponseDTO(
                horario.getCodhorario(),
                horario.getDuracaoaula(),
                classe.getCodclasse(),
                classe.getNivel(),
                horario.getSala(),
                horario.getDiaSemana(),
                horario.getHoraInicio()
        );
    }
}
