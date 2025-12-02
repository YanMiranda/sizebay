package yan.api.sizebay.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import yan.api.sizebay.dto.transacao.TransacaoRequest;
import yan.api.sizebay.exception.ClienteNaoEncontradoException;
import yan.api.sizebay.exception.SaldoInsuficienteException;
import yan.api.sizebay.model.Cliente;
import yan.api.sizebay.model.Transacao;
import yan.api.sizebay.model.enums.TipoTransacao;
import yan.api.sizebay.repository.ClienteRepository;
import yan.api.sizebay.repository.TransacaoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService service;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Test
    @DisplayName("Deve creditar valor e atualizar saldo com sucesso")
    void deveProcessarCreditoComSucesso() {
        Integer clienteId = 1;
        Integer saldoInicial = 0;
        Integer valorCredito = 1000;

        Cliente clienteMock = Cliente.builder()
                .id(clienteId)
                .limite(100000)
                .saldo(saldoInicial)
                .build();

        TransacaoRequest request = new TransacaoRequest(valorCredito, TipoTransacao.c, "deposito");

        when(clienteRepository.findByIdForUpdate(clienteId)).thenReturn(Optional.of(clienteMock));

        var response = service.processTransaction(clienteId, request);

        assertEquals(1000, response.saldo());
        assertEquals(100000, response.limite());
        verify(transacaoRepository, times(1)).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve debitar valor se houver limite")
    void deveProcessarDebitoComSucesso() {
        Integer clienteId = 1;
        Cliente clienteMock = Cliente.builder().id(clienteId).limite(1000).saldo(0).build();

        TransacaoRequest request = new TransacaoRequest(500, TipoTransacao.d, "compra");

        when(clienteRepository.findByIdForUpdate(clienteId)).thenReturn(Optional.of(clienteMock));

        var response = service.processTransaction(clienteId, request);

        assertEquals(-500, response.saldo());
        verify(transacaoRepository, times(1)).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve lançar SaldoInsuficienteException quando estourar o limite")
    void deveLancarErroSemLimite() {
        Integer clienteId = 1;
        Cliente clienteMock = Cliente.builder().id(clienteId).limite(1000).saldo(0).build();

        TransacaoRequest request = new TransacaoRequest(1001, TipoTransacao.d, "estouro");

        when(clienteRepository.findByIdForUpdate(clienteId)).thenReturn(Optional.of(clienteMock));

        assertThrows(SaldoInsuficienteException.class, () ->
                service.processTransaction(clienteId, request)
        );

        verify(transacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ClienteNaoEncontradoException se ID não existir")
    void deveLancarErroClienteInexistente() {
        Integer idInexistente = 99;
        TransacaoRequest request = new TransacaoRequest(10, TipoTransacao.c, "teste");

        when(clienteRepository.findByIdForUpdate(idInexistente)).thenReturn(Optional.empty());

        assertThrows(ClienteNaoEncontradoException.class, () ->
                service.processTransaction(idInexistente, request)
        );
    }

    @Test
    @DisplayName("Deve retornar extrato corretamente")
    void deveRetornarExtrato() {
        Integer clienteId = 1;
        Cliente clienteMock = Cliente.builder().id(clienteId).limite(1000).saldo(500).build();

        Transacao t1 = Transacao.builder().valor(10).tipo(TipoTransacao.c).descricao("t1").build();
        Transacao t2 = Transacao.builder().valor(20).tipo(TipoTransacao.d).descricao("t2").build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteMock));
        when(transacaoRepository.findByClienteIdOrderByRealizadaEmDesc(eq(clienteId), any(PageRequest.class)))
                .thenReturn(List.of(t1, t2));

        var extrato = service.getStatement(clienteId);

        assertEquals(500, extrato.saldo().total());
        assertEquals(2, extrato.ultimasTransacoes().size());
        assertEquals("t1", extrato.ultimasTransacoes().get(0).descricao());
    }
}