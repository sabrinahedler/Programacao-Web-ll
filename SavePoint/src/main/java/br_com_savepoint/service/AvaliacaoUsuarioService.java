package br_com_savepoint.service;

import br_com_savepoint.model.AvaliacaoUsuario;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.AvaliacaoUsuarioRepository;
import br_com_savepoint.repository.JogoRepository;
import br_com_savepoint.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoUsuarioService {

    private final AvaliacaoUsuarioRepository avaliacaoRepositorio;
    private final UsuarioRepository usuarioRepositorio;
    private final JogoRepository jogoRepositorio;

    public AvaliacaoUsuarioService(AvaliacaoUsuarioRepository avaliacaoRepositorio, UsuarioRepository usuarioRepositorio, JogoRepository jogoRepositorio) {
        this.avaliacaoRepositorio = avaliacaoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.jogoRepositorio = jogoRepositorio;
    }

    public List<AvaliacaoUsuario> buscarPorJogo(Long jogoId) {
        if (!jogoRepositorio.existsById(jogoId)) {
            throw new IllegalArgumentException("Jogo não encontrado com ID: " + jogoId);
        }
        return avaliacaoRepositorio.findByJogoId(jogoId);
    }

    public List<AvaliacaoUsuario> buscarPorUsuario(Long usuarioId) {
        if (!usuarioRepositorio.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuário não encontrado com ID: " + usuarioId);
        }
        return avaliacaoRepositorio.findByUsuarioId(usuarioId);
    }

    public double calcularMediaJogo(Long jogoId) {
        if (!jogoRepositorio.existsById(jogoId)) {
            throw new IllegalArgumentException("Jogo não encontrado com ID: " + jogoId);
        }
        Double media = avaliacaoRepositorio.calcularMediaJogo(jogoId);
        return media != null ? media : 0.0;
    }

    public AvaliacaoUsuario salvar(Long jogoId, AvaliacaoUsuario avaliacao) {
        Jogo jogo = jogoRepositorio.findById(jogoId).orElseThrow(() -> new IllegalArgumentException("Jogo não encontrado com ID: " + jogoId));

        if (avaliacao.getUsuario() == null || avaliacao.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório");
        }

        Usuario usuario = usuarioRepositorio.findById(avaliacao.getUsuario().getId()).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + avaliacao.getUsuario().getId()));

        if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new IllegalArgumentException("Nota deve ser entre 1 e 5");
        }

        if (avaliacao.getTextoAvaliacao() == null || avaliacao.getTextoAvaliacao().trim().isEmpty()) {
            throw new IllegalArgumentException("Texto da avaliação é obrigatório");
        }

        avaliacao.setJogo(jogo);
        avaliacao.setUsuario(usuario);
        if (avaliacao.getCurtidas() == null) {
            avaliacao.setCurtidas(0);
        }
        if (avaliacao.getHorasJogadas() == null) {
            avaliacao.setHorasJogadas(0);
        }

        avaliacao.setIndiceRecomendacao(avaliacao.getNota() >= 4);

        return avaliacaoRepositorio.save(avaliacao);
    }

    public AvaliacaoUsuario atualizar(Long jogoId, Long avaliacaoId, AvaliacaoUsuario avaliacaoAtualizada) {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.findById(avaliacaoId).orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada com ID: " + avaliacaoId));

        if (!avaliacao.getJogo().getId().equals(jogoId)) {
            throw new IllegalArgumentException("Esta avaliação não pertence ao jogo informado");
        }

        if (avaliacaoAtualizada.getNota() >= 1 && avaliacaoAtualizada.getNota() <= 5) {
            avaliacao.setNota(avaliacaoAtualizada.getNota());
            avaliacao.setIndiceRecomendacao(avaliacao.getNota() >= 4);
        }

        if (avaliacaoAtualizada.getTextoAvaliacao() != null && !avaliacaoAtualizada.getTextoAvaliacao().trim().isEmpty()) {
            avaliacao.setTextoAvaliacao(avaliacaoAtualizada.getTextoAvaliacao());
        }
        return avaliacaoRepositorio.save(avaliacao);
    }

    public void deletar(Long jogoId, Long avaliacaoId) {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.findById(avaliacaoId).orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        if (!avaliacao.getJogo().getId().equals(jogoId)) {
            throw new IllegalArgumentException("Esta avaliação não pertence ao jogo informado");
        }
        avaliacaoRepositorio.deleteById(avaliacaoId);
    }

    public boolean isRecomendado(AvaliacaoUsuario avaliacao) {
        return avaliacao.isRecomendado();
    }

    public AvaliacaoUsuario curtir(Long avaliacaoId) {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.findById(avaliacaoId).orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        avaliacao.curtir();
        return avaliacaoRepositorio.save(avaliacao);
    }
}