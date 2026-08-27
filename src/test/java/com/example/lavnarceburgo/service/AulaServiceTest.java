package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.aula.AulaRequestDTO;
import com.example.lavnarceburgo.dto.aula.AulaResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.AulaModel;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.repository.AulaRepository;
import com.example.lavnarceburgo.repository.ClasseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AulaServiceTest {

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private ClasseRepository classeRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private AulaService aulaService;

    private ClasseModel classe;
    private AulaModel aula;
    private AulaRequestDTO dto;

    @BeforeEach
    void prepararDados() {

        classe = new ClasseModel();
        classe.setCodclasse(1L);
        classe.setNivel("INTERMEDIARIO");

        aula = new AulaModel();
        aula.setCodaula(1L);
        aula.setClasse(classe);
        aula.setDiahora(
                LocalDateTime.of(2026, 8, 20, 19, 0)
        );

        dto = new AulaRequestDTO(
                1L,
                LocalDateTime.of(2026, 8, 20, 19, 0)
        );
    }

    @Test
    void deveCadastrarAulaComSucesso() {

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        when(aulaRepository.save(any(AulaModel.class)))
                .thenAnswer(invocation -> {
                    AulaModel salva = invocation.getArgument(0);
                    salva.setCodaula(1L);
                    return salva;
                });

        AulaResponseDTO resposta =
                aulaService.cadastrar(dto);

        assertNotNull(resposta);
        assertEquals(1L, resposta.codaula());
        assertEquals(1L, resposta.codclasse());
        assertEquals("INTERMEDIARIO", resposta.nivel());
        assertEquals(
                LocalDateTime.of(2026, 8, 20, 19, 0),
                resposta.diahora()
        );

        verify(aulaRepository)
                .save(any(AulaModel.class));
    }

    @Test
    void deveRetornarErroQuandoClasseNaoExiste() {

        when(classeRepository.findById(999L))
                .thenReturn(Optional.empty());

        AulaRequestDTO dtoInvalido =
                new AulaRequestDTO(
                        999L,
                        LocalDateTime.of(2026, 8, 20, 19, 0)
                );

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> aulaService.cadastrar(dtoInvalido)
        );

        assertEquals(
                "Classe não encontrada",
                exception.getMessage()
        );

        verify(aulaRepository, never())
                .save(any(AulaModel.class));
    }

    @Test
    void deveBuscarAulaPorId() {

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.of(aula));

        AulaResponseDTO resposta =
                aulaService.buscarPorId(1L);

        assertEquals(1L, resposta.codaula());
        assertEquals(1L, resposta.codclasse());
    }

    @Test
    void deveRetornarErroAoBuscarAulaInexistente() {

        when(aulaRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> aulaService.buscarPorId(999L)
        );
    }

    @Test
    void deveAtualizarAulaComSucesso() {

        AulaRequestDTO dtoAtualizado =
                new AulaRequestDTO(
                        1L,
                        LocalDateTime.of(2026, 8, 21, 20, 0)
                );

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.of(aula));

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        when(aulaRepository.save(any(AulaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AulaResponseDTO resposta =
                aulaService.atualizar(
                        1L,
                        dtoAtualizado
                );

        assertEquals(
                LocalDateTime.of(2026, 8, 21, 20, 0),
                resposta.diahora()
        );

        assertEquals(1L, resposta.codclasse());
    }

    @Test
    void deveExcluirAulaComSucesso() {

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.of(aula));

        aulaService.excluir(1L);

        verify(aulaRepository)
                .delete(aula);
    }

    @Test
    void deveRetornarErroAoExcluirAulaInexistente() {

        when(aulaRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> aulaService.excluir(999L)
        );

        verify(aulaRepository, never())
                .delete(any(AulaModel.class));
    }

    @Test
    void deveListarAulaDaTurmaDoProfessor() {

        when(aulaRepository.findAll())
                .thenReturn(List.of(aula));

        when(
                usuarioAutenticadoService
                        .podeAcessarClasse(classe)
        )
                .thenReturn(true);

        List<AulaResponseDTO> resposta =
                aulaService.listarTodos();

        assertEquals(1, resposta.size());
    }

    @Test
    void naoDeveListarAulaDeOutroProfessor() {

        when(aulaRepository.findAll())
                .thenReturn(List.of(aula));

        when(
                usuarioAutenticadoService
                        .podeAcessarClasse(classe)
        )
                .thenReturn(false);

        List<AulaResponseDTO> resposta =
                aulaService.listarTodos();

        assertTrue(resposta.isEmpty());
    }

    @Test
    void deveImpedirBuscaDeAulaDeOutroProfessor() {

        when(aulaRepository.findById(1L))
                .thenReturn(Optional.of(aula));

        doThrow(
                new SecurityException(
                        "Você não possui acesso a esta turma"
                )
        )
                .when(usuarioAutenticadoService)
                .validarAcessoAClasse(classe);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> aulaService.buscarPorId(1L)
        );

        assertEquals(
                "Você não possui acesso a esta turma",
                exception.getMessage()
        );
    }
}