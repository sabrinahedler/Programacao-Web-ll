
package br_com_savepoint.repository;

import br_com_savepoint.model.RequisitosMinimos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequisitosMinimosRepository extends JpaRepository<RequisitosMinimos, Long> {

    Optional<RequisitosMinimos> findByJogoId(Long jogoId);

    void deleteByJogoId(Long jogoId);
}