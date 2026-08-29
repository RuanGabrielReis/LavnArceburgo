package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.funcionario.FuncionarioRequestDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.repository.ClasseRepository;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import com.example.lavnarceburgo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioUpdateDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioSenhaDTO;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClasseRepository classeRepository;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder, ClasseRepository classeRepository
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.classeRepository = classeRepository;
    }

    @Transactional
    public FuncionarioResponseDTO cadastrar(FuncionarioRequestDTO dto) {

        if (dto.cargo() == Cargo.MASTER) {
            throw new IllegalArgumentException(
                    "Usuário MASTER não pode ser criado por este endpoint"
            );
        }

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

        UsuarioModel usuario = new UsuarioModel();

        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setTelefone(dto.telefone());
        usuario.setRg(dto.rg());
        usuario.setEndereco(dto.endereco());
        usuario.setCidade(dto.cidade());
        usuario.setEmail(dto.email());

        usuario = usuarioRepository.save(usuario);

        FuncionarioModel funcionario = new FuncionarioModel();

        funcionario.setUsuario(usuario);
        funcionario.setCargo(dto.cargo());
        funcionario.setSenha(
                passwordEncoder.encode(dto.senha())
        );

        funcionario = funcionarioRepository.save(funcionario);

        return converterParaDTO(funcionario);
    }

    public List<FuncionarioResponseDTO> listarTodos() {
        return funcionarioRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public FuncionarioResponseDTO buscarPorId(Long id) {

        FuncionarioModel funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Funcionário não encontrado")
                );

        return converterParaDTO(funcionario);
    }

    @Transactional
    public FuncionarioResponseDTO atualizar(
            Long id,
            FuncionarioUpdateDTO dto
    ) {

        FuncionarioModel funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Funcionário não encontrado")
                );

        if (funcionario.getCargo() == Cargo.MASTER
                && dto.cargo() != Cargo.MASTER) {

            throw new IllegalArgumentException(
                    "O usuário MASTER não pode ter seu cargo alterado"
            );
        }

        if (funcionario.getCargo() != Cargo.MASTER
                && dto.cargo() == Cargo.MASTER) {

            throw new IllegalArgumentException(
                    "Não é permitido promover um funcionário para MASTER"
            );
        }

        UsuarioModel usuario = funcionario.getUsuario();

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

        if (dto.cargo() != Cargo.PROFESSOR
                && classeRepository.existsByProfessorCodfuncionario(id)) {

            throw new IllegalArgumentException(
                    "Não é possível alterar o cargo do professor enquanto houver classes vinculadas"
            );
        }

        funcionario.setCargo(dto.cargo());

        usuarioRepository.save(usuario);
        funcionario = funcionarioRepository.save(funcionario);

        return converterParaDTO(funcionario);
    }

    @Transactional
    public void excluir(Long id) {

        FuncionarioModel funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Funcionário não encontrado")
                );

        if (funcionario.getCargo() == Cargo.MASTER) {
            throw new IllegalArgumentException(
                    "O usuário MASTER não pode ser excluído"
            );
        }

        UsuarioModel usuario = funcionario.getUsuario();

        if (classeRepository.existsByProfessorCodfuncionario(id)) {
            throw new IllegalArgumentException(
                    "Não é possível excluir o professor enquanto houver classes vinculadas"
            );
        }

        funcionarioRepository.delete(funcionario);
        usuarioRepository.delete(usuario);
    }

    private FuncionarioResponseDTO converterParaDTO(
            FuncionarioModel funcionario
    ) {

        UsuarioModel usuario = funcionario.getUsuario();

        return new FuncionarioResponseDTO(
                funcionario.getCodfuncionario(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getTelefone(),
                usuario.getRg(),
                usuario.getEndereco(),
                usuario.getCidade(),
                usuario.getEmail(),
                funcionario.getCargo()
        );
    }

    @Transactional
    public void alterarSenha(
            Long id,
            FuncionarioSenhaDTO dto
    ) {

        FuncionarioModel funcionario =
                funcionarioRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Funcionário não encontrado"
                                )
                        );

        funcionario.setSenha(
                passwordEncoder.encode(dto.senha())
        );

        funcionarioRepository.save(funcionario);
    }
}