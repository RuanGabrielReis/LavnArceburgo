package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.classe.ClasseRequestDTO;
import com.example.lavnarceburgo.dto.classe.ClasseResponseDTO;
import com.example.lavnarceburgo.service.ClasseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClasseController {

    private final ClasseService classeService;

    public ClasseController(ClasseService classeService) {
        this.classeService = classeService;
    }

    @PostMapping
    public ResponseEntity<ClasseResponseDTO> cadastrar(
            @Valid @RequestBody ClasseRequestDTO dto
    ) {

        ClasseResponseDTO classe = classeService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(classe);
    }

    @GetMapping
    public ResponseEntity<List<ClasseResponseDTO>> listarTodas() {
        return ResponseEntity.ok(
                classeService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClasseResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                classeService.buscarPorId(id)
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<ClasseResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClasseRequestDTO dto
    ) {
        return ResponseEntity.ok(
                classeService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        classeService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}