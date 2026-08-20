package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.auth.LoginRequestDTO;
import com.example.lavnarceburgo.dto.auth.LoginResponseDTO;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private FuncionarioModel funcionario;

    @BeforeEach
    void prepararDados() {

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Professor Teste");
        usuario.setEmail("professor@lavn.com");

        funcionario = new FuncionarioModel();
        funcionario.setCodfuncionario(1L);
        funcionario.setUsuario(usuario);
        funcionario.setCargo(Cargo.PROFESSOR);
        funcionario.setSenha("$2a$10$hashDeTeste");
    }

    @Test
    void deveRealizarLoginComSucesso() {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "professor@lavn.com",
                        "123456"
                );

        when(funcionarioRepository.findByUsuario_Email(
                "professor@lavn.com"
        ))
                .thenReturn(Optional.of(funcionario));

        when(passwordEncoder.matches(
                "123456",
                "$2a$10$hashDeTeste"
        ))
                .thenReturn(true);

        when(jwtService.gerarToken(funcionario))
                .thenReturn("jwt-token-teste");

        LoginResponseDTO resposta =
                authService.login(dto);

        assertNotNull(resposta);

        assertEquals(
                1L,
                resposta.codfuncionario()
        );

        assertEquals(
                "Professor Teste",
                resposta.nome()
        );

        assertEquals(
                "professor@lavn.com",
                resposta.email()
        );

        assertEquals(
                Cargo.PROFESSOR,
                resposta.cargo()
        );

        assertEquals(
                "jwt-token-teste",
                resposta.token()
        );

        verify(passwordEncoder).matches(
                "123456",
                "$2a$10$hashDeTeste"
        );

        verify(jwtService)
                .gerarToken(funcionario);
    }

    @Test
    void deveRejeitarSenhaIncorreta() {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "professor@lavn.com",
                        "senhaErrada"
                );

        when(funcionarioRepository.findByUsuario_Email(
                "professor@lavn.com"
        ))
                .thenReturn(Optional.of(funcionario));

        when(passwordEncoder.matches(
                "senhaErrada",
                "$2a$10$hashDeTeste"
        ))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.login(dto)
                );

        assertEquals(
                "E-mail ou senha inválidos",
                exception.getMessage()
        );

        verify(jwtService, never())
                .gerarToken(any(FuncionarioModel.class));
    }

    @Test
    void deveRejeitarEmailInexistente() {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "naoexiste@lavn.com",
                        "123456"
                );

        when(funcionarioRepository.findByUsuario_Email(
                "naoexiste@lavn.com"
        ))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.login(dto)
                );

        assertEquals(
                "E-mail ou senha inválidos",
                exception.getMessage()
        );

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }
}