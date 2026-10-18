package br_com_savepoint.util;

import br_com_savepoint.exception.RegraNegocioException;

/** Métodos auxiliares de validação de campos obrigatórios. */
public final class Validacao {

    private Validacao() {
    }

    /** Exige que o texto não seja nulo nem vazio. */
    public static void exigirTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new RegraNegocioException("O campo '" + campo + "' é obrigatório.");
        }
    }

    /** Exige que o objeto não seja nulo. */
    public static void exigirPreenchido(Object valor, String campo) {
        if (valor == null) {
            throw new RegraNegocioException("O campo '" + campo + "' é obrigatório.");
        }
    }

    /** Exige que o número não seja negativo. */
    public static void exigirNaoNegativo(double valor, String campo) {
        if (valor < 0) {
            throw new RegraNegocioException("O campo '" + campo + "' não pode ser negativo.");
        }
    }

    /** Exige que o número seja maior que zero. */
    public static void exigirPositivo(double valor, String campo) {
        if (valor <= 0) {
            throw new RegraNegocioException("O campo '" + campo + "' deve ser maior que zero.");
        }
    }
}