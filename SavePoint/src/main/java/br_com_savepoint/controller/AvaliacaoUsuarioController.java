package br_com_savepoint.controller;

import br_com_savepoint.model.AvaliacaoUsuario;
import br_com_savepoint.service.AvaliacaoUsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jogos/{jogoId}")
@CrossOrigin(origins = "*")
public class AvaliacaoUsuarioController {

    private final AvaliacaoUsuarioService avaliacaoServico;

    public AvaliacaoUsuarioController(AvaliacaoUsuarioService avaliacaoServico) {
        this.avaliacaoServico = avaliacaoServico;
    }

    @GetMapping("/avaliacoes")
    public ResponseEntity<List<AvaliacaoUsuario>> buscarPorJogo(@PathVariable Long jogoId) {
        try {
            List<AvaliacaoUsuario> avaliacoes = avaliacaoServico.buscarPorJogo(jogoId);
            return ResponseEntity.ok(avaliacoes);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/media-notas")
    public ResponseEntity<Double> calcularMedia(@PathVariable Long jogoId) {
        try {
            double media = avaliacaoServico.calcularMediaJogo(jogoId);
            return ResponseEntity.ok(media);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/avaliacoes")
    public ResponseEntity<?> criar(@PathVariable Long jogoId, @RequestBody AvaliacaoUsuario avaliacao) {
        try {
            AvaliacaoUsuario novaAvaliacao = avaliacaoServico.salvar(jogoId, avaliacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaAvaliacao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/avaliacoes/{avaliacaoId}")
    public ResponseEntity<?> atualizar(@PathVariable Long jogoId, @PathVariable Long avaliacaoId, @RequestBody AvaliacaoUsuario avaliacao) {
        try {
            AvaliacaoUsuario avaliacaoAtualizada = avaliacaoServico.atualizar(jogoId, avaliacaoId, avaliacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoAtualizada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/avaliacoes/{avaliacaoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long jogoId, @PathVariable Long avaliacaoId) {
        try {
            avaliacaoServico.deletar(jogoId, avaliacaoId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}