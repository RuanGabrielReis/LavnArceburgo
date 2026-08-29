package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.dto.horario.HorarioRequestDTO;
import com.example.lavnarceburgo.dto.horario.HorarioResponseDTO;
import com.example.lavnarceburgo.exception.ResourceNotFoundException;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.HorarioModel;
import com.example.lavnarceburgo.repository.ClasseRepository;
import com.example.lavnarceburgo.repository.HorarioRepository;
import com.example.lavnarceburgo.model.FuncionarioModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioServiceTest {

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private ClasseRepository classeRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private HorarioService horarioService;

    private ClasseModel classe;
    private HorarioModel horario;
    private HorarioRequestDTO dto;

    @BeforeEach
    void prepararDados() {

        classe = new ClasseModel();
        classe.setCodclasse(1L);
        classe.setNivel("INTERMEDIARIO");

        horario = new HorarioModel();
        horario.setCodhorario(1L);
        horario.setDuracaoaula(90);
        horario.setClasse(classe);
        horario.setSala("Sala 1");
        horario.setDiaSemana(DayOfWeek.MONDAY);
        horario.setHoraInicio(LocalTime.of(19, 0));

        dto = new HorarioRequestDTO(
                90,
                1L,
                "Sala 1",
                DayOfWeek.MONDAY,
                LocalTime.of(19, 0)
        );
    }

    @Test
    void deveCadastrarHorarioComSucesso() {

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        when(horarioRepository.save(any(HorarioModel.class)))
                .thenAnswer(invocation -> {
                    HorarioModel salvo = invocation.getArgument(0);
                    salvo.setCodhorario(1L);
                    return salvo;
                });

        HorarioResponseDTO resposta =
                horarioService.cadastrar(dto);

        assertNotNull(resposta);
        assertEquals(1L, resposta.codhorario());
        assertEquals(1L, resposta.codclasse());
        assertEquals("INTERMEDIARIO", resposta.nivel());
        assertEquals("Sala 1", resposta.sala());
        assertEquals(DayOfWeek.MONDAY, resposta.diaSemana());
        assertEquals(LocalTime.of(19, 0), resposta.horaInicio());

        verify(horarioRepository)
                .save(any(HorarioModel.class));
    }

    @Test
    void deveRetornarErroQuandoClasseNaoExiste() {

        when(classeRepository.findById(999L))
                .thenReturn(Optional.empty());

        HorarioRequestDTO dtoInvalido =
                new HorarioRequestDTO(
                        90,
                        999L,
                        "Sala 1",
                        DayOfWeek.MONDAY,
                        LocalTime.of(19, 0)
                );

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> horarioService.cadastrar(dtoInvalido)
        );

        assertEquals(
                "Classe não encontrada",
                exception.getMessage()
        );

        verify(horarioRepository, never())
                .save(any(HorarioModel.class));
    }

    @Test
    void deveBuscarHorarioPorId() {

        when(horarioRepository.findById(1L))
                .thenReturn(Optional.of(horario));

        HorarioResponseDTO resposta =
                horarioService.buscarPorId(1L);

        assertEquals(1L, resposta.codhorario());
        assertEquals("Sala 1", resposta.sala());
        assertEquals(DayOfWeek.MONDAY, resposta.diaSemana());
    }

    @Test
    void deveRetornarErroAoBuscarHorarioInexistente() {

        when(horarioRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> horarioService.buscarPorId(999L)
        );
    }

    @Test
    void deveAtualizarHorarioComSucesso() {

        HorarioRequestDTO dtoAtualizado =
                new HorarioRequestDTO(
                        60,
                        1L,
                        "Sala 2",
                        DayOfWeek.WEDNESDAY,
                        LocalTime.of(18, 30)
                );

        when(horarioRepository.findById(1L))
                .thenReturn(Optional.of(horario));

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        when(horarioRepository.save(any(HorarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        HorarioResponseDTO resposta =
                horarioService.atualizar(
                        1L,
                        dtoAtualizado
                );

        assertEquals(60, resposta.duracaoaula());
        assertEquals("Sala 2", resposta.sala());
        assertEquals(DayOfWeek.WEDNESDAY, resposta.diaSemana());
        assertEquals(LocalTime.of(18, 30), resposta.horaInicio());
    }

    @Test
    void deveExcluirHorarioComSucesso() {

        when(horarioRepository.findById(1L))
                .thenReturn(Optional.of(horario));

        horarioService.excluir(1L);

        verify(horarioRepository)
                .delete(horario);
    }

    @Test
    void deveRetornarErroAoExcluirHorarioInexistente() {

        when(horarioRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> horarioService.excluir(999L)
        );

        verify(horarioRepository, never())
                .delete(any(HorarioModel.class));
    }

    @Test
    void deveListarHorarioDaTurmaDoProfessor() {

        when(horarioRepository.findAll())
                .thenReturn(List.of(horario));

        when(
                usuarioAutenticadoService
                        .podeAcessarClasse(classe)
        )
                .thenReturn(true);

        List<HorarioResponseDTO> resposta =
                horarioService.listarTodos();

        assertEquals(1, resposta.size());
    }

    @Test
    void naoDeveListarHorarioDeOutraTurma() {

        when(horarioRepository.findAll())
                .thenReturn(List.of(horario));

        when(
                usuarioAutenticadoService
                        .podeAcessarClasse(classe)
        )
                .thenReturn(false);

        List<HorarioResponseDTO> resposta =
                horarioService.listarTodos();

        assertTrue(resposta.isEmpty());
    }

    @Test
    void deveImpedirBuscaDeHorarioDeOutraTurma() {

        when(horarioRepository.findById(1L))
                .thenReturn(Optional.of(horario));

        doThrow(
                new SecurityException(
                        "Você não possui acesso a esta turma"
                )
        )
                .when(usuarioAutenticadoService)
                .validarAcessoAClasse(classe);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> horarioService.buscarPorId(1L)
        );

        assertEquals(
                "Você não possui acesso a esta turma",
                exception.getMessage()
        );
    }

    @Test
    void deveImpedirConflitoDeHorarioDaMesmaClasse() {

        HorarioRequestDTO dtoConflito = new HorarioRequestDTO(
                60,
                1L,
                "Sala 2",
                DayOfWeek.MONDAY,
                LocalTime.of(19, 30)
        );

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        when(horarioRepository.findByDiaSemana(DayOfWeek.MONDAY))
                .thenReturn(List.of(horario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> horarioService.cadastrar(dtoConflito)
        );

        assertEquals(
                "A classe já possui outro horário neste período",
                exception.getMessage()
        );

        verify(horarioRepository, never())
                .save(any(HorarioModel.class));
    }

    @Test
    void deveImpedirConflitoDeSala() {

        ClasseModel outraClasse = new ClasseModel();
        outraClasse.setCodclasse(2L);
        outraClasse.setNivel("BASICO");

        HorarioRequestDTO dtoConflito = new HorarioRequestDTO(
                60,
                2L,
                "Sala 1",
                DayOfWeek.MONDAY,
                LocalTime.of(19, 30)
        );

        when(classeRepository.findById(2L))
                .thenReturn(Optional.of(outraClasse));

        when(horarioRepository.findByDiaSemana(DayOfWeek.MONDAY))
                .thenReturn(List.of(horario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> horarioService.cadastrar(dtoConflito)
        );

        assertEquals(
                "A sala já está ocupada neste período",
                exception.getMessage()
        );

        verify(horarioRepository, never())
                .save(any(HorarioModel.class));
    }

    @Test
    void deveImpedirConflitoDeHorarioDoMesmoProfessor() {

        FuncionarioModel professor = new FuncionarioModel();
        professor.setCodfuncionario(10L);

        classe.setProfessor(professor);

        ClasseModel outraClasse = new ClasseModel();
        outraClasse.setCodclasse(2L);
        outraClasse.setNivel("BASICO");
        outraClasse.setProfessor(professor);

        HorarioRequestDTO dtoConflito = new HorarioRequestDTO(
                60,
                2L,
                "Sala 2",
                DayOfWeek.MONDAY,
                LocalTime.of(19, 30)
        );

        when(classeRepository.findById(2L))
                .thenReturn(Optional.of(outraClasse));

        when(horarioRepository.findByDiaSemana(DayOfWeek.MONDAY))
                .thenReturn(List.of(horario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> horarioService.cadastrar(dtoConflito)
        );

        assertEquals(
                "O professor já possui outra aula neste período",
                exception.getMessage()
        );

        verify(horarioRepository, never())
                .save(any(HorarioModel.class));
    }

    @Test
    void deveIgnorarProprioHorarioAoAtualizar() {

        HorarioRequestDTO dtoAtualizado = new HorarioRequestDTO(
                90,
                1L,
                "Sala 1",
                DayOfWeek.MONDAY,
                LocalTime.of(19, 0)
        );

        when(horarioRepository.findById(1L))
                .thenReturn(Optional.of(horario));

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        when(horarioRepository.findByDiaSemana(DayOfWeek.MONDAY))
                .thenReturn(List.of(horario));

        when(horarioRepository.save(any(HorarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        HorarioResponseDTO resposta = assertDoesNotThrow(
                () -> horarioService.atualizar(
                        1L,
                        dtoAtualizado
                )
        );

        assertNotNull(resposta);

        verify(horarioRepository)
                .save(horario);
    }

    @Test
    void devePermitirHorariosConsecutivos() {

        HorarioRequestDTO dtoConsecutivo = new HorarioRequestDTO(
                60,
                1L,
                "Sala 1",
                DayOfWeek.MONDAY,
                LocalTime.of(20, 30)
        );

        when(classeRepository.findById(1L))
                .thenReturn(Optional.of(classe));

        when(horarioRepository.findByDiaSemana(DayOfWeek.MONDAY))
                .thenReturn(List.of(horario));

        when(horarioRepository.save(any(HorarioModel.class)))
                .thenAnswer(invocation -> {
                    HorarioModel salvo = invocation.getArgument(0);
                    salvo.setCodhorario(2L);
                    return salvo;
                });

        HorarioResponseDTO resposta = assertDoesNotThrow(
                () -> horarioService.cadastrar(dtoConsecutivo)
        );

        assertNotNull(resposta);
        assertEquals(LocalTime.of(20, 30), resposta.horaInicio());
    }
}