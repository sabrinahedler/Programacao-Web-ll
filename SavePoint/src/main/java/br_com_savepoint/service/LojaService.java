package br_com_savepoint.service;

import br_com_savepoint.model.Loja;
import br_com_savepoint.repository.LojaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LojaService {

    private final LojaRepository lojaRepository;

    public LojaService(LojaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    public List<Loja> listarTodas() {
        return lojaRepository.findAll();
    }

    public Optional<Loja> buscarPorId(Long id) {
        return lojaRepository.findById(id);
    }

    public Loja salvar(Loja loja) {
        if (loja.getNome() == null || loja.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da loja não pode ser vazio.");
        }

        Optional<Loja> lojaExistente = lojaRepository.findByNomeIgnoreCase(loja.getNome());
        if (lojaExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma loja cadastrada com este nome.");
        }

        return lojaRepository.save(loja);
    }

    public Optional<Loja> atualizar(Long id, Loja lojaAtualizada) {
        Optional<Loja> lojaOptional = lojaRepository.findById(id);

        if (lojaOptional.isPresent()) {
            Loja lojaExistente = lojaOptional.get();

            lojaExistente.setNome(lojaAtualizada.getNome());
            lojaExistente.setUrlLoja(lojaAtualizada.getUrlLoja());
            lojaExistente.setUrlLogo(lojaAtualizada.getUrlLogo());

            Loja lojaSalva = lojaRepository.save(lojaExistente);

            return Optional.of(lojaSalva);
        }

        return Optional.empty();
    }

    public boolean deletar(Long id) {
        if (lojaRepository.existsById(id)) {
            lojaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}