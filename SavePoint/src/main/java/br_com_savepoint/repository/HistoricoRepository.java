
package br_com_savepoint.repository;

import br_com_savepoint.model.Historico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico, Long> {

    List<Historico> findByOfertaIdOrderByDataDesc(Long ofertaId);
}
