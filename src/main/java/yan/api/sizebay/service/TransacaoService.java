package yan.api.sizebay.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yan.api.sizebay.dto.extrato.ExtratoResponse;
import yan.api.sizebay.dto.transacao.TransacaoRequest;
import yan.api.sizebay.dto.transacao.TransacaoResponse;
import yan.api.sizebay.exception.ClienteNaoEncontradoException;
import yan.api.sizebay.exception.SaldoInsuficienteException;
import yan.api.sizebay.model.Cliente;
import yan.api.sizebay.repository.ClienteRepository;
import yan.api.sizebay.repository.TransacaoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final ClienteRepository clienteRepository;
    private final TransacaoRepository transacaoRepository;

    @Transactional
    public TransacaoResponse processTransaction(Integer clienteId, TransacaoRequest request) {

        TransacaoResponse result = transacaoRepository.executeTransaction(
                clienteId,
                request.valor(),
                request.descricao(),
                request.tipo().toString()
        );

        if (Objects.isNull(result)) {
            throw new ClienteNaoEncontradoException();
        }

        if (Boolean.TRUE.equals(result.erro())) {
            throw new SaldoInsuficienteException();
        }

        return new TransacaoResponse(result.limite(), result.saldo(), result.erro());
    }

    @Transactional(readOnly = true)
    public ExtratoResponse getStatement(Integer clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(ClienteNaoEncontradoException::new);

        List<ExtratoResponse.TransacaoDto> ultimasTransacoes = transacaoRepository.findLatestByClienteId(clienteId)
                .stream()
                .map(t -> new ExtratoResponse.TransacaoDto(
                        t.getValor(),
                        t.getTipo().name(),
                        t.getDescricao(),
                        t.getRealizadaEm()
                )).toList();

        return new ExtratoResponse(
                new ExtratoResponse.SaldoDto(
                        cliente.getSaldo(),
                        LocalDateTime.now(),
                        cliente.getLimite()
                ),
                ultimasTransacoes
        );
    }
}