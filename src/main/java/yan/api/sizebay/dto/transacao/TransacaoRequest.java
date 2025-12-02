package yan.api.sizebay.dto.transacao;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import yan.api.sizebay.model.enums.TipoTransacao;

public record TransacaoRequest(

        @NotNull
        @Min(value = 1)
        Integer valor,

        @NotNull
        TipoTransacao tipo,

        @NotNull
        @Size(min = 1, max = 10, message = "Descrição deve conter entre 1 e 10 caracteres")
        String descricao
) {}
