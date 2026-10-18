package br_com_savepoint.controller;

import br_com_savepoint.model.Usuario;
import br_com_savepoint.service.ListaDesejosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ListaDesejosController {

    private final ListaDesejosService listaDesejosService;

    public ListaDesejosController(ListaDesejosService listaDesejosService) {
        this.listaDesejosService = listaDesejosService;
    }

    @GetMapping("/jogos/{idJogo}/usuarios-interessados")
    public ResponseEntity<List<Usuario>> buscarUsuariosInteressados(
            @PathVariable("idJogo") Long idJogo) {

        try {
            List<Usuario> usuarios =
                    listaDesejosService.buscarUsuariosInteressados(idJogo);

            return ResponseEntity.ok(usuarios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/usuarios/{id}/lista-desejos/")
    public ResponseEntity<br_com_savepoint.model.ListaDesejos> criarLista(
            @PathVariable("id") Long id) {

        try {
            br_com_savepoint.model.ListaDesejos lista =
                    listaDesejosService.criarLista(id);

            return ResponseEntity.status(HttpStatus.OK).body(lista);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/usuarios/{usuarioId}/lista-desejos/{listaId}")
    public ResponseEntity<Void> deletarLista(
            @PathVariable("usuarioId") Long usuarioId,
            @PathVariable("listaId") Long listaId) {

        boolean deletada =
                listaDesejosService.deletarLista(usuarioId, listaId);

        if (deletada) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
