package yan.api.sizebay.infra;

public abstract class BusinessException extends RuntimeException {

    protected BusinessException() {
        super();
    }

    protected BusinessException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
