package org.zone.commandit.code;

import java.io.OutputStream;

import org.zone.commandit.exception.CodeException;


public interface CodeRunner extends Runnable {
    
    /**
     * Run the code
     */
    public void run() throws CodeException;
    
    /**
     * Expose an object to the code interpreter
     * @param name Alias for the object
     * @param o The object to invoke
     */
    public void expose(String name, Object o);
    
    /**
     * Set the stream that is used for stdout
     * @param os The OutputStream to receive stdout messages
     */
    public void setOut(OutputStream os);
    
}
