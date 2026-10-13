package io.github.cursodsousa.msavaliadorcredito.infra.clients;

import io.github.cursodsousa.msavaliadorcredito.domain.model.DadosCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "msclientes", path = "/clientes")
public interface ClienteResourceClient {
    @GetMapping("/clientes/{cpf}")
    ResponseEntity<DadosCliente> dadosCliente(@PathVariable String cpf);
}
