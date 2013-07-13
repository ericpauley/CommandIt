package org.zone.commandit.exception;


public class CodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    // TODO: Fatal and non-fatal CodeExceptions
    
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
