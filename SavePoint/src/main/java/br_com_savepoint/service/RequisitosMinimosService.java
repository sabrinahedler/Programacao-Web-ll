package br_com_savepoint.service;

import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.RequisitosMinimos;
import br_com_savepoint.repository.JogoRepository;
import br_com_savepoint.repository.RequisitosMinimosRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequisitosMinimosService {

    private final RequisitosMinimosRepository requisitosRepository;
    private final JogoRepository jogoRepository;

    public RequisitosMinimosService(RequisitosMinimosRepository requisitosRepository, JogoRepository jogoRepository) {
        this.requisitosRepository = requisitosRepository;
        this.jogoRepository = jogoRepository;
    }

    public Optional<RequisitosMinimos> buscarPorJogoId(Long jogoId) {
        return requisitosRepository.findByJogoId(jogoId);
    }

    public RequisitosMinimos salvar(Long jogoId, RequisitosMinimos requisitos) {
        Optional<Jogo> jogoOptional = jogoRepository.findById(jogoId);

        if (jogoOptional.isPresent()) {
            Jogo jogo = jogoOptional.get();

            requisitos.setJogo(jogo);
            jogo.setRequisitosMinimos(requisitos);

            RequisitosMinimos requisitosSalvos = requisitosRepository.save(requisitos);
            jogoRepository.save(jogo);

            return requisitosSalvos;
        }

        throw new IllegalArgumentException("Jogo não encontrado com o ID: " + jogoId);
    }

    public Optional<RequisitosMinimos> atualizar(Long jogoId, RequisitosMinimos novosDados) {
        Optional<RequisitosMinimos> requisitosOptional = requisitosRepository.findByJogoId(jogoId);

        if (requisitosOptional.isPresent()) {
            RequisitosMinimos existente = requisitosOptional.get();

            existente.setProcessador(novosDados.getProcessador());
            existente.setMemoria(novosDados.getMemoria());
            existente.setPlacaDeVideo(novosDados.getPlacaDeVideo());
            existente.setSistemaOperacional(novosDados.getSistemaOperacional());

            return Optional.of(requisitosRepository.save(existente));
        }
        return Optional.empty();
    }

    public boolean deletarPorJogoId(Long jogoId) {
        Optional<Jogo> jogoOptional = jogoRepository.findById(jogoId);

        if (jogoOptional.isPresent()) {
            Jogo jogo = jogoOptional.get();
            if (jogo.getRequisitosMinimos() != null) {
                jogo.setRequisitosMinimos(null);
                jogoRepository.save(jogo);
                requisitosRepository.deleteByJogoId(jogoId);
                return true;
            }
        }
        return false;
    }
}