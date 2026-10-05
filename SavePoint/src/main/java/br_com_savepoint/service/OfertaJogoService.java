package br_com_savepoint.service;

import br_com_savepoint.exception.RecursoNaoEncontradoException;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.Loja;
import br_com_savepoint.model.OfertaJogo;
import br_com_savepoint.repository.OfertaJogoRepository;
import br_com_savepoint.util.Validacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Regras de negócio das ofertas de jogos nas lojas. */
@Service
@Transactional
public class OfertaJogoService {

    private final OfertaJogoRepository ofertaJogoRepository;
    private final JogoService jogoService;
    private final LojaService lojaService;

    public OfertaJogoService(OfertaJogoRepository ofertaJogoRepository, JogoService jogoService, LojaService lojaService) {
        this.ofertaJogoRepository = ofertaJogoRepository;
        this.jogoService = jogoService;
        this.lojaService = lojaService;
    }

    /** Cadastra a oferta de um jogo em uma loja e calcula o percentual de desconto. */
    public OfertaJogo cadastrarOferta(Long jogoId, OfertaJogo oferta) {
        Jogo jogo = jogoService.buscarPorId(jogoId);
        Validacao.exigirPreenchido(oferta.getLoja(), "loja");
        Validacao.exigirPreenchido(oferta.getLoja().getId(), "loja.id");
        Validacao.exigirPositivo(oferta.getPrecoOriginal(), "precoOriginal");
        Validacao.exigirNaoNegativo(oferta.getPrecoAtual(), "precoAtual");
        Loja loja = lojaService.buscarPorId(oferta.getLoja().getId());

        oferta.setId(null);
        oferta.setJogo(jogo);
        oferta.setLoja(loja);
        oferta.setPercentualDesconto(oferta.calcularPercentualDesconto());
        return ofertaJogoRepository.save(oferta);
    }

    /** Lista as ofertas de um jogo, da mais barata para a mais cara. */
    public List<OfertaJogo> compararPrecosPorJogo(Long jogoId) {
        jogoService.buscarPorId(jogoId);
        return ofertaJogoRepository.findByJogoIdOrderByPrecoAtualAsc(jogoId);
    }

    /** Busca uma oferta pelo identificador ou falha se ela não existir. */
    public OfertaJogo buscarPorId(Long ofertaId) {
        return ofertaJogoRepository.findById(ofertaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oferta não encontrada com o ID: " + ofertaId));
    }
}
