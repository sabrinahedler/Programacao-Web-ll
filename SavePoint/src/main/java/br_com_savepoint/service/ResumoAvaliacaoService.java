package br_com_savepoint.service;

import br_com_savepoint.model.AvaliacaoUsuario;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.ResumoAvaliacao;
import br_com_savepoint.repository.AvaliacaoUsuarioRepository;
import br_com_savepoint.repository.JogoRepository;
import br_com_savepoint.repository.ResumoAvaliacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ResumoAvaliacaoService {

    private final ResumoAvaliacaoRepository resumoAvaliacaoRepository;
    private final JogoRepository jogoRepository;
    private final AvaliacaoUsuarioRepository avaliacaoUsuarioRepository;

    public ResumoAvaliacaoService(ResumoAvaliacaoRepository resumoAvaliacaoRepository, JogoRepository jogoRepository, AvaliacaoUsuarioRepository avaliacaoUsuarioRepository) {
        this.resumoAvaliacaoRepository = resumoAvaliacaoRepository;
        this.jogoRepository = jogoRepository;
        this.avaliacaoUsuarioRepository = avaliacaoUsuarioRepository;
    }

    // POST /jogos/{id}/resumo
    public ResumoAvaliacao gerarResumo(Long jogoId) {
        Jogo jogo = jogoRepository.findById(jogoId).orElseThrow(() -> new IllegalArgumentException("Jogo não encontrado com o ID: " + jogoId));

        List<AvaliacaoUsuario> avaliacoes = avaliacaoUsuarioRepository.findByJogoId(jogoId);

        ResumoAvaliacao resumo = resumoAvaliacaoRepository.findByJogoId(jogoId).orElseGet(ResumoAvaliacao::new);

        double somaNotas = 0;
        int quantidadeRecomendacoes = 0;

        for (AvaliacaoUsuario avaliacao : avaliacoes) {
            if (avaliacao.getNota() != null) {
                somaNotas += avaliacao.getNota();
            }
            if (avaliacao.isRecomendado()) {
                quantidadeRecomendacoes++;
            }
        }

        resumo.setJogo(jogo);
        resumo.setTotalAvaliacoes(avaliacoes.size());
        resumo.setDataGeracao(LocalDate.now());

        if (avaliacoes.isEmpty()) {
            resumo.setNotaMedia(0);
            resumo.setPercentualRecomendacao(0);
        } else {
            double media = somaNotas / avaliacoes.size();
            double percentual = (quantidadeRecomendacoes * 100.0) / avaliacoes.size();

            resumo.setNotaMedia(media);
            resumo.setPercentualRecomendacao(percentual);
        }

        resumo.setResumoGeradoIA("Resumo gerado com base nas avaliações cadastradas para o jogo.");
        ResumoAvaliacao resumoSalvo = resumoAvaliacaoRepository.save(resumo);

        jogo.setResumoAvaliacao(resumoSalvo);
        jogoRepository.save(jogo);

        return resumoSalvo;
    }

    // GET /jogos/{id}/resumo
    public Optional<ResumoAvaliacao> buscarResumo(Long jogoId) {
        if (!jogoRepository.existsById(jogoId)) {
            return Optional.empty();
        }
        return resumoAvaliacaoRepository.findByJogoId(jogoId);
    }

    // PUT /jogos/{id}/resumo
    public Optional<ResumoAvaliacao> atualizarResumo(Long jogoId, ResumoAvaliacao resumoDados) {
        Optional<ResumoAvaliacao> resumoOptional = buscarResumo(jogoId);

        if (resumoOptional.isEmpty()) {
            return Optional.empty();
        }

        ResumoAvaliacao resumoExistente = resumoOptional.get();

        if (resumoDados.getPrincipaisElogios() != null) {
            resumoExistente.setPrincipaisElogios(resumoDados.getPrincipaisElogios());
        }

        if (resumoDados.getPrincipaisCriticas() != null) {
            resumoExistente.setPrincipaisCriticas(resumoDados.getPrincipaisCriticas());
        }

        if (resumoDados.getResumoGeradoIA() != null && !resumoDados.getResumoGeradoIA().trim().isEmpty()) {
            resumoExistente.setResumoGeradoIA(resumoDados.getResumoGeradoIA());
        }

        return Optional.of(resumoAvaliacaoRepository.save(resumoExistente));
    }

    @Transactional
    public boolean deletarResumo(Long jogoId) {
        Optional<Jogo> jogoOptional = jogoRepository.findById(jogoId);
        if (jogoOptional.isEmpty()) {
            return false;
        }

        Jogo jogo = jogoOptional.get();
        ResumoAvaliacao resumo = jogo.getResumoAvaliacao();

        if (resumo != null) {
            jogo.setResumoAvaliacao(null);
            resumo.setJogo(null);

            jogoRepository.save(jogo);

            resumoAvaliacaoRepository.delete(resumo);
            return true;
        }

        return false;
    }
}
