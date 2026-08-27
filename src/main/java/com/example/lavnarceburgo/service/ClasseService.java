package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.classe.ClasseRequestDTO;
import com.example.lavnarceburgo.dto.classe.ClasseResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.repository.ClasseRepository;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClasseService {

    private final ClasseRepository classeRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ClasseService(
            ClasseRepository classeRepository,
            FuncionarioRepository funcionarioRepository, UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.classeRepository = classeRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public ClasseResponseDTO cadastrar(ClasseRequestDTO dto) {

        FuncionarioModel professor = buscarProfessor(dto.codprofessor());

        ClasseModel classe = new ClasseModel();

        classe.setNivel(dto.nivel());
        classe.setProfessor(professor);

        classe = classeRepository.save(classe);

        return converterParaDTO(classe);
    }

    public List<ClasseResponseDTO> listarTodas() {

        return classeRepository.findAll()
                .stream()
                .filter(
                        usuarioAutenticadoService::podeAcessarClasse
                )
                .map(this::converterParaDTO)
                .toList();
    }

    public ClasseResponseDTO buscarPorId(Long id) {

        ClasseModel classe = classeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Classe não encontrada"
                        )
                );

        usuarioAutenticadoService
                .validarAcessoAClasse(classe);

        return converterParaDTO(classe);
    }

    @Transactional
    public ClasseResponseDTO atualizar(
            Long id,
            ClasseRequestDTO dto
    ) {

        ClasseModel classe = classeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        FuncionarioModel professor = buscarProfessor(dto.codprofessor());

        classe.setNivel(dto.nivel());
        classe.setProfessor(professor);

        classe = classeRepository.save(classe);

        return converterParaDTO(classe);
    }

    @Transactional
    public void excluir(Long id) {

        ClasseModel classe = classeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        classeRepository.delete(classe);
    }

    private FuncionarioModel buscarProfessor(Long id) {

        FuncionarioModel professor = funcionarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado")
                );

        if (professor.getCargo() != Cargo.PROFESSOR) {
            throw new IllegalArgumentException(
                    "O funcionário informado não possui cargo de professor"
            );
        }

        return professor;
    }

    private ClasseResponseDTO converterParaDTO(ClasseModel classe) {

        FuncionarioModel professor = classe.getProfessor();

        return new ClasseResponseDTO(
                classe.getCodclasse(),
                classe.getNivel(),
                professor != null ? professor.getCodfuncionario() : null,
                professor != null
                        ? professor.getUsuario().getNome()
                        : null
        );
    }
}