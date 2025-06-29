package fun.werfamily.sequence.exception;

/**
 * @AuthorMr.WenMing
 */
public class UniqueIdGeneratorException extends RuntimeException {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -27048199131316992L;

    public UniqueIdGeneratorException() {
        super();
    }

    public UniqueIdGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueIdGeneratorException(String message) {
        super(message);
    }


}
