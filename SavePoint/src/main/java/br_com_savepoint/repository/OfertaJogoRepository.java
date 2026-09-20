package br_com_savepoint.repository;

import br_com_savepoint.model.OfertaJogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfertaJogoRepository extends JpaRepository<OfertaJogo, Long> {

    List<OfertaJogo> findByLojaId(Long lojaId);

    List<OfertaJogo> findByJogoIdOrderByPrecoAtualAsc(Long jogoId);
}