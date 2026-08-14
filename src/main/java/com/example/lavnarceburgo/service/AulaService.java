package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.aula.AulaRequestDTO;
import com.example.lavnarceburgo.dto.aula.AulaResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.AulaModel;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.repository.AulaRepository;
import com.example.lavnarceburgo.repository.ClasseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final ClasseRepository classeRepository;

    public AulaService(
            AulaRepository aulaRepository,
            ClasseRepository classeRepository
    ) {
        this.aulaRepository = aulaRepository;
        this.classeRepository = classeRepository;
    }

    @Transactional
    public AulaResponseDTO cadastrar(AulaRequestDTO dto) {

        ClasseModel classe = classeRepository.findById(dto.codclasse())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        AulaModel aula = new AulaModel();

        aula.setClasse(classe);
        aula.setDiahora(dto.diahora());

        aula = aulaRepository.save(aula);

        return converterParaDTO(aula);
    }

    public List<AulaResponseDTO> listarTodos() {

        return aulaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public AulaResponseDTO buscarPorId(Long id) {

        AulaModel aula = aulaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aula não encontrada")
                );

        return converterParaDTO(aula);
    }

    @Transactional
    public AulaResponseDTO atualizar(
            Long id,
            AulaRequestDTO dto
    ) {

        AulaModel aula = aulaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aula não encontrada")
                );

        ClasseModel classe = classeRepository.findById(dto.codclasse())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        aula.setClasse(classe);
        aula.setDiahora(dto.diahora());

        aula = aulaRepository.save(aula);

        return converterParaDTO(aula);
    }

    @Transactional
    public void excluir(Long id) {

        AulaModel aula = aulaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aula não encontrada")
                );

        aulaRepository.delete(aula);
    }

    private AulaResponseDTO converterParaDTO(AulaModel aula) {

        ClasseModel classe = aula.getClasse();

        return new AulaResponseDTO(
                aula.getCodaula(),
                classe.getCodclasse(),
                classe.getNivel(),
                aula.getDiahora()
        );
    }
}