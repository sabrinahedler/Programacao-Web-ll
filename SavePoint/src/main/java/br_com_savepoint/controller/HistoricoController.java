package br_com_savepoint.controller;

import br_com_savepoint.model.Historico;
import br_com_savepoint.service.HistoricoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ofertas")
@CrossOrigin(origins = "*")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<Historico>> buscarPorOfertaId(@PathVariable("id") Long ofertaId) {
        List<Historico> historico = historicoService.buscarPorOfertaId(ofertaId);

        return ResponseEntity.ok(historico);
    }

    @PostMapping("/{id}/historico")
    public ResponseEntity<Historico> registrarAlteracaoPreco(@PathVariable("id") Long ofertaId, @RequestBody Historico historicoDados) {

        Historico novoRegistro = historicoService.registrarAlteracaoPreco(ofertaId, historicoDados.getPreco());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOferta(@PathVariable("id") Long ofertaId) {
        boolean deletado = historicoService.deletarPorOfertaId(ofertaId);

        if (deletado) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}