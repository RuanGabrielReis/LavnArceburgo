package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.funcionario.FuncionarioRequestDTO;
import com.example.lavnarceburgo.dto.funcionario.FuncionarioResponseDTO;
import com.example.lavnarceburgo.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> cadastrar(
            @Valid @RequestBody FuncionarioRequestDTO dto
    ) {

        FuncionarioResponseDTO funcionario =
                funcionarioService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(funcionario);
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                funcionarioService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                funcionarioService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> atualizar(
        @PathVariable long id,
        @Valid @RequestBody FuncionarioRequestDTO dto
    ){
        return ResponseEntity.ok(
                funcionarioService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> excluir(
            @PathVariable long id
            ){
                funcionarioService.excluir(id);
                return ResponseEntity.noContent().build();
    }





}