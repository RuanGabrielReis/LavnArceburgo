package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.model.FuncionarioModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "lavn-chave-jwt-super-secreta-com-pelo-menos-32-caracteres";

    private static final long EXPIRACAO =
            1000L * 60 * 60 * 8;

    private SecretKey getChave() {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String gerarToken(FuncionarioModel funcionario) {

        Date agora = new Date();

        Date expiracao =
                new Date(agora.getTime() + EXPIRACAO);

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
                .expiration(expiracao)
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
}