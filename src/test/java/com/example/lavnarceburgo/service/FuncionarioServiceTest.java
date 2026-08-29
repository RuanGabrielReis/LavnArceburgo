package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.funcionario.FuncionarioRequestDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioResponseDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioSenhaDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.repository.ClasseRepository;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import com.example.lavnarceburgo.repository.UsuarioRepository;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioUpdateDTO;
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

    @Mock
    private ClasseRepository classeRepository;

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

        FuncionarioUpdateDTO dtoAtualizado =
                new FuncionarioUpdateDTO(
                        "Professor Atualizado",
                        "123.456.789-00",
                        "35999999999",
                        "MG123456",
                        "Rua Atualizada",
                        "Arceburgo",
                        "atualizado@lavn.com",
                        Cargo.PROFESSOR
                );

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(funcionario));

        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenReturn(usuario);

        when(funcionarioRepository.save(any(FuncionarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FuncionarioResponseDTO resposta =
                funcionarioService.atualizar(
                        1L,
                        dtoAtualizado
                );

        assertNotNull(resposta);
        assertEquals(
                "Professor Atualizado",
                resposta.nome()
        );

        verify(usuarioRepository).save(usuario);
        verify(funcionarioRepository).save(funcionario);

        verify(passwordEncoder, never())
                .encode(anyString());
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

    @Test
    void naoDevePermitirCadastrarFuncionarioMaster() {

        FuncionarioRequestDTO dtoMaster =
                new FuncionarioRequestDTO(
                        "Master Teste",
                        "999.999.999-99",
                        "35999999999",
                        "MG999999",
                        "Rua Teste, 100",
                        "Arceburgo",
                        "master@teste.com",
                        Cargo.MASTER,
                        "master123"
                );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> funcionarioService.cadastrar(dtoMaster)
        );

        assertEquals(
                "Usuário MASTER não pode ser criado por este endpoint",
                exception.getMessage()
        );

        verify(funcionarioRepository, never())
                .save(any(FuncionarioModel.class));
    }

    @Test
    void naoDevePermitirPromoverFuncionarioParaMaster() {

        funcionario.setCargo(Cargo.PROFESSOR);

        FuncionarioUpdateDTO dtoMaster =
                new FuncionarioUpdateDTO(
                        usuario.getNome(),
                        usuario.getCpf(),
                        usuario.getTelefone(),
                        usuario.getRg(),
                        usuario.getEndereco(),
                        usuario.getCidade(),
                        usuario.getEmail(),
                        Cargo.MASTER
                );

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(funcionario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> funcionarioService.atualizar(
                        1L,
                        dtoMaster
                )
        );

        assertEquals(
                "Não é permitido promover um funcionário para MASTER",
                exception.getMessage()
        );

        verify(funcionarioRepository, never())
                .save(any(FuncionarioModel.class));
    }

    @Test
    void naoDevePermitirExcluirMaster() {

        funcionario.setCargo(Cargo.MASTER);

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(funcionario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> funcionarioService.excluir(1L)
        );

        assertEquals(
                "O usuário MASTER não pode ser excluído",
                exception.getMessage()
        );

        verify(funcionarioRepository, never())
                .delete(any(FuncionarioModel.class));

        verify(usuarioRepository, never())
                .delete(any(UsuarioModel.class));
    }

    @Test
    void naoDevePermitirRebaixarMaster() {

        funcionario.setCargo(Cargo.MASTER);

        FuncionarioUpdateDTO dtoSecretaria =
                new FuncionarioUpdateDTO(
                        usuario.getNome(),
                        usuario.getCpf(),
                        usuario.getTelefone(),
                        usuario.getRg(),
                        usuario.getEndereco(),
                        usuario.getCidade(),
                        usuario.getEmail(),
                        Cargo.SECRETARIA
                );

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(funcionario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> funcionarioService.atualizar(
                        1L,
                        dtoSecretaria
                )
        );

        assertEquals(
                "O usuário MASTER não pode ter seu cargo alterado",
                exception.getMessage()
        );

        verify(funcionarioRepository, never())
                .save(any(FuncionarioModel.class));
    }

    @Test
    void deveAlterarSenhaDoFuncionarioComSucesso() {

        FuncionarioSenhaDTO dtoSenha =
                new FuncionarioSenhaDTO(
                        "novaSenha123"
                );

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(funcionario));

        when(passwordEncoder.encode("novaSenha123"))
                .thenReturn("senhaHashNova");

        funcionarioService.alterarSenha(
                1L,
                dtoSenha
        );

        assertEquals(
                "senhaHashNova",
                funcionario.getSenha()
        );

        verify(passwordEncoder)
                .encode("novaSenha123");

        verify(funcionarioRepository)
                .save(funcionario);
    }

    @Test
    void deveRetornarErroAoAlterarSenhaDeFuncionarioInexistente() {

        FuncionarioSenhaDTO dtoSenha =
                new FuncionarioSenhaDTO(
                        "novaSenha123"
                );

        when(funcionarioRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> funcionarioService.alterarSenha(
                        999L,
                        dtoSenha
                )
        );

        assertEquals(
                "Funcionário não encontrado",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(funcionarioRepository, never())
                .save(any(FuncionarioModel.class));
    }

    @Test
    void deveImpedirExclusaoDeProfessorComClassesVinculadas() {

        FuncionarioModel funcionario = new FuncionarioModel();
        funcionario.setCodfuncionario(2L);
        funcionario.setCargo(Cargo.PROFESSOR);

        when(funcionarioRepository.findById(2L))
                .thenReturn(Optional.of(funcionario));

        when(classeRepository.existsByProfessorCodfuncionario(2L))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> funcionarioService.excluir(2L)
        );

        assertEquals(
                "Não é possível excluir o professor enquanto houver classes vinculadas",
                exception.getMessage()
        );

        verify(funcionarioRepository, never())
                .delete(any(FuncionarioModel.class));
    }

    @Test
    void deveExcluirFuncionarioSemClassesVinculadas() {

        FuncionarioModel funcionario = new FuncionarioModel();
        funcionario.setCodfuncionario(2L);
        funcionario.setCargo(Cargo.PROFESSOR);

        when(funcionarioRepository.findById(2L))
                .thenReturn(Optional.of(funcionario));

        when(classeRepository.existsByProfessorCodfuncionario(2L))
                .thenReturn(false);

        funcionarioService.excluir(2L);

        verify(funcionarioRepository).delete(funcionario);
    }
}