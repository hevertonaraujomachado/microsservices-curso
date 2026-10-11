package io.github.cursodsousa.mscartoes.controllers;

import io.github.cursodsousa.mscartoes.controllers.request.CartaoSaveRequest;
import io.github.cursodsousa.mscartoes.controllers.response.CartoesPorClienteResponse;
import io.github.cursodsousa.mscartoes.domain.Cartao;
import io.github.cursodsousa.mscartoes.domain.ClienteCartao;
import io.github.cursodsousa.mscartoes.services.CartaoService;
import io.github.cursodsousa.mscartoes.services.ClienteCartaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cartoes")
public class CartoesController {

    private final CartaoService cartaoService;
    private final ClienteCartaoService clienteCartaoService;

    public CartoesController(CartaoService cartaoService, ClienteCartaoService clienteCartaoService) {
        this.cartaoService = cartaoService;
        this.clienteCartaoService = clienteCartaoService;
    }

    @GetMapping
    public String status(){
        return "ok";
    }

    @PostMapping
    public ResponseEntity cadastra(@RequestBody CartaoSaveRequest request ){
        Cartao cartao = request.toModel();
        cartaoService.save(cartao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAteh(@RequestParam("renda") Long renda){
        List<Cartao> list = cartaoService.getCartoesRendaMenorIgual(renda);
        return ResponseEntity.ok(list);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<CartoesPorClienteResponse>> getCartoesByCliente(
            @RequestParam("cpf") String cpf){
        List<ClienteCartao> lista = clienteCartaoService.listCartoesByCpf(cpf);
        List<CartoesPorClienteResponse> resultList = lista.stream()
                .map(CartoesPorClienteResponse::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultList);
    }

}
