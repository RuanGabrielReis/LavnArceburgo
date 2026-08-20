package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.funcionario.FuncionarioRequestDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import com.example.lavnarceburgo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private FuncionarioRequestDTO dto;
    private UsuarioModel usuario;
    private FuncionarioModel funcionario;

    @BeforeEach
    void prepararDados() {

        dto = new FuncionarioRequestDTO(
                "Professor Teste",
                "111.111.111-11",
                "35999999999",
                "MG111111",
                "Rua Teste, 100",
                "Arceburgo",
                "professor@teste.com",
                Cargo.PROFESSOR,
                new BigDecimal("3000.00"),
                "123456"
        );

        usuario = new UsuarioModel();
        usuario.setCodusuario(1L);
        usuario.setNome("Professor Teste");
        usuario.setCpf("111.111.111-11");
        usuario.setTelefone("35999999999");
        usuario.setRg("MG111111");
        usuario.setEndereco("Rua Teste, 100");
        usuario.setCidade("Arceburgo");
        usuario.setEmail("professor@teste.com");

        funcionario = new FuncionarioModel();
        funcionario.setCodfuncionario(1L);
        funcionario.setUsuario(usuario);
        funcionario.setCargo(Cargo.PROFESSOR);
        funcionario.setSalario(new BigDecimal("3000.00"));
    }

    @Test
    void deveCadastrarFuncionarioComSucesso() {

        when(usuarioRepository.existsByCpf(dto.cpf()))
                .thenReturn(false);

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(false);

        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenReturn(usuario);

        when(passwordEncoder.encode("123456"))
                .thenReturn("$2a$10$senhaHashDeTeste");

        when(funcionarioRepository.save(any(FuncionarioModel.class)))
                .thenAnswer(invocation -> {
                    FuncionarioModel salvo = invocation.getArgument(0);
                    salvo.setCodfuncionario(1L);
                    salvo.setUsuario(usuario);
                    return salvo;
                });

        FuncionarioResponseDTO resposta =
                funcionarioService.cadastrar(dto);

        assertNotNull(resposta);
        assertEquals(1L, resposta.codfuncionario());
        assertEquals("Professor Teste", resposta.nome());
        assertEquals(Cargo.PROFESSOR, resposta.cargo());
        assertEquals(new BigDecimal("3000.00"), resposta.salario());

        verify(usuarioRepository).save(any(UsuarioModel.class));
        verify(funcionarioRepository).save(any(FuncionarioModel.class));
    }

    @Test
    void deveImpedirCadastroComCpfDuplicado() {

        when(usuarioRepository.existsByCpf(dto.cpf()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> funcionarioService.cadastrar(dto)
        );

        assertEquals(
                "Já existe um usuário cadastrado com esse CPF",
                exception.getMessage()
        );

        verify(usuarioRepository, never())
                .save(any(UsuarioModel.class));

        verify(funcionarioRepository, never())
                .save(any(FuncionarioModel.class));
    }

    @Test
    void deveImpedirCadastroComEmailDuplicado() {

        when(usuarioRepository.existsByCpf(dto.cpf()))
                .thenReturn(false);

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> funcionarioService.cadastrar(dto)
        );

        assertEquals(
                "Já existe um usuário cadastrado com esse e-mail",
                exception.getMessage()
        );

        verify(funcionarioRepository, never())
                .save(any(FuncionarioModel.class));
    }

    @Test
    void deveBuscarFuncionarioPorId() {

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(funcionario));

        FuncionarioResponseDTO resposta =
                funcionarioService.buscarPorId(1L);

        assertEquals(1L, resposta.codfuncionario());
        assertEquals("Professor Teste", resposta.nome());
        assertEquals(Cargo.PROFESSOR, resposta.cargo());
    }

    @Test
    void deveRetornarErroAoBuscarFuncionarioInexistente() {

        when(funcionarioRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> funcionarioService.buscarPorId(999L)
        );

        assertEquals(
                "Funcionário não encontrado",
                exception.getMessage()
        );
    }

    @Test
    void deveAtualizarFuncionarioComSucesso() {

        FuncionarioRequestDTO dtoAtualizado =
                new FuncionarioRequestDTO(
                        "Professor Atualizado",
                        "111.111.111-11",
                        "35988888888",
                        "MG111111",
                        "Rua Nova, 200",
                        "Arceburgo",
                        "professor@teste.com",
                        Cargo.PROFESSOR,
                        new BigDecimal("3500.00"),
                        "123456"
                );

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(funcionario));

        when(passwordEncoder.encode("123456"))
                .thenReturn("$2a$10$senhaHashDeTeste");

        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenReturn(usuario);

        when(funcionarioRepository.save(any(FuncionarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FuncionarioResponseDTO resposta =
                funcionarioService.atualizar(
                        1L,
                        dtoAtualizado
                );

        assertEquals(
                "Professor Atualizado",
                resposta.nome()
        );

        assertEquals(
                new BigDecimal("3500.00"),
                resposta.salario()
        );

        verify(usuarioRepository).save(usuario);
        verify(funcionarioRepository).save(funcionario);
    }

    @Test
    void deveExcluirFuncionarioComSucesso() {

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(funcionario));

        funcionarioService.excluir(1L);

        verify(funcionarioRepository)
                .delete(funcionario);

        verify(usuarioRepository)
                .delete(usuario);
    }

    @Test
    void deveRetornarErroAoExcluirFuncionarioInexistente() {

        when(funcionarioRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> funcionarioService.excluir(999L)
        );

        verify(funcionarioRepository, never())
                .delete(any(FuncionarioModel.class));

        verify(usuarioRepository, never())
                .delete(any(UsuarioModel.class));
    }
}