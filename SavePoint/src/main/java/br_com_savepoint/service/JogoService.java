package br_com_savepoint.service;

import br_com_savepoint.model.Jogo;
import br_com_savepoint.repository.JogoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;

    public JogoService(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }

    public List<Jogo> listarTodos() {
        return jogoRepository.findAll();
    }

    public Optional<Jogo> buscarPorId(Long id) {
        return jogoRepository.findById(id);
    }

    public Jogo salvar(Jogo jogo) {
        if (jogo.getTitulo() == null || jogo.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("O título do jogo não pode ser vazio.");
        }
        return jogoRepository.save(jogo);
    }

    public Optional<Jogo> atualizar(Long id, Jogo jogoAtualizado) {

        Optional<Jogo> jogoOptional = jogoRepository.findById(id);

        if (jogoOptional.isPresent()) {
            Jogo jogoExistente = jogoOptional.get();
            jogoExistente.setTitulo(jogoAtualizado.getTitulo());
            jogoExistente.setDescricao(jogoAtualizado.getDescricao());
            jogoExistente.setDataLancamento(jogoAtualizado.getDataLancamento());
            jogoExistente.setImagemCapa(jogoAtualizado.getImagemCapa());
            jogoExistente.setClassificacaoIndicativa(jogoAtualizado.getClassificacaoIndicativa());
            jogoExistente.setDesenvolvedora(jogoAtualizado.getDesenvolvedora());
            jogoExistente.setGenero(jogoAtualizado.getGenero());

            Jogo jogoSalvo = jogoRepository.save(jogoExistente);

            return Optional.of(jogoSalvo);
        }
        return Optional.empty();
    }

    public boolean deletar(Long id) {
        if (jogoRepository.existsById(id)) {
            jogoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}