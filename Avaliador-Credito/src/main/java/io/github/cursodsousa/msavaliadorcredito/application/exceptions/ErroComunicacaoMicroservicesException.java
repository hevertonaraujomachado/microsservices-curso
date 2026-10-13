package io.github.cursodsousa.msavaliadorcredito.application.exceptions;

import lombok.Getter;

public class ErroComunicacaoMicroservicesException extends RuntimeException {
    @Getter
    private Integer status;

    public ErroComunicacaoMicroservicesException(String msg, Integer status) {
        super(msg);
        this.status = status;

    }
}
