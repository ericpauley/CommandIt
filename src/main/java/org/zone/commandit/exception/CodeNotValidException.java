package org.zone.commandit.exception;


public class CodeNotValidException extends CodeException {
    private static final long serialVersionUID = 1L;
    
    public CodeNotValidException() {
        super();
    }
    
    public CodeNotValidException(String message) {
        super(message);
    }
    
    public CodeNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
