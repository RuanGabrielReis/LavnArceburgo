package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.presenca.PresencaRequestDTO;
import com.example.lavnarceburgo.dto.presenca.PresencaResponseDTO;
import com.example.lavnarceburgo.service.PresencaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presencas")
public class PresencaController {

    private final PresencaService presencaService;

    public PresencaController(PresencaService presencaService) {
        this.presencaService = presencaService;
    }

    @PostMapping
    public ResponseEntity<PresencaResponseDTO> cadastrar(
            @Valid @RequestBody PresencaRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(presencaService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<PresencaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(
                presencaService.listarTodas()
        );
    }

    @GetMapping("/{codaluno}/{codaula}")
    public ResponseEntity<PresencaResponseDTO> buscarPorId(
            @PathVariable Long codaluno,
            @PathVariable Long codaula
    ) {
        return ResponseEntity.ok(
                presencaService.buscarPorId(codaluno, codaula)
        );
    }

    @PutMapping("/{codaluno}/{codaula}")
    public ResponseEntity<PresencaResponseDTO> atualizar(
            @PathVariable Long codaluno,
            @PathVariable Long codaula,
            @Valid @RequestBody PresencaRequestDTO dto
    ) {
        return ResponseEntity.ok(
                presencaService.atualizar(codaluno, codaula, dto)
        );
    }

    @DeleteMapping("/{codaluno}/{codaula}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long codaluno,
            @PathVariable Long codaula
    ) {
        presencaService.excluir(codaluno, codaula);

        return ResponseEntity.noContent().build();
    }
}