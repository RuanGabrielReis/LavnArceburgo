package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.anotacao.AnotacaoRequestDTO;
import com.example.lavnarceburgo.dto.anotacao.AnotacaoResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.AlunoModel;
import com.example.lavnarceburgo.model.AnotacoesModel;
import com.example.lavnarceburgo.model.AulaModel;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.enums.TipoAnotacao;
import com.example.lavnarceburgo.repository.AlunoRepository;
import com.example.lavnarceburgo.repository.AnotacoesRepository;
import com.example.lavnarceburgo.repository.AulaRepository;
import com.example.lavnarceburgo.repository.ClasseRepository;
import com.example.lavnarceburgo.service.UsuarioAutenticadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnotacoesServiceTest {

    @Mock
    private AnotacoesRepository anotacoesRepository;

    @Mock
    private ClasseRepository classeRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private AnotacoesService anotacoesService;

    private ClasseModel classe;
    private AlunoModel aluno;
    private AulaModel aula;
    private AnotacaoRequestDTO dtoTurma;

    @BeforeEach
    void prepararDados() {

        classe = new ClasseModel();
        classe.setCodclasse(1L);
        classe.setNivel("INTERMEDIARIO");

        aluno = new AlunoModel();
        aluno.setCodaluno(2L);
        aluno.setClasse(classe);

        aula = new AulaModel();
        aula.setCodaula(1L);
        aula.setClasse(classe);

        dtoTurma = new AnotacaoRequestDTO(
                TipoAnotacao.TURMA,
                "Anotação de teste",
                1L,
                null,
                null
        );
    }

    @Test
    void deveListarAnotacoesPermitidas() {

        AnotacoesModel anotacao = new AnotacoesModel();
        anotacao.setCodanotacao(1L);
        anotacao.setTipo(TipoAnotacao.TURMA);
        anotacao.setTexto("Anotação da turma");
        anotacao.setClasse(classe);

        when(anotacoesRepository.findAll())
                .thenReturn(List.of(anotacao));

        when(
                usuarioAutenticadoService
                        .podeAcessarClasse(classe)
        )
                .thenReturn(true);

        List<AnotacaoResponseDTO> resposta =
                anotacoesService.listarTodas();

        assertEquals(1, resposta.size());
        assertEquals(
                "Anotação da turma",
                resposta.get(0).texto()
        );
    }

    @Test
    void deveCadastrarAnotacaoDeTurmaComSucesso() {

        AnotacaoRequestDTO dto = new AnotacaoRequestDTO(
                TipoAnotacao.TURMA,
                "Anotação da turma",
                1L,
                null,
                null
        );

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        when(anotacoesRepository.save(any(AnotacoesModel.class)))
                .thenAnswer(invocation -> {
                    AnotacoesModel anotacao = invocation.getArgument(0);
                    anotacao.setCodanotacao(1L);
                    return anotacao;
                });

        AnotacaoResponseDTO resposta =
                anotacoesService.cadastrar(dto);

        assertNotNull(resposta);
        assertEquals(1L, resposta.codanotacao());
        assertEquals(TipoAnotacao.TURMA, resposta.tipo());
        assertEquals(1L, resposta.codclasse());
        assertNull(resposta.codaluno());
        assertNull(resposta.codaula());

        verify(anotacoesRepository)
                .save(any(AnotacoesModel.class));
    }

    @Test
    void deveCadastrarAnotacaoDeAlunoComSucesso() {

        AnotacaoRequestDTO dto = new AnotacaoRequestDTO(
                TipoAnotacao.ALUNO,
                "Anotação do aluno",
                null,
                2L,
                null
        );

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        when(anotacoesRepository.save(any(AnotacoesModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AnotacaoResponseDTO resposta =
                anotacoesService.cadastrar(dto);

        assertEquals(TipoAnotacao.ALUNO, resposta.tipo());
        assertEquals(2L, resposta.codaluno());
        assertNull(resposta.codclasse());
        assertNull(resposta.codaula());
    }

    @Test
    void deveCadastrarAnotacaoDeAulaComSucesso() {

        AnotacaoRequestDTO dto = new AnotacaoRequestDTO(
                TipoAnotacao.AULA,
                "Anotação da aula",
                null,
                null,
                1L
        );

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.of(aula));

        when(anotacoesRepository.save(any(AnotacoesModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AnotacaoResponseDTO resposta =
                anotacoesService.cadastrar(dto);

        assertEquals(TipoAnotacao.AULA, resposta.tipo());
        assertEquals(1L, resposta.codaula());
        assertNull(resposta.codclasse());
        assertNull(resposta.codaluno());
    }

    @Test
    void deveImpedirAnotacaoDeTurmaComAlunoInformado() {

        AnotacaoRequestDTO dto = new AnotacaoRequestDTO(
                TipoAnotacao.TURMA,
                "Anotação inválida",
                1L,
                2L,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> anotacoesService.cadastrar(dto)
        );

        assertEquals(
                "Anotação de turma deve estar vinculada somente a uma classe",
                exception.getMessage()
        );

        verify(anotacoesRepository, never())
                .save(any(AnotacoesModel.class));
    }

    @Test
    void deveImpedirAnotacaoDeAlunoSemAluno() {

        AnotacaoRequestDTO dto = new AnotacaoRequestDTO(
                TipoAnotacao.ALUNO,
                "Anotação inválida",
                null,
                null,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> anotacoesService.cadastrar(dto)
        );

        assertEquals(
                "O código do aluno é obrigatório para anotação de aluno",
                exception.getMessage()
        );
    }

    @Test
    void deveRetornarErroQuandoClasseNaoExiste() {

        AnotacaoRequestDTO dto = new AnotacaoRequestDTO(
                TipoAnotacao.TURMA,
                "Anotação da turma",
                999L,
                null,
                null
        );

        when(classeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> anotacoesService.cadastrar(dto)
        );
    }

    @Test
    void deveAtualizarAnotacaoComSucesso() {

        AnotacoesModel anotacao = new AnotacoesModel();
        anotacao.setCodanotacao(1L);
        anotacao.setTipo(TipoAnotacao.TURMA);
        anotacao.setTexto("Texto antigo");
        anotacao.setClasse(classe);

        AnotacaoRequestDTO dto = new AnotacaoRequestDTO(
                TipoAnotacao.ALUNO,
                "Texto atualizado",
                null,
                2L,
                null
        );

        when(anotacoesRepository.findById(1L))
                .thenReturn(Optional.of(anotacao));

        when(alunoRepository.findById(2L))
                .thenReturn(Optional.of(aluno));

        when(anotacoesRepository.save(any(AnotacoesModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AnotacaoResponseDTO resposta =
                anotacoesService.atualizar(1L, dto);

        assertEquals(TipoAnotacao.ALUNO, resposta.tipo());
        assertEquals("Texto atualizado", resposta.texto());
        assertEquals(2L, resposta.codaluno());
        assertNull(resposta.codclasse());
        assertNull(resposta.codaula());
    }

    @Test
    void deveExcluirAnotacaoComSucesso() {

        AnotacoesModel anotacao = new AnotacoesModel();
        anotacao.setCodanotacao(1L);

        when(anotacoesRepository.findById(1L))
                .thenReturn(Optional.of(anotacao));

        anotacoesService.excluir(1L);

        verify(anotacoesRepository).delete(anotacao);
    }

    @Test
    void deveRetornarErroAoExcluirAnotacaoInexistente() {

        when(anotacoesRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> anotacoesService.excluir(999L)
        );

        verify(anotacoesRepository, never())
                .delete(any(AnotacoesModel.class));
    }

    @Test
    void deveImpedirCadastroDeAnotacaoEmTurmaDeOutroProfessor() {

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        doThrow(
                new SecurityException(
                        "Você não possui acesso a esta turma"
                )
        )
                .when(usuarioAutenticadoService)
                .validarAcessoAClasse(classe);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> anotacoesService.cadastrar(dtoTurma)
        );

        assertEquals(
                "Você não possui acesso a esta turma",
                exception.getMessage()
        );

        verify(anotacoesRepository, never())
                .save(any(AnotacoesModel.class));
    }

    @Test
    void deveImpedirBuscaDeAnotacaoDeOutroProfessor() {

        AnotacoesModel anotacao = new AnotacoesModel();
        anotacao.setCodanotacao(1L);
        anotacao.setTipo(TipoAnotacao.TURMA);
        anotacao.setTexto("Anotação de outra turma");
        anotacao.setClasse(classe);

        when(anotacoesRepository.findById(1L))
                .thenReturn(Optional.of(anotacao));

        doThrow(
                new SecurityException(
                        "Você não possui acesso a esta turma"
                )
        )
                .when(usuarioAutenticadoService)
                .validarAcessoAClasse(classe);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> anotacoesService.buscarPorId(1L)
        );

        assertEquals(
                "Você não possui acesso a esta turma",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveListarAnotacaoDeOutroProfessor() {

        AnotacoesModel anotacao = new AnotacoesModel();
        anotacao.setCodanotacao(1L);
        anotacao.setTipo(TipoAnotacao.TURMA);
        anotacao.setTexto("Anotação de outra turma");
        anotacao.setClasse(classe);

        when(anotacoesRepository.findAll())
                .thenReturn(List.of(anotacao));

        when(
                usuarioAutenticadoService
                        .podeAcessarClasse(classe)
        )
                .thenReturn(false);

        List<AnotacaoResponseDTO> resposta =
                anotacoesService.listarTodas();

        assertTrue(resposta.isEmpty());
    }
}