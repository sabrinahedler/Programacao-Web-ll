package br_com_savepoint.service;

import br_com_savepoint.model.Historico;
import br_com_savepoint.model.OfertaJogo;
import br_com_savepoint.repository.HistoricoRepository;
import br_com_savepoint.util.Validacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Regras de negócio do histórico de preços das ofertas. */
@Service
@Transactional
public class HistoricoService {

    private final HistoricoRepository historicoRepository;
    private final OfertaJogoService ofertaJogoService;

    public HistoricoService(HistoricoRepository historicoRepository, OfertaJogoService ofertaJogoService) {
        this.historicoRepository = historicoRepository;
        this.ofertaJogoService = ofertaJogoService;
    }

    /** Lista o histórico de preços de uma oferta, do mais recente para o mais antigo. */
    public List<Historico> buscarPorOfertaId(Long ofertaId) {
        ofertaJogoService.buscarPorId(ofertaId);
        return historicoRepository.findByOfertaIdOrderByDataDesc(ofertaId);
    }

    /** Registra um novo preço no histórico e atualiza o preço atual da oferta. */
    public Historico registrarAlteracaoPreco(Long ofertaId, double novoPreco) {
        OfertaJogo oferta = ofertaJogoService.buscarPorId(ofertaId);
        Validacao.exigirNaoNegativo(novoPreco, "preco");

        oferta.atualizarPrecoAtual(novoPreco);

        Historico historico = new Historico();
        historico.setOferta(oferta);
        historico.setPreco(novoPreco);
        historico.setData(LocalDateTime.now());
        return historicoRepository.save(historico);
    }
}
