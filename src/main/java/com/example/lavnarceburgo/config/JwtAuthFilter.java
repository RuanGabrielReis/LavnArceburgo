package com.example.lavnarceburgo.config;

import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import com.example.lavnarceburgo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final FuncionarioRepository funcionarioRepository;

    public JwtAuthFilter(
            JwtService jwtService,
            FuncionarioRepository funcionarioRepository
    ) {
        this.jwtService = jwtService;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        if (!jwtService.tokenValido(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.extrairEmail(token);

        FuncionarioModel funcionario = funcionarioRepository
                .findByUsuario_Email(email)
                .orElse(null);

        if (funcionario != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            SimpleGrantedAuthority autoridade =
                    new SimpleGrantedAuthority(
                            "ROLE_" + funcionario.getCargo().name()
                    );

            UsernamePasswordAuthenticationToken autenticacao =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(autoridade)
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(autenticacao);
        }

        filterChain.doFilter(request, response);
    }
}