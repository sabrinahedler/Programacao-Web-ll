package br_com_savepoint.controller;

import br_com_savepoint.model.ItemListaDesejos;
import br_com_savepoint.model.UserRoles;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.service.ItemListaDesejosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class ItemListaDesejosController {

    private final ItemListaDesejosService itemListaDesejosService;

    public ItemListaDesejosController(
            ItemListaDesejosService itemListaDesejosService) {
        this.itemListaDesejosService = itemListaDesejosService;
    }

    @PostMapping("/usuarios/{id}/lista-desejos/itens")
    public ResponseEntity<ItemListaDesejos> adicionarItem(
            @PathVariable("id") Long id,
            @RequestBody ItemListaDesejos itemDados,
            @AuthenticationPrincipal Usuario logado) {

        if (!podeAcessar(logado, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            ItemListaDesejos novoItem =
                    itemListaDesejosService.adicionarItem(id, itemDados);

            return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuarios/{id}/lista-desejos/itens/{itemId}")
    public ResponseEntity<ItemListaDesejos> buscarItem(
            @PathVariable("id") Long id,
            @PathVariable("itemId") Long itemId,
            @AuthenticationPrincipal Usuario logado) {

        if (!podeAcessar(logado, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<ItemListaDesejos> itemOptional =
                itemListaDesejosService.buscarItem(id, itemId);

        if (itemOptional.isPresent()) {
            return ResponseEntity.ok(itemOptional.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/usuarios/{id}/lista-desejos/itens/{itemId}")
    public ResponseEntity<ItemListaDesejos> atualizarItem(
            @PathVariable("id") Long id,
            @PathVariable("itemId") Long itemId,
            @RequestBody ItemListaDesejos itemDados,
            @AuthenticationPrincipal Usuario logado) {

        if (!podeAcessar(logado, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Optional<ItemListaDesejos> itemOptional =
                    itemListaDesejosService.atualizarItem(
                            id, itemId, itemDados);

            if (itemOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(itemOptional.get());
            }

            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // remove um único jogo da lista de desejos
    @DeleteMapping("/usuarios/{id}/lista-desejos/itens/{itemId}")
    public ResponseEntity<Void> removerItem(
            @PathVariable("id") Long id,
            @PathVariable("itemId") Long itemId,
            @AuthenticationPrincipal Usuario logado) {

        if (!podeAcessar(logado, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (itemListaDesejosService.removerItem(id, itemId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // cada usuário só mexe na própria lista, ADMIN mexe em qualquer uma
    private boolean podeAcessar(Usuario logado, Long usuarioId) {
        return logado != null
                && (logado.getRole() == UserRoles.ADMIN || logado.getId().equals(usuarioId));
    }
}
