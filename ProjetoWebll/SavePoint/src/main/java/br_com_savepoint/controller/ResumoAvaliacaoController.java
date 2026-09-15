package br_com_savepoint.controller;

import br_com_savepoint.service.ResumoAvaliacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class ResumoAvaliacaoController {

    private final ResumoAvaliacaoService resumoAvaliacaoService;

    public ResumoAvaliacaoController(
            ResumoAvaliacaoService resumoAvaliacaoService) {
        this.resumoAvaliacaoService = resumoAvaliacaoService;
    }

    @PostMapping("/jogos/{id}/resumo")
    public ResponseEntity<br_com_savepoint.model.ResumoAvaliacao> gerarResumo(
            @PathVariable("id") Long id) {

        try {
            br_com_savepoint.model.ResumoAvaliacao resumo =
                    resumoAvaliacaoService.gerarResumo(id);

            return ResponseEntity.status(HttpStatus.CREATED).body(resumo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/jogos/{id}/resumo")
    public ResponseEntity<br_com_savepoint.model.ResumoAvaliacao> buscarResumo(
            @PathVariable("id") Long id) {

        Optional<br_com_savepoint.model.ResumoAvaliacao> resumoOptional =
                resumoAvaliacaoService.buscarResumo(id);

        if (resumoOptional.isPresent()) {
            return ResponseEntity.ok(resumoOptional.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/jogos/{id}/resumo")
    public ResponseEntity<br_com_savepoint.model.ResumoAvaliacao> atualizarResumo(
            @PathVariable("id") Long id,
            @RequestBody br_com_savepoint.model.ResumoAvaliacao resumoDados) {

        Optional<br_com_savepoint.model.ResumoAvaliacao> resumoOptional =
                resumoAvaliacaoService.atualizarResumo(id, resumoDados);

        if (resumoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(resumoOptional.get());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/jogos/{jogoId}/resumo/{resumoId}")
    public ResponseEntity<Void> deletarResumo(
            @PathVariable("jogoId") Long jogoId,
            @PathVariable("resumoId") Long resumoId) {

        boolean deletado =
                resumoAvaliacaoService.deletarResumo(jogoId, resumoId);

        if (deletado) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
