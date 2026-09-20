package br_com_savepoint.controller;

import br_com_savepoint.model.Loja;
import br_com_savepoint.service.LojaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lojas")
@CrossOrigin(origins = "*")
public class LojaController {

    private final LojaService lojaService;

    public LojaController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @PostMapping
    public ResponseEntity<Loja> salvar(@RequestBody Loja loja) {
        Loja novaLoja = lojaService.salvar(loja);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaLoja);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean deletado = lojaService.deletar(id);

        if (deletado) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Loja> atualizar(@PathVariable Long id, @RequestBody Loja lojaAtualizada) {
        Optional<Loja> lojaOptional = lojaService.atualizar(id, lojaAtualizada);

        if (lojaOptional.isPresent()) {
            Loja lojaModificada = lojaOptional.get();
            return ResponseEntity.status(HttpStatus.CREATED).body(lojaModificada);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Loja>> listarTodas() {
        List<Loja> lojas = lojaService.listarTodas();
        return ResponseEntity.ok(lojas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loja> buscarPorId(@PathVariable Long id) {
        Optional<Loja> lojaOptional = lojaService.buscarPorId(id);

        if (lojaOptional.isPresent()) {
            Loja lojaEncontrada = lojaOptional.get();
            return ResponseEntity.ok(lojaEncontrada);
        }

        return ResponseEntity.notFound().build();
    }
}