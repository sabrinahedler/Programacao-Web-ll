package br_com_savepoint.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

/** Converte as exceções da aplicação em respostas HTTP padronizadas. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(RecursoNaoEncontradoException excecao) {
        return montarResposta(HttpStatus.NOT_FOUND, excecao.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> tratarRegraNegocio(RegraNegocioException excecao) {
        return montarResposta(HttpStatus.BAD_REQUEST, excecao.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResposta> tratarRequisicaoInvalida(Exception excecao) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Requisição inválida: verifique o corpo e os parâmetros enviados.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException excecao) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .findFirst().orElse("Dados inválidos.");
        return montarResposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratarIntegridade(DataIntegrityViolationException excecao) {
        return montarResposta(HttpStatus.CONFLICT, "A operação viola uma restrição de integridade dos dados.");
    }

    private ResponseEntity<ErroResposta> montarResposta(HttpStatus status, String mensagem) {
        ErroResposta corpo = new ErroResposta(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensagem);
        return ResponseEntity.status(status).body(corpo);
    }
}
