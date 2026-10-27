
package com.project.supportchatbot.exception;

public class AIServiceException extends RuntimeException {

    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AIServiceException(String message) {
        super(message);
    }
}
