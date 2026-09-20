package br_com_savepoint.service;

import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.OfertaJogo;
import br_com_savepoint.repository.JogoRepository;
import br_com_savepoint.repository.OfertaJogoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfertaJogoService {

    private final OfertaJogoRepository ofertaJogoRepository;
    private final JogoRepository jogoRepository;

    public OfertaJogoService(OfertaJogoRepository ofertaJogoRepository, JogoRepository jogoRepository) {
        this.ofertaJogoRepository = ofertaJogoRepository;
        this.jogoRepository = jogoRepository;
    }

    public OfertaJogo cadastrarOferta(Long jogoId, OfertaJogo novaOferta) {
        Optional<Jogo> jogoOptional = jogoRepository.findById(jogoId);

        if (jogoOptional.isPresent()) {
            Jogo jogoExistente = jogoOptional.get();
            novaOferta.setJogo(jogoExistente);

            if (novaOferta.getPrecoOriginal() > 0 && novaOferta.getPrecoAtual() > 0) {
                float desconto = (float) ((1 - (novaOferta.getPrecoAtual() / novaOferta.getPrecoOriginal())) * 100);
                novaOferta.setPercentualDesconto(desconto);
            }

            return ofertaJogoRepository.save(novaOferta);
        }

        throw new IllegalArgumentException("Jogo não encontrado com o ID: " + jogoId);
    }

    public List<OfertaJogo> compararPrecosPorJogo(Long jogoId) {
        return ofertaJogoRepository.findByJogoIdOrderByPrecoAtualAsc(jogoId);
    }

    public List<OfertaJogo> listarPorLoja(Long lojaId) {
        return ofertaJogoRepository.findByLojaId(lojaId);
    }

    public Optional<OfertaJogo> buscarPorId(Long ofertaId) {
        return ofertaJogoRepository.findById(ofertaId);
    }

    public boolean deletar(Long ofertaId) {
        if (ofertaJogoRepository.existsById(ofertaId)) {
            ofertaJogoRepository.deleteById(ofertaId);
            return true;
        }
        return false;
    }
}