package br_com_savepoint.service;

import br_com_savepoint.exception.RecursoNaoEncontradoException;
import br_com_savepoint.model.AvaliacaoUsuario;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.ResumoAvaliacao;
import br_com_savepoint.repository.AvaliacaoUsuarioRepository;
import br_com_savepoint.repository.ResumoAvaliacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Regras de negócio do resumo consolidado de avaliações de um jogo. */
@Service
@Transactional
public class ResumoAvaliacaoService {

    private final ResumoAvaliacaoRepository resumoRepository;
    private final AvaliacaoUsuarioRepository avaliacaoRepository;
    private final JogoService jogoService;

    public ResumoAvaliacaoService(ResumoAvaliacaoRepository resumoRepository,
                                  AvaliacaoUsuarioRepository avaliacaoRepository,
                                  JogoService jogoService) {
        this.resumoRepository = resumoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.jogoService = jogoService;
    }

    /** Gera ou recalcula o resumo de avaliações de um jogo. */
    public ResumoAvaliacao gerarResumo(Long jogoId) {
        Jogo jogo = jogoService.buscarPorId(jogoId);
        List<AvaliacaoUsuario> avaliacoes = avaliacaoRepository.findByJogoId(jogoId);

        double somaNotas = 0;
        int recomendacoes = 0;
        for (AvaliacaoUsuario avaliacao : avaliacoes) {
            somaNotas += avaliacao.getNota();
            if (avaliacao.isRecomendado()) {
                recomendacoes++;
            }
        }

        int total = avaliacoes.size();
        ResumoAvaliacao resumo = jogo.getResumoAvaliacao() != null ? jogo.getResumoAvaliacao() : new ResumoAvaliacao();
        resumo.setTotalAvaliacoes(total);
        resumo.setNotaMedia(total == 0 ? 0 : somaNotas / total);
        resumo.setPercentualRecomendacao(total == 0 ? 0 : recomendacoes * 100.0 / total);
        resumo.setResumoGeradoIA("Resumo gerado com base nas avaliações cadastradas para o jogo.");
        resumo.setDataGeracao(LocalDate.now());

        ResumoAvaliacao salvo = resumoRepository.save(resumo);
        salvo.setJogo(jogo);
        jogo.setResumoAvaliacao(salvo);
        return salvo;
    }

    /** Busca o resumo de um jogo ou falha se ele ainda não tiver sido gerado. */
    public ResumoAvaliacao buscarResumo(Long jogoId) {
        Jogo jogo = jogoService.buscarPorId(jogoId);
        if (jogo.getResumoAvaliacao() == null) {
            throw new RecursoNaoEncontradoException("O jogo " + jogoId + " ainda não possui resumo de avaliações.");
        }
        return jogo.getResumoAvaliacao();
    }

    /** Atualiza os elogios, as críticas e o texto do resumo, ignorando os campos não informados. */
    public ResumoAvaliacao atualizarResumo(Long jogoId, ResumoAvaliacao dados) {
        ResumoAvaliacao resumo = buscarResumo(jogoId);

        if (dados.getPrincipaisElogios() != null) {
            resumo.setPrincipaisElogios(dados.getPrincipaisElogios());
        }
        if (dados.getPrincipaisCriticas() != null) {
            resumo.setPrincipaisCriticas(dados.getPrincipaisCriticas());
        }
        if (dados.getResumoGeradoIA() != null && !dados.getResumoGeradoIA().trim().isEmpty()) {
            resumo.setResumoGeradoIA(dados.getResumoGeradoIA());
        }
        return resumoRepository.save(resumo);
    }

    /** Remove o resumo de um jogo. */
    public void deletarResumo(Long jogoId, Long resumoId) {
        Jogo jogo = jogoService.buscarPorId(jogoId);
        ResumoAvaliacao resumo = jogo.getResumoAvaliacao();
        if (resumo == null || !resumo.getId().equals(resumoId)) {
            throw new RecursoNaoEncontradoException(
                    "O resumo " + resumoId + " não pertence ao jogo " + jogoId + ".");
        }
        jogo.setResumoAvaliacao(null);
        resumoRepository.delete(resumo);
    }
}
