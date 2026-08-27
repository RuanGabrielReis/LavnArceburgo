package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioAutenticadoServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    private UsuarioAutenticadoService usuarioAutenticadoService;

    private FuncionarioModel master;
    private FuncionarioModel outroProfessor;
    private ClasseModel classe;

    @BeforeEach
    void prepararDados() {

        usuarioAutenticadoService =
                new UsuarioAutenticadoService(funcionarioRepository);

        UsuarioModel usuarioMaster = new UsuarioModel();
        usuarioMaster.setEmail("master@lavn.com");
        usuarioMaster.setNome("Master");

        master = new FuncionarioModel();
        master.setCodfuncionario(1L);
        master.setUsuario(usuarioMaster);
        master.setCargo(Cargo.MASTER);

        UsuarioModel usuarioProfessor = new UsuarioModel();
        usuarioProfessor.setEmail("professor@lavn.com");
        usuarioProfessor.setNome("Outro Professor");

        outroProfessor = new FuncionarioModel();
        outroProfessor.setCodfuncionario(2L);
        outroProfessor.setUsuario(usuarioProfessor);
        outroProfessor.setCargo(Cargo.PROFESSOR);

        classe = new ClasseModel();
        classe.setCodclasse(10L);
        classe.setNivel("INTERMEDIARIO");
        classe.setProfessor(outroProfessor);
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void masterDevePoderAcessarClasseDeQualquerProfessor() {

        var autenticacao =
                new UsernamePasswordAuthenticationToken(
                        "master@lavn.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_MASTER"
                                )
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(autenticacao);

        when(
                funcionarioRepository
                        .findByUsuario_Email("master@lavn.com")
        )
                .thenReturn(Optional.of(master));

        boolean resultado =
                usuarioAutenticadoService
                        .podeAcessarClasse(classe);

        assertTrue(resultado);
    }

    @Test
    void validacaoNaoDeveBloquearMasterEmClasseDeOutroProfessor() {

        var autenticacao =
                new UsernamePasswordAuthenticationToken(
                        "master@lavn.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_MASTER"
                                )
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(autenticacao);

        when(
                funcionarioRepository
                        .findByUsuario_Email("master@lavn.com")
        )
                .thenReturn(Optional.of(master));

        assertDoesNotThrow(
                () -> usuarioAutenticadoService
                        .validarAcessoAClasse(classe)
        );
    }
}