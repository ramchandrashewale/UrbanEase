package user.exception;

public class UserException extends RuntimeException {
    public UserException(String message, Exception exception) {
        super(message);
    }
}
