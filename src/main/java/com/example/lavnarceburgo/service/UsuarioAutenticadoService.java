package com.example.lavnarceburgo.service;

import com.example.lavnarceburgo.model.FuncionarioModel;
import com.example.lavnarceburgo.repository.FuncionarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.lavnarceburgo.model.ClasseModel;
import com.example.lavnarceburgo.model.enums.Cargo;

@Service
public class UsuarioAutenticadoService {

    private final FuncionarioRepository funcionarioRepository;

    public UsuarioAutenticadoService(
            FuncionarioRepository funcionarioRepository
    ) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public FuncionarioModel getFuncionarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Usuário não autenticado"
            );
        }

        String email = authentication.getName();

        return funcionarioRepository
                .findByUsuario_Email(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Funcionário autenticado não encontrado"
                        )
                );
    }

    public boolean classePertenceAoProfessor(
            Long codprofessor
    ) {

        FuncionarioModel funcionario =
                getFuncionarioAutenticado();

        return funcionario
                .getCodfuncionario()
                .equals(codprofessor);
    }

    public void validarAcessoAClasse(ClasseModel classe) {

        if (!podeAcessarClasse(classe)) {
            throw new SecurityException(
                    "Você não possui acesso a esta turma"
            );
        }
    }

    public boolean podeAcessarClasse(ClasseModel classe) {

        FuncionarioModel funcionario =
                getFuncionarioAutenticado();

        if (funcionario.getCargo() == Cargo.SECRETARIA) {
            return true;
        }

        if (funcionario.getCargo() == Cargo.MASTER) {
            return true;
        }

        if (funcionario.getCargo() != Cargo.PROFESSOR) {
            return false;
        }

        if (classe == null || classe.getProfessor() == null) {
            return false;
        }

        return funcionario
                .getCodfuncionario()
                .equals(
                        classe.getProfessor().getCodfuncionario()
                );
    }
}