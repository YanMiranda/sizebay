package yan.api.sizebay.exception;

import yan.api.sizebay.infra.BusinessException;

public class SaldoInsuficienteException extends BusinessException {

    public SaldoInsuficienteException() {
        super("Saldo insuficiente.");
    }

    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
