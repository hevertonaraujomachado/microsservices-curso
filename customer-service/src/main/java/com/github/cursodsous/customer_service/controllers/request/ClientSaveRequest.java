package com.github.cursodsous.customer_service.controllers.request;

import com.github.cursodsous.customer_service.domain.Client;
import lombok.Data;

@Data
public class ClientSaveRequest {
    private Long id;
    private String cpf;
    private String nome;
    private Integer idade;

    public Client toModel(){
        return new Client(id,cpf, nome, idade);
    }
}
