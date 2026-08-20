package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.auth.LoginRequestDTO;
import com.example.lavnarceburgo.dto.auth.LoginResponseDTO;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        FuncionarioModel funcionario = funcionarioRepository
                .findByUsuario_Email(dto.email())
                .orElseThrow(() ->
                        new IllegalArgumentException("E-mail ou senha inválidos")
                );

        if (!passwordEncoder.matches(
                dto.senha(),
                funcionario.getSenha()
        )) {
            throw new IllegalArgumentException(
                    "E-mail ou senha inválidos"
            );
        }

        String token =
                jwtService.gerarToken(funcionario);

        return new LoginResponseDTO(
                funcionario.getCodfuncionario(),
                funcionario.getUsuario().getNome(),
                funcionario.getUsuario().getEmail(),
                funcionario.getCargo(),
                token
        );
    }
}