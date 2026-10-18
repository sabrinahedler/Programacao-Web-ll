package br_com_savepoint.controller;

import br_com_savepoint.model.ResumoAvaliacao;
import br_com_savepoint.service.ResumoAvaliacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/jogos")
@CrossOrigin(origins = "*")
public class ResumoAvaliacaoController {

    private final ResumoAvaliacaoService resumoAvaliacaoService;

    public ResumoAvaliacaoController(ResumoAvaliacaoService resumoAvaliacaoService) {
        this.resumoAvaliacaoService = resumoAvaliacaoService;
    }

    @PostMapping("/{id}/resumo")
    public ResponseEntity<?> gerarResumo(@PathVariable("id") Long id) {
        try {
            ResumoAvaliacao resumo = resumoAvaliacaoService.gerarResumo(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(resumo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro de Validação: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Crítico: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<?> buscarResumo(@PathVariable("id") Long id) {
        Optional<ResumoAvaliacao> resumoOptional = resumoAvaliacaoService.buscarResumo(id);

        if (resumoOptional.isPresent()) {
            return ResponseEntity.ok(resumoOptional.get());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum resumo encontrado para o jogo ID " + id + ". Rode o POST primeiro.");
    }

    @PutMapping("/{id}/resumo")
    public ResponseEntity<?> atualizarResumo(@PathVariable("id") Long id, @RequestBody ResumoAvaliacao resumoDados) {

        Optional<ResumoAvaliacao> resumoOptional = resumoAvaliacaoService.atualizarResumo(id, resumoDados);

        if (resumoOptional.isPresent()) {
            return ResponseEntity.ok(resumoOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resumo não encontrado para o jogo ID " + id);
    }

    @DeleteMapping("/{id}/resumo")
    public ResponseEntity<?> deletarResumo(@PathVariable("id") Long id) {
        boolean deletado = resumoAvaliacaoService.deletarResumo(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível deletar: Resumo não encontrado para o jogo ID " + id);
    }
}
