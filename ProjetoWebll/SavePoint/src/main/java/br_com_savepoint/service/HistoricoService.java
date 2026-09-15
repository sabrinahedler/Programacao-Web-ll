package br_com_savepoint.service;

import br_com_savepoint.model.Historico;
import br_com_savepoint.model.OfertaJogo;
import br_com_savepoint.repository.HistoricoRepository;
import br_com_savepoint.repository.OfertaJogoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricoService {

    private final HistoricoRepository historicoRepository;
    private final OfertaJogoRepository ofertaJogoRepository;

    public HistoricoService(HistoricoRepository historicoRepository, OfertaJogoRepository ofertaJogoRepository) {
        this.historicoRepository = historicoRepository;
        this.ofertaJogoRepository = ofertaJogoRepository;
    }

    public List<Historico> buscarPorOfertaId(Long ofertaId) {
        return historicoRepository.findByOfertaIdOrderByDataDesc(ofertaId);
    }

    public Historico registrarAlteracaoPreco(Long ofertaId, double novoPreco) {
        Optional<OfertaJogo> ofertaOptional = ofertaJogoRepository.findById(ofertaId);

        if (ofertaOptional.isPresent()) {
            OfertaJogo ofertaExistente = ofertaOptional.get();

            Historico novoHistorico = new Historico();
            novoHistorico.setPreco(novoPreco);
            novoHistorico.setData(LocalDateTime.now());
            novoHistorico.setOferta(ofertaExistente);

            ofertaExistente.setPrecoAtual(novoPreco);
            ofertaJogoRepository.save(ofertaExistente);

            return historicoRepository.save(novoHistorico);
        }

        throw new IllegalArgumentException("Oferta não encontrada com o ID: " + ofertaId);
    }

    public boolean deletarPorOfertaId(Long ofertaId) {
        if (ofertaJogoRepository.existsById(ofertaId)) {
            ofertaJogoRepository.deleteById(ofertaId);
            return true;
        }
        return false;
    }
}