package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.aluno.AlunoRequestDTO;
import com.example.lavnarceburgo.dto.aluno.AlunoResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.AlunoModel;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.repository.AlunoRepository;
import com.example.lavnarceburgo.repository.ClasseRepository;
import com.example.lavnarceburgo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClasseRepository classeRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public AlunoService(
            AlunoRepository alunoRepository,
            UsuarioRepository usuarioRepository,
            ClasseRepository classeRepository, UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.classeRepository = classeRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public AlunoResponseDTO cadastrar(AlunoRequestDTO dto) {

        if (usuarioRepository.existsByCpf(dto.cpf())) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse CPF"
            );
        }

        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse e-mail"
            );
        }

        ClasseModel classe = classeRepository.findById(dto.codclasse())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        UsuarioModel usuario = new UsuarioModel();

        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setTelefone(dto.telefone());
        usuario.setRg(dto.rg());
        usuario.setEndereco(dto.endereco());
        usuario.setCidade(dto.cidade());
        usuario.setEmail(dto.email());

        usuario = usuarioRepository.save(usuario);

        AlunoModel aluno = new AlunoModel();

        aluno.setUsuario(usuario);
        aluno.setClasse(classe);

        aluno = alunoRepository.save(aluno);

        return converterParaDTO(aluno);
    }

    public List<AlunoResponseDTO> listarTodos() {

        return alunoRepository.findAll()
                .stream()
                .filter(this::podeAcessarAluno)
                .map(this::converterParaDTO)
                .toList();
    }

    private boolean podeAcessarAluno(AlunoModel aluno) {

        return aluno.getClasse() != null
                && usuarioAutenticadoService
                .podeAcessarClasse(
                        aluno.getClasse()
                );
    }

    public AlunoResponseDTO buscarPorId(Long id) {

        AlunoModel aluno = alunoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aluno não encontrado"
                        )
                );

        validarAcessoAluno(aluno);

        return converterParaDTO(aluno);
    }

    private void validarAcessoAluno(AlunoModel aluno) {

        if (aluno.getClasse() == null) {
            throw new SecurityException(
                    "Você não possui acesso a este aluno"
            );
        }

        usuarioAutenticadoService
                .validarAcessoAClasse(
                        aluno.getClasse()
                );
    }

    @Transactional
    public AlunoResponseDTO atualizar(
            Long id,
            AlunoRequestDTO dto
    ) {

        AlunoModel aluno = alunoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aluno não encontrado")
                );

        ClasseModel classe = classeRepository.findById(dto.codclasse())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classe não encontrada")
                );

        UsuarioModel usuario = aluno.getUsuario();

        if (!usuario.getCpf().equals(dto.cpf())
                && usuarioRepository.existsByCpf(dto.cpf())) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse CPF"
            );
        }

        if (!usuario.getEmail().equals(dto.email())
                && usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse e-mail"
            );
        }

        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setTelefone(dto.telefone());
        usuario.setRg(dto.rg());
        usuario.setEndereco(dto.endereco());
        usuario.setCidade(dto.cidade());
        usuario.setEmail(dto.email());

        aluno.setClasse(classe);

        usuarioRepository.save(usuario);
        aluno = alunoRepository.save(aluno);

        return converterParaDTO(aluno);
    }

    @Transactional
    public void excluir(Long id) {

        AlunoModel aluno = alunoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aluno não encontrado")
                );

        UsuarioModel usuario = aluno.getUsuario();

        alunoRepository.delete(aluno);
        usuarioRepository.delete(usuario);
    }

    private AlunoResponseDTO converterParaDTO(AlunoModel aluno) {

        UsuarioModel usuario = aluno.getUsuario();
        ClasseModel classe = aluno.getClasse();

        return new AlunoResponseDTO(
                aluno.getCodaluno(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getTelefone(),
                usuario.getRg(),
                usuario.getEndereco(),
                usuario.getCidade(),
                usuario.getEmail(),
                classe != null ? classe.getCodclasse() : null,
                classe != null ? classe.getNivel() : null
        );
    }
}