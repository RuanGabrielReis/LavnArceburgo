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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClasseRepository classeRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private AlunoService alunoService;

    private AlunoRequestDTO dto;
    private ClasseModel classe;
    private UsuarioModel usuario;
    private AlunoModel aluno;

    @BeforeEach
    void prepararDados() {

        dto = new AlunoRequestDTO(
                "Aluno Teste",
                "123.456.789-00",
                "35999999999",
                "MG123456",
                "Rua Teste, 100",
                "Arceburgo",
                "aluno@teste.com",
                1L
        );

        classe = new ClasseModel();
        classe.setCodclasse(1L);
        classe.setNivel("INTERMEDIARIO");

        usuario = new UsuarioModel();
        usuario.setCodusuario(2L);
        usuario.setNome("Aluno Teste");
        usuario.setCpf("123.456.789-00");
        usuario.setTelefone("35999999999");
        usuario.setRg("MG123456");
        usuario.setEndereco("Rua Teste, 100");
        usuario.setCidade("Arceburgo");
        usuario.setEmail("aluno@teste.com");

        aluno = new AlunoModel();
        aluno.setUsuario(usuario);
        aluno.setClasse(classe);
    }

    @Test
    void deveCadastrarAlunoComSucesso() {

        when(usuarioRepository.existsByCpf(dto.cpf()))
                .thenReturn(false);

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(false);

        when(classeRepository.findById(dto.codclasse()))
                .thenReturn(Optional.of(classe));

        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenReturn(usuario);

        when(alunoRepository.save(any(AlunoModel.class)))
                .thenAnswer(invocation -> {
                    AlunoModel alunoSalvo = invocation.getArgument(0);
                    alunoSalvo.setUsuario(usuario);
                    return alunoSalvo;
                });

        AlunoResponseDTO resposta = alunoService.cadastrar(dto);

        assertNotNull(resposta);
        assertEquals("Aluno Teste", resposta.nome());
        assertEquals("123.456.789-00", resposta.cpf());
        assertEquals(1L, resposta.codclasse());
        assertEquals("INTERMEDIARIO", resposta.nivel());

        verify(usuarioRepository).save(any(UsuarioModel.class));
        verify(alunoRepository).save(any(AlunoModel.class));
    }

    @Test
    void deveImpedirCadastroComCpfDuplicado() {

        when(usuarioRepository.existsByCpf(dto.cpf()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> alunoService.cadastrar(dto)
        );

        assertEquals(
                "Já existe um usuário cadastrado com esse CPF",
                exception.getMessage()
        );

        verify(usuarioRepository, never())
                .save(any(UsuarioModel.class));

        verify(alunoRepository, never())
                .save(any(AlunoModel.class));
    }

    @Test
    void deveImpedirCadastroComEmailDuplicado() {

        when(usuarioRepository.existsByCpf(dto.cpf()))
                .thenReturn(false);

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> alunoService.cadastrar(dto)
        );

        assertEquals(
                "Já existe um usuário cadastrado com esse e-mail",
                exception.getMessage()
        );

        verify(usuarioRepository, never())
                .save(any(UsuarioModel.class));

        verify(alunoRepository, never())
                .save(any(AlunoModel.class));
    }

    @Test
    void deveImpedirCadastroComClasseInexistente() {

        when(usuarioRepository.existsByCpf(dto.cpf()))
                .thenReturn(false);

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(false);

        when(classeRepository.findById(dto.codclasse()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> alunoService.cadastrar(dto)
        );

        verify(usuarioRepository, never())
                .save(any(UsuarioModel.class));

        verify(alunoRepository, never())
                .save(any(AlunoModel.class));
    }

    @Test
    void deveListarAlunoDaTurmaDoProfessor() {

        when(alunoRepository.findAll())
                .thenReturn(List.of(aluno));

        when(
                usuarioAutenticadoService
                        .podeAcessarClasse(classe)
        )
                .thenReturn(true);

        List<AlunoResponseDTO> resposta =
                alunoService.listarTodos();

        assertEquals(1, resposta.size());
        assertEquals("Aluno Teste", resposta.get(0).nome());
        assertEquals(1L, resposta.get(0).codclasse());
    }

    @Test
    void naoDeveListarAlunoDeOutraTurma() {

        when(alunoRepository.findAll())
                .thenReturn(List.of(aluno));

        when(
                usuarioAutenticadoService
                        .podeAcessarClasse(classe)
        )
                .thenReturn(false);

        List<AlunoResponseDTO> resposta =
                alunoService.listarTodos();

        assertTrue(resposta.isEmpty());
    }

    @Test
    void deveImpedirBuscaDeAlunoDeOutraTurma() {

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        doThrow(
                new SecurityException(
                        "Você não possui acesso a esta turma"
                )
        )
                .when(usuarioAutenticadoService)
                .validarAcessoAClasse(classe);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> alunoService.buscarPorId(2L)
        );

        assertEquals(
                "Você não possui acesso a esta turma",
                exception.getMessage()
        );
    }

    @Test
    void deveRetornarErroAoBuscarAlunoInexistente() {

        when(alunoRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> alunoService.buscarPorId(999L)
        );

        assertEquals(
                "Aluno não encontrado",
                exception.getMessage()
        );
    }

    @Test
    void deveAtualizarAlunoComSucesso() {

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        when(classeRepository.findById(dto.codclasse()))
                .thenReturn(Optional.of(classe));

        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenReturn(usuario);

        when(alunoRepository.save(any(AlunoModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AlunoResponseDTO resposta =
                alunoService.atualizar(2L, dto);

        assertNotNull(resposta);
        assertEquals("Aluno Teste", resposta.nome());
        assertEquals(1L, resposta.codclasse());

        verify(usuarioRepository).save(usuario);
        verify(alunoRepository).save(aluno);
    }

    @Test
    void deveExcluirAlunoComSucesso() {

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        alunoService.excluir(2L);

        verify(alunoRepository).delete(aluno);
        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void deveRetornarErroAoExcluirAlunoInexistente() {

        when(alunoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> alunoService.excluir(999L)
        );

        verify(alunoRepository, never())
                .delete(any(AlunoModel.class));

        verify(usuarioRepository, never())
                .delete(any(UsuarioModel.class));
    }
}