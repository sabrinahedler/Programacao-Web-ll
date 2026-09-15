package br_com_savepoint.controller;

import br_com_savepoint.model.Historico;
import br_com_savepoint.model.OfertaJogo;
import br_com_savepoint.service.HistoricoService;
import br_com_savepoint.service.OfertaJogoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class OfertaJogoController {

    private final OfertaJogoService ofertaJogoService;
    private final HistoricoService historicoService;

    public OfertaJogoController(OfertaJogoService ofertaJogoService, HistoricoService historicoService) {
        this.ofertaJogoService = ofertaJogoService;
        this.historicoService = historicoService;
    }

    @PostMapping("/jogos/{id}/ofertas")
    public ResponseEntity<OfertaJogo> cadastrarOferta(@PathVariable("id") Long jogoId, @RequestBody OfertaJogo novaOferta) {
        try {
            OfertaJogo ofertaSalva = ofertaJogoService.cadastrarOferta(jogoId, novaOferta);
            return ResponseEntity.status(HttpStatus.CREATED).body(ofertaSalva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ofertas/{id}/comparacao")
    public ResponseEntity<List<OfertaJogo>> compararPrecos(@PathVariable("id") Long jogoId) {
        List<OfertaJogo> ofertasComparadas = ofertaJogoService.compararPrecosPorJogo(jogoId);
        return ResponseEntity.ok(ofertasComparadas);
    }

    @GetMapping("/ofertas/{id}/historico")
    public ResponseEntity<List<Historico>> buscarHistorico(@PathVariable("id") Long ofertaId) {
        List<Historico> historicoPrecos = historicoService.buscarPorOfertaId(ofertaId);
        return ResponseEntity.ok(historicoPrecos);
    }

    @GetMapping("/ofertas/{id}")
    public ResponseEntity<OfertaJogo> buscarPorId(@PathVariable("id") Long ofertaId) {
        Optional<OfertaJogo> ofertaOptional = ofertaJogoService.buscarPorId(ofertaId);

        if (ofertaOptional.isPresent()) {
            OfertaJogo ofertaEncontrada = ofertaOptional.get();
            return ResponseEntity.ok(ofertaEncontrada);
        }

        return ResponseEntity.notFound().build();
    }
}