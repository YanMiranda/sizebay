package yan.api.sizebay.dto.transacao;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TransacaoRequest(

        @NotNull
        @Min(value = 1, message = "Valor deve ser positivo")
        Integer valor,

        @NotNull
        @Pattern(regexp = "[cd]", message = "Tipo deve ser c ou d")
        String tipo,

        @NotNull
        @Size(min = 1, max = 10, message = "Descricao deve ter entre 1 e 10 chars")
        String descricao
) {}
