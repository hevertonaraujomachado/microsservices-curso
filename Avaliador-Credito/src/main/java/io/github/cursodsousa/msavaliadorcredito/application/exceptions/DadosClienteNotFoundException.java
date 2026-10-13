package io.github.cursodsousa.msavaliadorcredito.application.exceptions;

public class DadosClienteNotFoundException extends RuntimeException {
    public DadosClienteNotFoundException(String message) {
        super("Dados do cliente não encontrado para o CPF informado: " + message);
    }
}
