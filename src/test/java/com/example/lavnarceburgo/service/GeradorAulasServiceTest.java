package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.model.AulaModel;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.HorarioModel;
import com.example.lavnarceburgo.repository.AulaRepository;
import com.example.lavnarceburgo.repository.HorarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeradorAulasServiceTest {

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private AulaRepository aulaRepository;

    private GeradorAulasService geradorAulasService;

    private ClasseModel classe;
    private HorarioModel horario;

    @BeforeEach
    void prepararDados() {

        geradorAulasService =
                new GeradorAulasService(
                        horarioRepository,
                        aulaRepository
                );

        classe = new ClasseModel();
        classe.setCodclasse(1L);

        horario = new HorarioModel();
        horario.setClasse(classe);
        horario.setHoraInicio(
                LocalTime.of(19, 0)
        );
    }

    @Test
    void deveCriarAulaParaHorarioDoDia() {

        LocalDate hoje = LocalDate.now(
                ZoneId.of("America/Sao_Paulo")
        );

        when(
                horarioRepository.findByDiaSemana(
                        hoje.getDayOfWeek()
                )
        ).thenReturn(List.of(horario));

        when(
                aulaRepository
                        .existsByClasseCodclasseAndDiahora(
                                1L,
                                hoje.atTime(19, 0)
                        )
        ).thenReturn(false);

        geradorAulasService.gerarAulasDoDia();

        ArgumentCaptor<AulaModel> captor =
                ArgumentCaptor.forClass(
                        AulaModel.class
                );

        verify(aulaRepository).save(
                captor.capture()
        );

        AulaModel aulaCriada =
                captor.getValue();

        assertEquals(
                classe,
                aulaCriada.getClasse()
        );

        assertEquals(
                hoje.atTime(19, 0),
                aulaCriada.getDiahora()
        );
    }

    @Test
    void naoDeveCriarAulaQuandoElaJaExistir() {

        LocalDate hoje = LocalDate.now(
                ZoneId.of("America/Sao_Paulo")
        );

        when(
                horarioRepository.findByDiaSemana(
                        hoje.getDayOfWeek()
                )
        ).thenReturn(List.of(horario));

        when(
                aulaRepository
                        .existsByClasseCodclasseAndDiahora(
                                1L,
                                hoje.atTime(19, 0)
                        )
        ).thenReturn(true);

        geradorAulasService.gerarAulasDoDia();

        verify(
                aulaRepository,
                never()
        ).save(any(AulaModel.class));
    }

    @Test
    void naoDeveCriarAulaQuandoNaoHouverHorarioNoDia() {

        LocalDate hoje = LocalDate.now(
                ZoneId.of("America/Sao_Paulo")
        );

        when(
                horarioRepository.findByDiaSemana(
                        hoje.getDayOfWeek()
                )
        ).thenReturn(List.of());

        geradorAulasService.gerarAulasDoDia();

        verify(
                aulaRepository,
                never()
        ).save(any(AulaModel.class));
    }

    @Test
    void deveCriarVariasAulasDoMesmoDia() {

        LocalDate hoje = LocalDate.now(
                ZoneId.of("America/Sao_Paulo")
        );

        ClasseModel outraClasse =
                new ClasseModel();

        outraClasse.setCodclasse(2L);

        HorarioModel outroHorario =
                new HorarioModel();

        outroHorario.setClasse(
                outraClasse
        );

        outroHorario.setHoraInicio(
                LocalTime.of(20, 30)
        );

        when(
                horarioRepository.findByDiaSemana(
                        hoje.getDayOfWeek()
                )
        ).thenReturn(
                List.of(
                        horario,
                        outroHorario
                )
        );

        when(
                aulaRepository
                        .existsByClasseCodclasseAndDiahora(
                                1L,
                                hoje.atTime(19, 0)
                        )
        ).thenReturn(false);

        when(
                aulaRepository
                        .existsByClasseCodclasseAndDiahora(
                                2L,
                                hoje.atTime(20, 30)
                        )
        ).thenReturn(false);

        geradorAulasService.gerarAulasDoDia();

        verify(
                aulaRepository,
                times(2)
        ).save(any(AulaModel.class));
    }
}