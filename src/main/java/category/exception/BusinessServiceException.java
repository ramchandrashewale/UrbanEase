package category.exception;

public class BusinessServiceException extends RuntimeException {
    private String errorCode;

    public BusinessServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessServiceException(String message) {
        super(message);
    }

    public String getErrorCode() {
        return errorCode;
    }


}
