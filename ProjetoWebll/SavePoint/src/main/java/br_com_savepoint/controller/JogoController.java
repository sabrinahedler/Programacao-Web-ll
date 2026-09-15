package br_com_savepoint.controller;

import br_com_savepoint.model.Jogo;
import br_com_savepoint.service.JogoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jogos")
@CrossOrigin(origins = "*")
public class JogoController {

    private final JogoService jogoService;

    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @GetMapping
    public ResponseEntity<List<Jogo>> listarTodos() {
        List<Jogo> jogos = jogoService.listarTodos();
        return ResponseEntity.ok(jogos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jogo> buscarPorId(@PathVariable Long id) {
        Optional<Jogo> jogoOptional = jogoService.buscarPorId(id);

        if (jogoOptional.isPresent()) {
            Jogo jogoEncontrado = jogoOptional.get();
            return ResponseEntity.ok(jogoEncontrado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Jogo> salvar(@RequestBody Jogo jogo) {
        Jogo novoJogo = jogoService.salvar(jogo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoJogo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Jogo> atualizar(@PathVariable Long id, @RequestBody Jogo jogoAtualizado) {
        Optional<Jogo> jogoOptional = jogoService.atualizar(id, jogoAtualizado);

        if (jogoOptional.isPresent()) {
            Jogo jogoModificado = jogoOptional.get();
            return ResponseEntity.status(HttpStatus.CREATED).body(jogoModificado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean deletado = jogoService.deletar(id);

        if (deletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}