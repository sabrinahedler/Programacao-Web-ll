
package br_com_savepoint.repository;

import br_com_savepoint.model.ListaDesejos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListaDesejosRepository extends JpaRepository<ListaDesejos, Long> {

    Optional<ListaDesejos> findByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}