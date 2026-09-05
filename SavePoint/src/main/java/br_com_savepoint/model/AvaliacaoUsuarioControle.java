package br_com_savepoint.model;

import br.com.savepoint.savepoint.model.AvaliacaoUsuario;
import br.com.savepoint.savepoint.service.AvaliacaoUsuarioServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoUsuarioControle {

    @Autowired
    private AvaliacaoUsuarioServico avaliacaoServico;

    @GetMapping
    public ResponseEntity<List<AvaliacaoUsuario>> listarTodas() {
        List<AvaliacaoUsuario> avaliacoes = avaliacaoServico.listarTodas();
        return new ResponseEntity<List<AvaliacaoUsuario>>(avaliacoes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoUsuario> buscarPorId(@PathVariable Long id) {
        AvaliacaoUsuario avaliacao = avaliacaoServico.buscarPorId(id);
        if (avaliacao != null) {
            return new ResponseEntity<AvaliacaoUsuario>(avaliacao, HttpStatus.OK);
        } else {
            return new ResponseEntity<AvaliacaoUsuario>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/jogo/{jogoId}")
    public ResponseEntity<?> buscarPorJogo(@PathVariable Long jogoId) {
        try {
            List<AvaliacaoUsuario> avaliacoes = avaliacaoServico.buscarPorJogo(jogoId);
            return new ResponseEntity<List<AvaliacaoUsuario>>(avaliacoes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> buscarPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<AvaliacaoUsuario> avaliacoes = avaliacaoServico.buscarPorUsuario(usuarioId);
            return new ResponseEntity<List<AvaliacaoUsuario>>(avaliacoes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/jogo/{jogoId}/media")
    public ResponseEntity<?> calcularMedia(@PathVariable Long jogoId) {
        try {
            double media = avaliacaoServico.calcularMediaJogo(jogoId);
            return new ResponseEntity<Double>(media, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/jogo/{jogoId}/indice-recomendacao")
    public ResponseEntity<?> calcularIndiceRecomendacao(@PathVariable Long jogoId) {
        try {
            double indice = avaliacaoServico.calcularIndiceRecomendacao(jogoId);
            return new ResponseEntity<Double>(indice, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/jogo/{jogoId}/count")
    public ResponseEntity<?> contarPorJogo(@PathVariable Long jogoId) {
        try {
            long count = avaliacaoServico.contarPorJogo(jogoId);
            return new ResponseEntity<Long>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody AvaliacaoUsuario avaliacao) {
        try {
            AvaliacaoUsuario novaAvaliacao = avaliacaoServico.salvar(avaliacao);
            return new ResponseEntity<AvaliacaoUsuario>(novaAvaliacao, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody AvaliacaoUsuario avaliacao) {
        try {
            AvaliacaoUsuario avaliacaoAtualizada = avaliacaoServico.atualizar(id, avaliacao);
            return new ResponseEntity<AvaliacaoUsuario>(avaliacaoAtualizada, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            avaliacaoServico.deletar(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/jogo/{jogoId}")
    public ResponseEntity<?> deletarPorJogo(@PathVariable Long jogoId) {
        try {
            avaliacaoServico.deletarPorJogo(jogoId);
            return new ResponseEntity<String>("Avaliações do jogo removidas com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> deletarPorUsuario(@PathVariable Long usuarioId) {
        try {
            avaliacaoServico.deletarPorUsuario(usuarioId);
            return new ResponseEntity<String>("Avaliações do usuário removidas com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/curtir")
    public ResponseEntity<?> curtir(@PathVariable Long id) {
        try {
            AvaliacaoUsuario avaliacao = avaliacaoServico.curtir(id);
            return new ResponseEntity<AvaliacaoUsuario>(avaliacao, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/util")
    public ResponseEntity<?> marcarComoUtil(@PathVariable Long id) {
        try {
            AvaliacaoUsuario avaliacao = avaliacaoServico.marcarComoUtil(id);
            return new ResponseEntity<AvaliacaoUsuario>(avaliacao, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/recomendado")
    public ResponseEntity<?> isRecomendado(@PathVariable Long id) {
        AvaliacaoUsuario avaliacao = avaliacaoServico.buscarPorId(id);
        if (avaliacao != null) {
            boolean recomendado = avaliacaoServico.isRecomendado(avaliacao);
            return new ResponseEntity<Boolean>(recomendado, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Avaliação não encontrada", HttpStatus.NOT_FOUND);
        }
    }
}