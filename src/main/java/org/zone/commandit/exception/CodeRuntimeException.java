package org.zone.commandit.exception;


public class CodeRuntimeException extends CodeException {
    private static final long serialVersionUID = 1L;
    
    public CodeRuntimeException() {
        super();
    }
    
    public CodeRuntimeException(String message) {
        super(message);
    }
    
    public CodeRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
