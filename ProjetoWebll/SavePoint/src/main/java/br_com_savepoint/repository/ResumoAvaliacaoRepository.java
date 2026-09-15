package br_com_savepoint.repository;

import br_com_savepoint.model.ResumoAvaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumoAvaliacaoRepository extends JpaRepository<ResumoAvaliacao, Long> {


    Optional<ResumoAvaliacao> findByJogoId(Long jogoId);

    void deleteByJogoId(Long jogoId);
}