package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.aula.AulaRequestDTO;
import com.example.lavnarceburgo.dto.aula.AulaResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.AulaModel;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.repository.AulaRepository;
import com.example.lavnarceburgo.repository.ClasseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final ClasseRepository classeRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public AulaService(
            AulaRepository aulaRepository,
            ClasseRepository classeRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.aulaRepository = aulaRepository;
        this.classeRepository = classeRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
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
                .filter(this::podeAcessarAula)
                .map(this::converterParaDTO)
                .toList();
    }

    private boolean podeAcessarAula(AulaModel aula) {

        return aula.getClasse() != null
                && usuarioAutenticadoService
                .podeAcessarClasse(
                        aula.getClasse()
                );
    }

    public AulaResponseDTO buscarPorId(Long id) {

        AulaModel aula = aulaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aula não encontrada"
                        )
                );

        validarAcessoAula(aula);

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

        validarAcessoAula(aula);

        ClasseModel classe = classeRepository.findById(dto.codclasse())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        aula.setClasse(classe);
        aula.setDiahora(dto.diahora());

        aula = aulaRepository.save(aula);

        return converterParaDTO(aula);
    }

    private void validarAcessoAula(AulaModel aula) {

        if (aula.getClasse() == null) {
            throw new SecurityException(
                    "Você não possui acesso a esta aula"
            );
        }

        usuarioAutenticadoService
                .validarAcessoAClasse(
                        aula.getClasse()
                );
    }

    @Transactional
    public void excluir(Long id) {

        AulaModel aula = aulaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aula não encontrada")
                );

        validarAcessoAula(aula);

        aulaRepository.delete(aula);
    }

    private AulaResponseDTO converterParaDTO(AulaModel aula) {

        ClasseModel classe = aula.getClasse();
        FuncionarioModel professor = classe.getProfessor();

        return new AulaResponseDTO(
                aula.getCodaula(),
                classe.getCodclasse(),
                classe.getNivel(),
                professor.getCodfuncionario(),
                professor.getUsuario().getNome(),
                aula.getDiahora()
        );
    }
}