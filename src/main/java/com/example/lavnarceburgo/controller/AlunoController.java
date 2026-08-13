package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.aluno.AlunoRequestDTO;
import com.example.lavnarceburgo.dto.aluno.AlunoResponseDTO;
import com.example.lavnarceburgo.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<AlunoResponseDTO> cadastrar(
            @Valid @RequestBody AlunoRequestDTO dto
    ) {

        AlunoResponseDTO aluno = alunoService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aluno);
    }

    @GetMapping
    public ResponseEntity<List<AlunoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                alunoService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                alunoService.buscarPorId(id)
        );
    }
}