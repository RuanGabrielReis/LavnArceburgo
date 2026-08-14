package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.anotacao.AnotacaoRequestDTO;
import com.example.lavnarceburgo.dto.anotacao.AnotacaoResponseDTO;
import com.example.lavnarceburgo.service.AnotacoesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anotacoes")
public class AnotacoesController {

    private final AnotacoesService anotacoesService;

    public AnotacoesController(AnotacoesService anotacoesService) {
        this.anotacoesService = anotacoesService;
    }

    @PostMapping
    public ResponseEntity<AnotacaoResponseDTO> cadastrar(
            @Valid @RequestBody AnotacaoRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(anotacoesService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AnotacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(
                anotacoesService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnotacaoResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                anotacoesService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnotacaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AnotacaoRequestDTO dto
    ) {
        return ResponseEntity.ok(
                anotacoesService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        anotacoesService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}