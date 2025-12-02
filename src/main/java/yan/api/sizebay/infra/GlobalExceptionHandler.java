package yan.api.sizebay.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yan.api.sizebay.exception.ClienteNaoEncontradoException;
import yan.api.sizebay.exception.SaldoInsuficienteException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Void> handleSaldoInsuficiente() {
        return ResponseEntity.unprocessableEntity().build();
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<Void> handleClienteNaoEncontrado() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Void> handleValidationExceptions() {
        return ResponseEntity.unprocessableEntity().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleGeneral(Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}