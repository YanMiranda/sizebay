package yan.api.sizebay.dto.extrato;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record ExtratoResponse(
        @JsonProperty("saldo")
        SaldoDto saldo,

        @JsonProperty("ultimas_transacoes")
        List<TransacaoDto> ultimasTransacoes
) {
    public record SaldoDto(
            Integer total,
            @JsonProperty("data_extrato")
            LocalDateTime dataExtrato,
            Integer limite
    ) {}

    public record TransacaoDto(
            Integer valor,
            String tipo,
            String descricao,
            @JsonProperty("realizada_em")
            LocalDateTime realizadaEm
    ) {}
}
