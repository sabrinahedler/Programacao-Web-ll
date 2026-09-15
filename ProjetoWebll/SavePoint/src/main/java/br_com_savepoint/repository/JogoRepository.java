
package br_com_savepoint.repository;

import br_com_savepoint.model.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {

    List<Jogo> findByGeneroIgnoreCase(String genero);

    List<Jogo> findByTituloContainingIgnoreCase(String titulo);

    List<Jogo> findByDesenvolvedoraIgnoreCase(String desenvolvedora);
}