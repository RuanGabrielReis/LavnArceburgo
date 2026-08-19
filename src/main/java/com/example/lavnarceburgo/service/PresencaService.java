package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.presenca.PresencaRequestDTO;
import com.example.lavnarceburgo.dto.presenca.PresencaResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.AlunoModel;
import com.example.lavnarceburgo.model.AulaModel;
import com.example.lavnarceburgo.model.PresencaId;
import com.example.lavnarceburgo.model.PresencaModel;
import com.example.lavnarceburgo.repository.AlunoRepository;
import com.example.lavnarceburgo.repository.AulaRepository;
import com.example.lavnarceburgo.repository.PresencaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PresencaService {

    private final PresencaRepository presencaRepository;
    private final AlunoRepository alunoRepository;
    private final AulaRepository aulaRepository;

    public PresencaService(
            PresencaRepository presencaRepository,
            AlunoRepository alunoRepository,
            AulaRepository aulaRepository
    ) {
        this.presencaRepository = presencaRepository;
        this.alunoRepository = alunoRepository;
        this.aulaRepository = aulaRepository;
    }

    @Transactional
    public PresencaResponseDTO cadastrar(PresencaRequestDTO dto) {

        AlunoModel aluno = alunoRepository.findById(dto.codaluno())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aluno não encontrado")
                );

        AulaModel aula = aulaRepository.findById(dto.codaula())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aula não encontrada")
                );

        validarMesmaClasse(aluno, aula);

        PresencaId id = new PresencaId(
                dto.codaluno(),
                dto.codaula()
        );

        if (presencaRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Já existe presença registrada para este aluno nesta aula"
            );
        }

        PresencaModel presenca = new PresencaModel();

        presenca.setId(id);
        presenca.setAluno(aluno);
        presenca.setAula(aula);
        presenca.setCodclasse(aluno.getClasse().getCodclasse());
        presenca.setPresente(dto.presente());
        presenca.setObservacao(dto.observacao());

        presenca = presencaRepository.save(presenca);

        return converterParaDTO(presenca);
    }

    public List<PresencaResponseDTO> listarTodas() {
        return presencaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public PresencaResponseDTO buscarPorId(
            Long codaluno,
            Long codaula
    ) {

        PresencaId id = new PresencaId(codaluno, codaula);

        PresencaModel presenca = presencaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Presença não encontrada")
                );

        return converterParaDTO(presenca);
    }

    @Transactional
    public PresencaResponseDTO atualizar(
            Long codaluno,
            Long codaula,
            PresencaRequestDTO dto
    ) {

        PresencaId id = new PresencaId(codaluno, codaula);

        PresencaModel presenca = presencaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Presença não encontrada")
                );

        // Não permitimos mudar aluno/aula através do PUT.
        if (!codaluno.equals(dto.codaluno())
                || !codaula.equals(dto.codaula())) {

            throw new IllegalArgumentException(
                    "Aluno e aula da presença não podem ser alterados"
            );
        }

        presenca.setPresente(dto.presente());
        presenca.setObservacao(dto.observacao());

        presenca = presencaRepository.save(presenca);

        return converterParaDTO(presenca);
    }

    @Transactional
    public void excluir(
            Long codaluno,
            Long codaula
    ) {

        PresencaId id = new PresencaId(codaluno, codaula);

        PresencaModel presenca = presencaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Presença não encontrada")
                );

        presencaRepository.delete(presenca);
    }

    private void validarMesmaClasse(
            AlunoModel aluno,
            AulaModel aula
    ) {

        if (aluno.getClasse() == null) {
            throw new IllegalArgumentException(
                    "O aluno não está vinculado a uma classe"
            );
        }

        if (!aluno.getClasse().getCodclasse()
                .equals(aula.getClasse().getCodclasse())) {

            throw new IllegalArgumentException(
                    "O aluno não pertence à mesma classe da aula"
            );
        }
    }

    private PresencaResponseDTO converterParaDTO(
            PresencaModel presenca
    ) {

        return new PresencaResponseDTO(
                presenca.getAluno().getCodaluno(),
                presenca.getAluno().getUsuario().getNome(),
                presenca.getAula().getCodaula(),
                presenca.getCodclasse(),
                presenca.getAula().getClasse().getNivel(),
                presenca.getPresente(),
                presenca.getObservacao()
        );
    }
}