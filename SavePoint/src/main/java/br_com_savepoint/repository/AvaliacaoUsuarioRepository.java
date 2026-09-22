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

    @Query("SELECT AVG(a.nota) FROM AvaliacaoUsuario a WHERE a.jogo.id = :jogoId")
    Double calcularMediaJogo(@Param("jogoId") Long jogoId);

    // NOVO: Calcula o percentual de recomendação (nota >= 4)
    @Query("SELECT (COUNT(a) * 100.0 / (SELECT COUNT(b) FROM AvaliacaoUsuario b WHERE b.jogo.id = :jogoId)) " +
           "FROM AvaliacaoUsuario a WHERE a.jogo.id = :jogoId AND a.nota >= 4")
    Double calcularPercentualRecomendacao(@Param("jogoId") Long jogoId);
}