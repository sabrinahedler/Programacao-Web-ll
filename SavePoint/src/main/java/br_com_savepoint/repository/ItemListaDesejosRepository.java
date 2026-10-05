package br_com_savepoint.repository;

import br_com_savepoint.model.ItemListaDesejos;
import br_com_savepoint.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemListaDesejosRepository extends JpaRepository<ItemListaDesejos, Long> {

    boolean existsByListaDesejosIdAndJogoId(Long listaDesejosId, Long jogoId);

    @Query("SELECT DISTINCT i.listaDesejos.usuario FROM ItemListaDesejos i WHERE i.jogo.id = :jogoId")
    List<Usuario> buscarUsuariosPorJogo(@Param("jogoId") Long jogoId);
}
