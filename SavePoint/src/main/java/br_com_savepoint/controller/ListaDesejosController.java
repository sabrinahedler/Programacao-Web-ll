package br_com_savepoint.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.model.ListaDesejos;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.service.ListaDesejosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Lista de desejos", description = "Lista de desejos do usuário")
@RestController
public class ListaDesejosController {

    private final ListaDesejosService listaDesejosService;

    public ListaDesejosController(ListaDesejosService listaDesejosService) {
        this.listaDesejosService = listaDesejosService;
    }

    @Operation(
            summary = "Listar usuários que desejam um jogo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários"),
                    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
            })
    @GetMapping("/jogos/{idJogo}/usuarios-interessados")
    public ResponseEntity<List<Usuario>> buscarUsuariosInteressados(@PathVariable("idJogo") Long jogoId) {
        return ResponseEntity.ok(listaDesejosService.buscarUsuariosInteressados(jogoId));
    }

    @Operation(
            summary = "Criar lista de desejos do usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista criada, ou a existente se o usuário já tiver uma"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            })
    @PostMapping("/usuarios/{id}/lista-desejos")
    public ResponseEntity<ListaDesejos> criarLista(@PathVariable("id") Long usuarioId) {
        return ResponseEntity.ok(listaDesejosService.criarLista(usuarioId));
    }

    @Operation(
            summary = "Excluir lista de desejos",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @DeleteMapping("/usuarios/{usuarioId}/lista-desejos/{listaId}")
    public ResponseEntity<Void> deletarLista(@PathVariable Long usuarioId, @PathVariable Long listaId) {
        listaDesejosService.deletarLista(usuarioId, listaId);
        return ResponseEntity.noContent().build();
    }
}
