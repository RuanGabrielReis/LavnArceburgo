package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.anotacao.AnotacaoRequestDTO;
import com.example.lavnarceburgo.dto.anotacao.AnotacaoResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.*;
import com.example.lavnarceburgo.model.enums.TipoAnotacao;
import com.example.lavnarceburgo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnotacoesService {

    private final AnotacoesRepository anotacoesRepository;
    private final ClasseRepository classeRepository;
    private final AlunoRepository alunoRepository;
    private final AulaRepository aulaRepository;

    public AnotacoesService(
            AnotacoesRepository anotacoesRepository,
            ClasseRepository classeRepository,
            AlunoRepository alunoRepository,
            AulaRepository aulaRepository
    ) {
        this.anotacoesRepository = anotacoesRepository;
        this.classeRepository = classeRepository;
        this.alunoRepository = alunoRepository;
        this.aulaRepository = aulaRepository;
    }

    @Transactional
    public AnotacaoResponseDTO cadastrar(AnotacaoRequestDTO dto) {

        AnotacoesModel anotacao = new AnotacoesModel();

        anotacao.setTipo(dto.tipo());
        anotacao.setTexto(dto.texto());

        configurarRelacionamento(anotacao, dto);

        anotacao = anotacoesRepository.save(anotacao);

        return converterParaDTO(anotacao);
    }

    public List<AnotacaoResponseDTO> listarTodas() {
        return anotacoesRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public AnotacaoResponseDTO buscarPorId(Long id) {

        AnotacoesModel anotacao = anotacoesRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Anotação não encontrada")
                );

        return converterParaDTO(anotacao);
    }

    @Transactional
    public AnotacaoResponseDTO atualizar(
            Long id,
            AnotacaoRequestDTO dto
    ) {

        AnotacoesModel anotacao = anotacoesRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Anotação não encontrada")
                );

        anotacao.setTipo(dto.tipo());
        anotacao.setTexto(dto.texto());

        anotacao.setClasse(null);
        anotacao.setAluno(null);
        anotacao.setAula(null);

        configurarRelacionamento(anotacao, dto);

        anotacao = anotacoesRepository.save(anotacao);

        return converterParaDTO(anotacao);
    }

    @Transactional
    public void excluir(Long id) {

        AnotacoesModel anotacao = anotacoesRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Anotação não encontrada")
                );

        anotacoesRepository.delete(anotacao);
    }

    private void configurarRelacionamento(
            AnotacoesModel anotacao,
            AnotacaoRequestDTO dto
    ) {

        if (dto.tipo() == TipoAnotacao.TURMA) {

            if (dto.codclasse() == null) {
                throw new IllegalArgumentException(
                        "O código da classe é obrigatório para anotação de turma"
                );
            }

            ClasseModel classe = classeRepository.findById(dto.codclasse())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Classe não encontrada")
                    );

            anotacao.setClasse(classe);

        } else if (dto.tipo() == TipoAnotacao.ALUNO) {

            if (dto.codaluno() == null) {
                throw new IllegalArgumentException(
                        "O código do aluno é obrigatório para anotação de aluno"
                );
            }

            AlunoModel aluno = alunoRepository.findById(dto.codaluno())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Aluno não encontrado")
                    );

            anotacao.setAluno(aluno);

        } else if (dto.tipo() == TipoAnotacao.AULA) {

            if (dto.codaula() == null) {
                throw new IllegalArgumentException(
                        "O código da aula é obrigatório para anotação de aula"
                );
            }

            AulaModel aula = aulaRepository.findById(dto.codaula())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Aula não encontrada")
                    );

            anotacao.setAula(aula);
        }
    }

    private AnotacaoResponseDTO converterParaDTO(
            AnotacoesModel anotacao
    ) {

        return new AnotacaoResponseDTO(
                anotacao.getCodanotacao(),
                anotacao.getTipo(),
                anotacao.getTexto(),
                anotacao.getClasse() != null
                        ? anotacao.getClasse().getCodclasse()
                        : null,
                anotacao.getAluno() != null
                        ? anotacao.getAluno().getCodaluno()
                        : null,
                anotacao.getAula() != null
                        ? anotacao.getAula().getCodaula()
                        : null
        );
    }
}