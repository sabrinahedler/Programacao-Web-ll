package br_com_savepoint.controller;

import br_com_savepoint.model.ItemListaDesejos;
import br_com_savepoint.service.ItemListaDesejosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuarios/{id}/lista-desejos/itens")
public class ItemListaDesejosController {

    private final ItemListaDesejosService itemListaDesejosService;

    public ItemListaDesejosController(ItemListaDesejosService itemListaDesejosService) {
        this.itemListaDesejosService = itemListaDesejosService;
    }

    @PostMapping
    public ResponseEntity<ItemListaDesejos> adicionarItem(@PathVariable("id") Long usuarioId, @RequestBody ItemListaDesejos itemDados) {
        try {
            ItemListaDesejos novoItem = itemListaDesejosService.adicionarItem(usuarioId, itemDados);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemListaDesejos> buscarItem(@PathVariable("id") Long usuarioId, @PathVariable("itemId") Long itemId) {
        Optional<ItemListaDesejos> itemOptional = itemListaDesejosService.buscarItem(usuarioId, itemId);
        if (itemOptional.isPresent()) {
            return ResponseEntity.ok(itemOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemListaDesejos> atualizarItem(@PathVariable("id") Long usuarioId, @PathVariable("itemId") Long itemId, @RequestBody ItemListaDesejos itemDados) {
        try {
            Optional<ItemListaDesejos> itemOptional = itemListaDesejosService.atualizarItem(usuarioId, itemId, itemDados);
            if (itemOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(itemOptional.get());
            }
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable("id") Long usuarioId, @PathVariable("itemId") Long itemId) {
        boolean deletado = itemListaDesejosService.removerItem(usuarioId, itemId);
        if (deletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
