package org.zone.commandit.exception;


public class CodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public CodeException() {
        super();
    }
    
    public CodeException(String message) {
        super(message);
    }
    
    public CodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
