package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.aluno.AlunoRequestDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioRequestDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioResponseDTO;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import com.example.lavnarceburgo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public FuncionarioResponseDTO cadastrar(FuncionarioRequestDTO dto) {

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
        funcionario.setSalario(dto.salario());

        funcionario = funcionarioRepository.save(funcionario);

        return converterParaDTO(funcionario);
    }

    public List<FuncionarioResponseDTO> listarTodos() {
        return funcionarioRepository
                .findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public FuncionarioResponseDTO buscarPorId(Long id) {

        FuncionarioModel funcionario = funcionarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Funcionario não encontrado")
                );

        return converterParaDTO(funcionario);
    }

@Transactional
public FuncionarioResponseDTO atualizar(Long id, FuncionarioRequestDTO dto) {
    FuncionarioModel funcionario = funcionarioRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Funcionario não encontrado")
            );

    UsuarioModel usuario = funcionario.getUsuario();
    if (!usuario.getCpf().equals(dto.cpf()) && usuarioRepository.existsByCpf(dto.cpf())) {
        throw new IllegalArgumentException("Esse cpf ja apresenta usuario cadastrado");
    }

    if (!usuario.getEmail().equals(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
        throw new IllegalArgumentException("Esse email ja apresenta um usuario cadastrado");
    }
    usuario.setNome(dto.nome());
    usuario.setNome(dto.nome());
    usuario.setCpf(dto.cpf());
    usuario.setTelefone(dto.telefone());
    usuario.setRg(dto.rg());
    usuario.setEndereco(dto.endereco());
    usuario.setCidade(dto.cidade());
    usuario.setEmail(dto.email());

    funcionario.setCargo(dto.cargo());
    funcionario.setSalario(dto.salario());

    usuarioRepository.save(usuario);
    funcionario = funcionarioRepository.save(funcionario);

    return converterParaDTO(funcionario);
}
    @Transactional
    public void excluir(Long id) {

        FuncionarioModel funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Funcionario não encontrado")
                );

        UsuarioModel usuario = funcionario.getUsuario();

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
                funcionario.getCargo(),
                funcionario.getSalario()
        );
    }
}