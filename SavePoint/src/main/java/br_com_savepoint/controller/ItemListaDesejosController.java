package br_com_savepoint.controller;

import br_com_savepoint.service.ItemListaDesejosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    public ResponseEntity<br_com_savepoint.model.ItemListaDesejos> adicionarItem(
            @PathVariable("id") Long id,
            @RequestBody br_com_savepoint.model.ItemListaDesejos itemDados) {

        try {
            br_com_savepoint.model.ItemListaDesejos novoItem =
                    itemListaDesejosService.adicionarItem(id, itemDados);

            return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuarios/{id}/lista-desejos/itens/{itemId}")
    public ResponseEntity<br_com_savepoint.model.ItemListaDesejos> buscarItem(
            @PathVariable("id") Long id,
            @PathVariable("itemId") Long itemId) {

        Optional<br_com_savepoint.model.ItemListaDesejos> itemOptional =
                itemListaDesejosService.buscarItem(id, itemId);

        if (itemOptional.isPresent()) {
            return ResponseEntity.ok(itemOptional.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/usuarios/{id}/lista-desejos/itens/{itemId}")
    public ResponseEntity<br_com_savepoint.model.ItemListaDesejos> atualizarItem(
            @PathVariable("id") Long id,
            @PathVariable("itemId") Long itemId,
            @RequestBody br_com_savepoint.model.ItemListaDesejos itemDados) {

        try {
            Optional<br_com_savepoint.model.ItemListaDesejos> itemOptional =
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
}
