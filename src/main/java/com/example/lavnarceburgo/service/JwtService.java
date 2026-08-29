package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.model.FuncionarioModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final String secret;
    private final long expiracao;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiracao
    ) {
        this.secret = secret;
        this.expiracao = expiracao;
    }

    private SecretKey getChave() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String gerarToken(FuncionarioModel funcionario) {

        Date agora = new Date();

        Date dataExpiracao =
                new Date(agora.getTime() + expiracao);

        return Jwts.builder()
                .subject(
                        funcionario
                                .getUsuario()
                                .getEmail()
                )
                .claim(
                        "codfuncionario",
                        funcionario.getCodfuncionario()
                )
                .claim(
                        "cargo",
                        funcionario.getCargo().name()
                )
                .issuedAt(agora)
                .expiration(dataExpiracao)
                .signWith(getChave())
                .compact();
    }

    public String extrairEmail(String token) {

        return extrairClaims(token)
                .getSubject();
    }

    public boolean tokenValido(String token) {

        try {
            extrairClaims(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private Claims extrairClaims(String token) {

        return Jwts.parser()
                .verifyWith(getChave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extrairCodFuncionario(String token) {

        Number codFuncionario = extrairClaims(token)
                .get(
                        "codfuncionario",
                        Number.class
                );

        return codFuncionario.longValue();
    }
}