package br_com_savepoint.exception;

/** Indica que os dados enviados violam uma regra de negócio ou de validação. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}