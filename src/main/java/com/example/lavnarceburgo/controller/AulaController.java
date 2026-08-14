package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.aula.AulaRequestDTO;
import com.example.lavnarceburgo.dto.aula.AulaResponseDTO;
import com.example.lavnarceburgo.service.AulaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aulas")
public class AulaController {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostMapping
    public ResponseEntity<AulaResponseDTO> cadastrar(
            @Valid @RequestBody AulaRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aulaService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AulaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                aulaService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AulaResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                aulaService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AulaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AulaRequestDTO dto
    ) {
        return ResponseEntity.ok(
                aulaService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        aulaService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}