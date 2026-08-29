package com.example.lavnarceburgo.config;

import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.model.UsuarioModel;
import com.example.lavnarceburgo.model.enums.Cargo;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import com.example.lavnarceburgo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthFilter jwtAuthFilter;
    private FuncionarioModel funcionario;

    @BeforeEach
    void prepararDados() {

        jwtAuthFilter = new JwtAuthFilter(
                jwtService,
                funcionarioRepository
        );

        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail("professor@lavn.com");
        usuario.setNome("Professor Teste");

        funcionario = new FuncionarioModel();
        funcionario.setCodfuncionario(1L);
        funcionario.setUsuario(usuario);
        funcionario.setCargo(Cargo.PROFESSOR);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAutenticarQuandoTokenForValido() throws Exception {

        String token = "token.jwt.valido";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.tokenValido(token))
                .thenReturn(true);

        when(jwtService.extrairCodFuncionario(token))
                .thenReturn(1L);

        when(funcionarioRepository.findById(1L))
                .thenReturn(Optional.of(funcionario));

        jwtAuthFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertEquals(
                "professor@lavn.com",
                authentication.getPrincipal()
        );

        assertTrue(
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_PROFESSOR")
                        )
        );

        verify(jwtService)
                .tokenValido(token);

        verify(jwtService)
                .extrairCodFuncionario(token);

        verify(funcionarioRepository)
                .findById(1L);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void naoDeveAutenticarQuandoNaoHouverToken() throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        jwtAuthFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNull(authentication);

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(
                jwtService,
                funcionarioRepository
        );
    }

    @Test
    void naoDeveAutenticarQuandoTokenForInvalido() throws Exception {

        String token = "token.invalido";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.tokenValido(token))
                .thenReturn(false);

        jwtAuthFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNull(authentication);

        verify(jwtService)
                .tokenValido(token);

        verify(jwtService, never())
                .extrairCodFuncionario(anyString());

        verifyNoInteractions(funcionarioRepository);

        verify(filterChain)
                .doFilter(request, response);
    }
}