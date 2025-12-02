package yan.api.sizebay.exception;

import yan.api.sizebay.infra.BusinessException;

public class ClienteNaoEncontradoException extends BusinessException {

  public ClienteNaoEncontradoException() {
    super("Cliente não encontrado.");
  }

  public ClienteNaoEncontradoException(String message) {
    super(message);
  }
}
