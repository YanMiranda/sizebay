package yan.api.sizebay.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yan.api.sizebay.dto.extrato.ExtratoResponse;
import yan.api.sizebay.dto.transacao.TransacaoRequest;
import yan.api.sizebay.dto.transacao.TransacaoResponse;
import yan.api.sizebay.exception.ClienteNaoEncontradoException;
import yan.api.sizebay.exception.SaldoInsuficienteException;
import yan.api.sizebay.repository.ClienteJdbcRepository;
import yan.api.sizebay.repository.TransacaoJdbcRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoJdbcRepository transacaoRepository;

    private final ClienteJdbcRepository clienteRepository;

    @Transactional
    public TransacaoResponse processTransaction(Integer clienteId, TransacaoRequest request) {
        return transacaoRepository.createTransaction(
                clienteId,
                request.valor(),
                request.descricao(),
                request.tipo()
        );
    }

    @Transactional(readOnly = true)
    public ExtratoResponse getStatement(Integer clienteId) {
        Map<String, Object> dadosSaldo = findSaldoByClienteId(clienteId);

        if (Objects.isNull(dadosSaldo)) {
            throw new ClienteNaoEncontradoException();
        }

        List<ExtratoResponse.TransacaoDto> ultimasTransacoes = transacaoRepository.buscarUltimasTransacoes(clienteId)
                .stream()
                .map(t -> new ExtratoResponse.TransacaoDto(
                        t.getValor(),
                        t.getTipo(),
                        t.getDescricao(),
                        t.getRealizadaEm()
                ))
                .toList();

        return new ExtratoResponse(
                new ExtratoResponse.SaldoDto(
                        (Integer) dadosSaldo.get("total"),
                        LocalDateTime.now(),
                        (Integer) dadosSaldo.get("limite")
                ),
                ultimasTransacoes
        );
    }

    public Map<String, Object> findSaldoByClienteId(Integer clienteId) {
        return clienteRepository.findSaldoCliente(clienteId);
    }
}