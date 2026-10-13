package io.github.cursodsousa.msavaliadorcredito.application.services;
import feign.FeignException;
import io.github.cursodsousa.msavaliadorcredito.application.exceptions.DadosClienteNotFoundException;
import io.github.cursodsousa.msavaliadorcredito.application.exceptions.ErroComunicacaoMicroservicesException;
import io.github.cursodsousa.msavaliadorcredito.application.exceptions.ErroSolicitacaoCartaoException;
import io.github.cursodsousa.msavaliadorcredito.domain.model.Cartao;
import io.github.cursodsousa.msavaliadorcredito.domain.model.CartaoAprovado;
import io.github.cursodsousa.msavaliadorcredito.domain.model.CartaoCliente;
import io.github.cursodsousa.msavaliadorcredito.domain.model.DadosCliente;
import io.github.cursodsousa.msavaliadorcredito.domain.model.DadosSolicitacaoEmissaoCartao;
import io.github.cursodsousa.msavaliadorcredito.domain.model.ProtocoloSolicitacaoCartao;
import io.github.cursodsousa.msavaliadorcredito.domain.model.RetornoAvaliacaoCliente;
import io.github.cursodsousa.msavaliadorcredito.domain.model.SituacaoCliente;
import io.github.cursodsousa.msavaliadorcredito.infra.clients.CartoesResourceClient;
import io.github.cursodsousa.msavaliadorcredito.infra.clients.ClienteResourceClient;
import io.github.cursodsousa.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AvaliadorCreditoService {

    private static final BigDecimal DIVISOR_IDADE = BigDecimal.TEN;

    private  ClienteResourceClient clientesClient;
    private  CartoesResourceClient cartoesClient;
    private  SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;

    public AvaliadorCreditoService(ClienteResourceClient clientesClient, CartoesResourceClient cartoesClient, SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher) {
        this.clientesClient = clientesClient;
        this.cartoesClient = cartoesClient;
        this.emissaoCartaoPublisher = emissaoCartaoPublisher;
    }

    public AvaliadorCreditoService(){

    }

    public SituacaoCliente obterSituacaoCliente(String cpf)
            throws DadosClienteNotFoundException,
            ErroComunicacaoMicroservicesException {

        try {
            DadosCliente cliente = clientesClient.dadosCliente(cpf).getBody();

            List<CartaoCliente> cartoes =
                    cartoesClient.getCartoesByCliente(cpf).getBody();

            return SituacaoCliente.builder()
                    .cliente(cliente)
                    .cartoes(cartoes)
                    .build();

        } catch (FeignException e) {
            tratarErroComunicacao(e);
            throw e;
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda)
            throws DadosClienteNotFoundException,
            ErroComunicacaoMicroservicesException {

        try {
            DadosCliente cliente = clientesClient.dadosCliente(cpf).getBody();

            List<Cartao> cartoes =
                    cartoesClient.getCartoesRendaAteh(renda).getBody();

            List<CartaoAprovado> cartoesAprovados = cartoes.stream()
                    .map(cartao -> calcularCartaoAprovado(cartao, cliente))
                    .toList();

            return new RetornoAvaliacaoCliente(cartoesAprovados);

        } catch (FeignException e) {
            tratarErroComunicacao(e);
            throw e;
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(
            DadosSolicitacaoEmissaoCartao dados) {

        try {
            emissaoCartaoPublisher.solicitarCartao(dados);

            String protocolo = UUID.randomUUID().toString();

            return new ProtocoloSolicitacaoCartao(protocolo);

        } catch (Exception e) {
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }

    private CartaoAprovado calcularCartaoAprovado(
            Cartao cartao,
            DadosCliente cliente) {

        BigDecimal idade = BigDecimal.valueOf(cliente.getIdade());

        BigDecimal fator = idade.divide(DIVISOR_IDADE);

        BigDecimal limiteAprovado =
                fator.multiply(cartao.getLimiteBasico());

        CartaoAprovado aprovado = new CartaoAprovado();
        aprovado.setCartao(cartao.getNome());
        aprovado.setBandeira(cartao.getBandeira());
        aprovado.setLimiteAprovado(limiteAprovado);

        return aprovado;
    }

    private void tratarErroComunicacao(FeignException e)
            throws DadosClienteNotFoundException,
            ErroComunicacaoMicroservicesException {

        int status = e.status();

        if (status == HttpStatus.NOT_FOUND.value()) {
            throw new DadosClienteNotFoundException("Cliente não encontrado");
        }

        throw new ErroComunicacaoMicroservicesException(
                e.getMessage(),
                status
        );
    }
}