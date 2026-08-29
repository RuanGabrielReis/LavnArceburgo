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
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public HorarioService(
            HorarioRepository horarioRepository,
            ClasseRepository classeRepository, UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.horarioRepository = horarioRepository;
        this.classeRepository = classeRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public HorarioResponseDTO cadastrar(HorarioRequestDTO dto) {

        ClasseModel classe = classeRepository.findById(dto.codclasse())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        validarConflitos(dto, classe, null);

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
                .filter(this::podeAcessarHorario)
                .map(this::converterParaDTO)
                .toList();
    }

    private boolean podeAcessarHorario(HorarioModel horario) {

        return horario.getClasse() != null
                && usuarioAutenticadoService
                .podeAcessarClasse(
                        horario.getClasse()
                );
    }

    public HorarioResponseDTO buscarPorId(Long id) {

        HorarioModel horario = horarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Horário não encontrado"
                        )
                );

        validarAcessoHorario(horario);

        return converterParaDTO(horario);
    }

    private void validarAcessoHorario(HorarioModel horario) {

        if (horario.getClasse() == null) {
            throw new SecurityException(
                    "Você não possui acesso a este horário"
            );
        }

        usuarioAutenticadoService
                .validarAcessoAClasse(
                        horario.getClasse()
                );
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

        validarConflitos(dto, classe, id);

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

    private void validarConflitos(
            HorarioRequestDTO dto,
            ClasseModel novaClasse,
            Long horarioIgnorarId
    ) {

        var horariosDoDia =
                horarioRepository.findByDiaSemana(
                        dto.diaSemana()
                );

        var novoInicio = dto.horaInicio();

        var novoFim = novoInicio.plusMinutes(
                dto.duracaoaula()
        );

        for (HorarioModel horarioExistente : horariosDoDia) {

            // No PUT, ignora o próprio horário sendo atualizado.
            if (horarioIgnorarId != null
                    && horarioIgnorarId.equals(
                    horarioExistente.getCodhorario()
            )) {
                continue;
            }

            var inicioExistente =
                    horarioExistente.getHoraInicio();

            var fimExistente =
                    inicioExistente.plusMinutes(
                            horarioExistente.getDuracaoaula()
                    );

            boolean horariosSobrepostos =
                    novoInicio.isBefore(fimExistente)
                            && novoFim.isAfter(inicioExistente);

            if (!horariosSobrepostos) {
                continue;
            }

            // MESMA TURMA
            if (horarioExistente
                    .getClasse()
                    .getCodclasse()
                    .equals(novaClasse.getCodclasse())) {

                throw new IllegalArgumentException(
                        "A classe já possui outro horário neste período"
                );
            }

            // MESMA SALA
            if (horarioExistente.getSala() != null
                    && horarioExistente.getSala()
                    .trim()
                    .equalsIgnoreCase(
                            dto.sala().trim()
                    )) {

                throw new IllegalArgumentException(
                        "A sala já está ocupada neste período"
                );
            }

            // MESMO PROFESSOR
            if (horarioExistente.getClasse().getProfessor() != null
                    && novaClasse.getProfessor() != null
                    && horarioExistente
                    .getClasse()
                    .getProfessor()
                    .getCodfuncionario()
                    .equals(
                            novaClasse
                                    .getProfessor()
                                    .getCodfuncionario()
                    )) {

                throw new IllegalArgumentException(
                        "O professor já possui outra aula neste período"
                );
            }
        }
    }
}
