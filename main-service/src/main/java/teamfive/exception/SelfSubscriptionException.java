package teamfive.exception;

public class SelfSubscriptionException extends RuntimeException {
    public SelfSubscriptionException(String message) {
        super(message);
    }
}