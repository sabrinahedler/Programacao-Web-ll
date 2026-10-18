package br_com_savepoint.controller;

import br_com_savepoint.model.RequisitosMinimos;
import br_com_savepoint.service.RequisitosMinimosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/jogos/{id}/requisitos-minimos")
@CrossOrigin(origins = "*")
public class RequisitosMinimosController {

    private final RequisitosMinimosService requisitosMinimosService;

    public RequisitosMinimosController(RequisitosMinimosService requisitosMinimosService) {
        this.requisitosMinimosService = requisitosMinimosService;
    }

    @GetMapping
    public ResponseEntity<RequisitosMinimos> buscarPorJogoId(@PathVariable("id") Long jogoId) {
        Optional<RequisitosMinimos> requisitosOptional = requisitosMinimosService.buscarPorJogoId(jogoId);

        if (requisitosOptional.isPresent()) {
            RequisitosMinimos requisitos = requisitosOptional.get();
            return ResponseEntity.ok(requisitos);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<RequisitosMinimos> cadastrar(@PathVariable("id") Long jogoId, @RequestBody RequisitosMinimos requisitos) {
        try {
            RequisitosMinimos novoRegistro = requisitosMinimosService.salvar(jogoId, requisitos);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping
    public ResponseEntity<RequisitosMinimos> atualizar(@PathVariable("id") Long jogoId, @RequestBody RequisitosMinimos novosDados) {
        Optional<RequisitosMinimos> requisitosAtualizados = requisitosMinimosService.atualizar(jogoId, novosDados);

        if (requisitosAtualizados.isPresent()) {
            RequisitosMinimos requisitos = requisitosAtualizados.get();
            return ResponseEntity.status(HttpStatus.CREATED).body(requisitos);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deletar(@PathVariable("id") Long jogoId) {
        boolean deletado = requisitosMinimosService.deletarPorJogoId(jogoId);

        if (deletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}