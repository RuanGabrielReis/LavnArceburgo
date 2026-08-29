package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.presenca.PresencaRequestDTO;
import com.example.lavnarceburgo.dto.presenca.PresencaResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.*;
import com.example.lavnarceburgo.repository.AlunoRepository;
import com.example.lavnarceburgo.repository.AulaRepository;
import com.example.lavnarceburgo.repository.PresencaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresencaServiceTest {

    @Mock
    private PresencaRepository presencaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private PresencaService presencaService;

    private ClasseModel classe;
    private UsuarioModel usuario;
    private AlunoModel aluno;
    private AulaModel aula;
    private PresencaRequestDTO dto;

    @BeforeEach
    void prepararDados() {

        classe = new ClasseModel();
        classe.setCodclasse(1L);
        classe.setNivel("INTERMEDIARIO");

        usuario = new UsuarioModel();
        usuario.setCodusuario(2L);
        usuario.setNome("Aluno Teste");

        aluno = new AlunoModel();
        aluno.setCodaluno(2L);
        aluno.setUsuario(usuario);
        aluno.setClasse(classe);

        aula = new AulaModel();
        aula.setCodaula(1L);
        aula.setClasse(classe);

        dto = new PresencaRequestDTO(
                2L,
                1L,
                true,
                "Participou normalmente"
        );
    }

    @Test
    void deveImpedirCadastroDePresencaEmTurmaDeOutroProfessor() {

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.of(aula));

        doThrow(
                new SecurityException(
                        "Você não possui acesso a esta turma"
                )
        )
                .when(usuarioAutenticadoService)
                .validarAcessoAClasse(aula.getClasse());

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> presencaService.cadastrar(dto)
        );

        assertEquals(
                "Você não possui acesso a esta turma",
                exception.getMessage()
        );

        verify(presencaRepository, never())
                .save(any(PresencaModel.class));
    }

    @Test
    void naoDeveListarPresencaDeOutroProfessor() {

        PresencaModel presenca = new PresencaModel();

        presenca.setAluno(aluno);
        presenca.setAula(aula);
        presenca.setPresente(true);
        presenca.setObservacao("Presente");

        when(presencaRepository.findAll())
                .thenReturn(List.of(presenca));

        when(
                usuarioAutenticadoService
                        .podeAcessarClasse(aula.getClasse())
        )
                .thenReturn(false);

        List<PresencaResponseDTO> resposta =
                presencaService.listarTodas();

        assertTrue(resposta.isEmpty());
    }

    @Test
    void deveImpedirBuscaDePresencaDeOutroProfessor() {

        PresencaId id = new PresencaId(2L, 1L);

        PresencaModel presenca = new PresencaModel();
        presenca.setId(id);
        presenca.setAluno(aluno);
        presenca.setAula(aula);
        presenca.setPresente(true);
        presenca.setObservacao("Presente");

        when(presencaRepository.findById(id))
                .thenReturn(Optional.of(presenca));

        doThrow(
                new SecurityException(
                        "Você não possui acesso a esta turma"
                )
        )
                .when(usuarioAutenticadoService)
                .validarAcessoAClasse(aula.getClasse());

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> presencaService.buscarPorId(2L, 1L)
        );

        assertEquals(
                "Você não possui acesso a esta turma",
                exception.getMessage()
        );
    }

    @Test
    void deveImpedirExclusaoDePresencaDeOutroProfessor() {

        PresencaId id = new PresencaId(2L, 1L);

        PresencaModel presenca = new PresencaModel();
        presenca.setId(id);
        presenca.setAluno(aluno);
        presenca.setAula(aula);
        presenca.setPresente(true);

        when(presencaRepository.findById(id))
                .thenReturn(Optional.of(presenca));

        doThrow(
                new SecurityException(
                        "Você não possui acesso a esta turma"
                )
        )
                .when(usuarioAutenticadoService)
                .validarAcessoAClasse(aula.getClasse());

        assertThrows(
                SecurityException.class,
                () -> presencaService.excluir(2L, 1L)
        );

        verify(presencaRepository, never())
                .delete(any(PresencaModel.class));
    }

    @Test
    void deveCadastrarPresencaComSucesso() {

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.of(aula));

        when(presencaRepository.existsById(any(PresencaId.class)))
                .thenReturn(false);

        when(presencaRepository.save(any(PresencaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PresencaResponseDTO resposta =
                presencaService.cadastrar(dto);

        assertNotNull(resposta);
        assertEquals(2L, resposta.codaluno());
        assertEquals(1L, resposta.codaula());
        assertEquals(1L, resposta.codclasse());
        assertTrue(resposta.presente());

        verify(presencaRepository).save(any(PresencaModel.class));
    }

    @Test
    void deveImpedirPresencaDeAlunoEmOutraClasse() {

        ClasseModel outraClasse = new ClasseModel();
        outraClasse.setCodclasse(2L);
        outraClasse.setNivel("AVANCADO");

        aula.setClasse(outraClasse);

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.of(aula));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> presencaService.cadastrar(dto)
        );

        assertEquals(
                "O aluno não pertence à mesma classe da aula",
                exception.getMessage()
        );

        verify(presencaRepository, never())
                .save(any(PresencaModel.class));
    }

    @Test
    void deveImpedirPresencaDuplicada() {

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.of(aula));

        when(presencaRepository.existsById(any(PresencaId.class)))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> presencaService.cadastrar(dto)
        );

        assertEquals(
                "Já existe presença registrada para este aluno nesta aula",
                exception.getMessage()
        );

        verify(presencaRepository, never())
                .save(any(PresencaModel.class));
    }

    @Test
    void deveRetornarErroQuandoAlunoNaoExiste() {

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> presencaService.cadastrar(dto)
        );

        verify(aulaRepository, never())
                .findById(anyLong());
    }

    @Test
    void deveRetornarErroQuandoAulaNaoExiste() {

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> presencaService.cadastrar(dto)
        );
    }
}