package yan.api.sizebay.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yan.api.sizebay.dto.extrato.ExtratoResponse;
import yan.api.sizebay.dto.transacao.TransacaoRequest;
import yan.api.sizebay.dto.transacao.TransacaoResponse;
import yan.api.sizebay.exception.ClienteNaoEncontradoException;
import yan.api.sizebay.exception.SaldoInsuficienteException;
import yan.api.sizebay.model.Transacao;
import yan.api.sizebay.model.enums.TipoTransacao;
import yan.api.sizebay.repository.ClienteJdbcRepository;
import yan.api.sizebay.repository.TransacaoJdbcRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService service;

    @Mock
    private TransacaoJdbcRepository transacaoRepository;

    @Mock
    private ClienteJdbcRepository clienteRepository;

    @Test
    void shouldReturnSuccessWhenTransactionIsValid() {
        Integer clienteId = 1;
        TransacaoRequest request = new TransacaoRequest(100, "c", "descricao");
        TransacaoResponse expectedResponse = new TransacaoResponse(1000, 100);

        when(transacaoRepository.createTransaction(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(expectedResponse);

        TransacaoResponse result = service.processTransaction(clienteId, request);

        assertNotNull(result);
        assertEquals(expectedResponse.limite(), result.limite());
        assertEquals(expectedResponse.saldo(), result.saldo());
        verify(transacaoRepository).createTransaction(clienteId, request.valor(), request.descricao(), request.tipo());
    }

    @Test
    void shouldThrowClientNotFoundExceptionWhenTransactionFailsAndClientDoesNotExist() {
        Integer clienteId = 99;
        TransacaoRequest request = new TransacaoRequest(100, "c", "descricao");

        when(transacaoRepository.createTransaction(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(null);
        when(clienteRepository.findSaldoCliente(clienteId))
                .thenReturn(null);

        assertThrows(ClienteNaoEncontradoException.class, () ->
                service.processTransaction(clienteId, request));
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenTransactionFailsButClientExists() {
        Integer clienteId = 1;
        TransacaoRequest request = new TransacaoRequest(1000000, "d", "descricao");

        when(transacaoRepository.createTransaction(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(null);
        when(clienteRepository.findSaldoCliente(clienteId))
                .thenReturn(Map.of("total", 0, "limite", 1000));

        assertThrows(SaldoInsuficienteException.class, () ->
                service.processTransaction(clienteId, request));
    }

    @Test
    void shouldReturnStatementWhenClientExists() {
        Integer clienteId = 1;
        Map<String, Object> dadosSaldo = Map.of("total", 100, "limite", 1000);
        List<Transacao> transacoes = List.of(
                Transacao.builder()
                        .valor(10)
                        .tipo("d")
                        .descricao("teste")
                        .realizadaEm(LocalDateTime.now())
                        .build()
        );

        when(clienteRepository.findSaldoCliente(clienteId)).thenReturn(dadosSaldo);
        when(transacaoRepository.buscarUltimasTransacoes(clienteId)).thenReturn(transacoes);

        ExtratoResponse result = service.getStatement(clienteId);

        assertNotNull(result);
        assertEquals(dadosSaldo.get("total"), result.saldo().total());
        assertEquals(dadosSaldo.get("limite"), result.saldo().limite());
        assertEquals(1, result.ultimasTransacoes().size());
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExistForStatement() {
        Integer clienteId = 99;

        when(clienteRepository.findSaldoCliente(clienteId)).thenReturn(null);

        assertThrows(ClienteNaoEncontradoException.class, () ->
                service.getStatement(clienteId));
    }
}