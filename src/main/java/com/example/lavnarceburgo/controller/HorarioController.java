package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.horario.HorarioRequestDTO;
import com.example.lavnarceburgo.dto.horario.HorarioResponseDTO;
import com.example.lavnarceburgo.service.HorarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @PostMapping
    public ResponseEntity<HorarioResponseDTO> cadastrar(
            @Valid @RequestBody HorarioRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(horarioService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<HorarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                horarioService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                horarioService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody HorarioRequestDTO dto
    ) {
        return ResponseEntity.ok(
                horarioService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        horarioService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}