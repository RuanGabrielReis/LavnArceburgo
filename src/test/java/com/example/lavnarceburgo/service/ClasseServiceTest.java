package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.classe.ClasseRequestDTO;
import com.example.lavnarceburgo.dto.classe.ClasseResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.repository.ClasseRepository;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClasseServiceTest {

    @Mock
    private ClasseRepository classeRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private ClasseService classeService;

    private FuncionarioModel professor;
    private ClasseModel classe;
    private ClasseRequestDTO dto;

    @BeforeEach
    void prepararDados() {

        UsuarioModel usuario = new UsuarioModel();
        usuario.setCodusuario(1L);
        usuario.setNome("Professor Teste");

        professor = new FuncionarioModel();
        professor.setCodfuncionario(1L);
        professor.setUsuario(usuario);
        professor.setCargo(Cargo.PROFESSOR);

        classe = new ClasseModel();
        classe.setCodclasse(1L);
        classe.setNivel("INTERMEDIARIO");
        classe.setProfessor(professor);

        dto = new ClasseRequestDTO(
                "INTERMEDIARIO",
                1L
        );
    }

    @Test
    void deveCadastrarClasseComSucesso() {

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(professor));

        when(classeRepository.save(any(ClasseModel.class)))
                .thenAnswer(invocation -> {
                    ClasseModel salva = invocation.getArgument(0);
                    salva.setCodclasse(1L);
                    return salva;
                });

        ClasseResponseDTO resposta =
                classeService.cadastrar(dto);

        assertNotNull(resposta);
        assertEquals(1L, resposta.codclasse());
        assertEquals("INTERMEDIARIO", resposta.nivel());
        assertEquals(1L, resposta.codprofessor());
        assertEquals("Professor Teste", resposta.nomeProfessor());

        verify(classeRepository)
                .save(any(ClasseModel.class));
    }

    @Test
    void deveImpedirCadastroComFuncionarioQueNaoEhProfessor() {

        professor.setCargo(Cargo.SECRETARIA);

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(professor));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> classeService.cadastrar(dto)
        );

        assertEquals(
                "O funcionário informado não possui cargo de professor",
                exception.getMessage()
        );

        verify(classeRepository, never())
                .save(any(ClasseModel.class));
    }

    @Test
    void deveRetornarErroQuandoProfessorNaoExiste() {

        when(funcionarioRepository.findById(999L))
                .thenReturn(Optional.empty());

        ClasseRequestDTO dtoInvalido =
                new ClasseRequestDTO(
                        "INTERMEDIARIO",
                        999L
                );

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> classeService.cadastrar(dtoInvalido)
        );

        assertEquals(
                "Professor não encontrado",
                exception.getMessage()
        );
    }

    @Test
    void deveBuscarClassePorId() {

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        ClasseResponseDTO resposta =
                classeService.buscarPorId(1L);

        assertEquals(1L, resposta.codclasse());
        assertEquals("INTERMEDIARIO", resposta.nivel());
        assertEquals(1L, resposta.codprofessor());
    }

    @Test
    void deveRetornarErroAoBuscarClasseInexistente() {

        when(classeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> classeService.buscarPorId(999L)
        );
    }

    @Test
    void deveAtualizarClasseComSucesso() {

        ClasseRequestDTO dtoAtualizado =
                new ClasseRequestDTO(
                        "AVANCADO",
                        1L
                );

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(professor));

        when(classeRepository.save(any(ClasseModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClasseResponseDTO resposta =
                classeService.atualizar(
                        1L,
                        dtoAtualizado
                );

        assertEquals("AVANCADO", resposta.nivel());
        assertEquals(1L, resposta.codprofessor());
    }

    @Test
    void deveExcluirClasseComSucesso() {

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        classeService.excluir(1L);

        verify(classeRepository).delete(classe);
    }

    @Test
    void deveRetornarErroAoExcluirClasseInexistente() {

        when(classeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> classeService.excluir(999L)
        );

        verify(classeRepository, never())
                .delete(any(ClasseModel.class));
    }
}