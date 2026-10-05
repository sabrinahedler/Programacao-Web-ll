package br_com_savepoint.repository;

import br_com_savepoint.model.AvaliacaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoUsuarioRepository extends JpaRepository<AvaliacaoUsuario, Long> {

    List<AvaliacaoUsuario> findByJogoId(Long jogoId);

    List<AvaliacaoUsuario> findByUsuarioId(Long usuarioId);

    void deleteByJogoId(Long jogoId);

    void deleteByUsuarioId(Long usuarioId);

    long countByJogoId(Long jogoId);

    long countByJogoIdAndNotaGreaterThanEqual(Long jogoId, int nota);

    @Query("SELECT AVG(a.nota) FROM AvaliacaoUsuario a WHERE a.jogo.id = :jogoId")
    Double calcularMediaPorJogo(@Param("jogoId") Long jogoId);
}
