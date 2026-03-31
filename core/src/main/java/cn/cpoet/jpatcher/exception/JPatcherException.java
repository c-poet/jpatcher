package cn.cpoet.jpatcher.exception;

/**
 * 异常
 *
 * @author CPoet
 */
public class JPatcherException extends RuntimeException {

    private static final long serialVersionUID = -6045503054658482670L;

    public JPatcherException(String message) {
        super(message);
    }

    public JPatcherException(String message, Throwable cause) {
        super(message, cause);
    }
}
