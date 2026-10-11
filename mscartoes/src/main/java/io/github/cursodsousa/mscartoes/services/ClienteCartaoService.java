package io.github.cursodsousa.mscartoes.services;

import io.github.cursodsousa.mscartoes.domain.ClienteCartao;
import io.github.cursodsousa.mscartoes.repositories.ClienteCartaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteCartaoService {

    private final ClienteCartaoRepository repository;

    public ClienteCartaoService(ClienteCartaoRepository repository) {
        this.repository = repository;
    }

    public List<ClienteCartao> listCartoesByCpf(String cpf){
        return repository.findByCpf(cpf);
}
}
