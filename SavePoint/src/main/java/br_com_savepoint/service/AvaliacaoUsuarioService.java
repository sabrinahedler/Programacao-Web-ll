package br_com_savepoint.service;

import br_com_savepoint.model.AvaliacaoUsuario;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.AvaliacaoUsuarioRepository;
import br_com_savepoint.repository.JogoRepository;
import br_com_savepoint.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoUsuarioService {

    @Autowired
    private AvaliacaoUsuarioRepository avaliacaoRepositorio;

    @Autowired
    private UsuarioRepository usuarioRepositorio;

    @Autowired
    private JogoRepository jogoRepositorio;

    // LISTAGENS

    public List<AvaliacaoUsuario> listarTodas() {
        return avaliacaoRepositorio.findAll();
    }

    // BUSCAS 

    public AvaliacaoUsuario buscarPorId(Long id) {
        return avaliacaoRepositorio.findById(id).orElse(null);
    }

    public List<AvaliacaoUsuario> buscarPorJogo(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.findById(jogoId).orElse(null);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        return avaliacaoRepositorio.findByJogoId(jogoId);
    }

    public List<AvaliacaoUsuario> buscarPorUsuario(Long usuarioId) throws Exception {
        Usuario usuario = usuarioRepositorio.findById(usuarioId).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + usuarioId);
        }
        return avaliacaoRepositorio.findByUsuarioId(usuarioId);
    }

    //  CÁLCULOS

    public double calcularMediaJogo(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.findById(jogoId).orElse(null);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        Double media = avaliacaoRepositorio.calcularMediaJogo(jogoId);
        return media != null ? media : 0.0;
    }

    public double calcularIndiceRecomendacao(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.findById(jogoId).orElse(null);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        Double indice = avaliacaoRepositorio.calcularPercentualRecomendacao(jogoId);
        return indice != null ? indice : 0.0;
    }

    public long contarPorJogo(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.findById(jogoId).orElse(null);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        return avaliacaoRepositorio.countByJogoId(jogoId);
    }

    //  CRUD

    public AvaliacaoUsuario salvar(AvaliacaoUsuario avaliacao) throws Exception {
        if (avaliacao.getUsuario() == null) {
            throw new Exception("Usuário é obrigatório");
        }
        if (avaliacao.getJogo() == null) {
            throw new Exception("Jogo é obrigatório");
        }

        Usuario usuario = usuarioRepositorio.findById(avaliacao.getUsuario().getId()).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + avaliacao.getUsuario().getId());
        }

        Jogo jogo = jogoRepositorio.findById(avaliacao.getJogo().getId()).orElse(null);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + avaliacao.getJogo().getId());
        }

        if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new Exception("Nota deve ser entre 1 e 5");
        }

        if (avaliacao.getTextoAvaliacao() == null || avaliacao.getTextoAvaliacao().trim().isEmpty()) {
            throw new Exception("Texto da avaliação é obrigatório");
        }

        return avaliacaoRepositorio.save(avaliacao);
    }

    public AvaliacaoUsuario atualizar(Long id, AvaliacaoUsuario avaliacaoAtualizada) throws Exception {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.findById(id).orElse(null);
        if (avaliacao == null) {
            throw new Exception("Avaliação não encontrada com ID: " + id);
        }

        if (avaliacaoAtualizada.getNota() >= 1 && avaliacaoAtualizada.getNota() <= 5) {
            avaliacao.setNota(avaliacaoAtualizada.getNota());
        }

        if (avaliacaoAtualizada.getTextoAvaliacao() != null &&
            !avaliacaoAtualizada.getTextoAvaliacao().trim().isEmpty()) {
            avaliacao.setTextoAvaliacao(avaliacaoAtualizada.getTextoAvaliacao());
        }

        return avaliacaoRepositorio.save(avaliacao);
    }

    public void deletar(Long id) throws Exception {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.findById(id).orElse(null);
        if (avaliacao == null) {
            throw new Exception("Avaliação não encontrada com ID: " + id);
        }
        avaliacaoRepositorio.deleteById(id);
    }

    public void deletarPorJogo(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.findById(jogoId).orElse(null);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        avaliacaoRepositorio.deleteByJogoId(jogoId);
    }

    public void deletarPorUsuario(Long usuarioId) throws Exception {
        Usuario usuario = usuarioRepositorio.findById(usuarioId).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + usuarioId);
        }
        avaliacaoRepositorio.deleteByUsuarioId(usuarioId);
    }

    // CURTIDAS/ VOTOS 

    public AvaliacaoUsuario curtir(Long id) throws Exception {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.findById(id).orElse(null);
        if (avaliacao == null) {
            throw new Exception("Avaliação não encontrada com ID: " + id);
        }
        avaliacao.curtir();
        return avaliacaoRepositorio.save(avaliacao);
    }

    public AvaliacaoUsuario marcarComoUtil(Long id) throws Exception {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.findById(id).orElse(null);
        if (avaliacao == null) {
            throw new Exception("Avaliação não encontrada com ID: " + id);
        }
        avaliacao.marcarComoUtil();
        return avaliacaoRepositorio.save(avaliacao);
    }

    // MÉT. AUXILIARES 

    public boolean isRecomendado(AvaliacaoUsuario avaliacao) {
        return avaliacao.isRecomendado();
    }
}