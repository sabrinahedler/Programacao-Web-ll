
package br_com_savepoint.repository;

import br_com_savepoint.model.ItemListaDesejos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemListaDesejosRepository extends JpaRepository<ItemListaDesejos, Long> {

    List<ItemListaDesejos> findByListaDesejosId(Long listaDesejosId);

    void deleteByListaDesejosIdAndJogoId(Long listaDesejosId, Long jogoId);
}