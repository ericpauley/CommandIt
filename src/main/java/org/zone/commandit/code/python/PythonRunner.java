package org.zone.commandit.code.python;

import java.io.OutputStream;

import org.python.util.PythonInterpreter;
import org.zone.commandit.code.CodeRunner;

public class PythonRunner implements CodeRunner {
    
    private final PythonInterpreter interpreter = new PythonInterpreter();
    
    private String code;
    
    public PythonRunner(String code) {
        this.code = code;
    }
    
    @Override
    public void run() {
        interpreter.exec(code);
    }
    
    @Override
    public void expose(String name, Object o) {
        interpreter.set(name, o);
    }
    
    @Override
    public void setOut(OutputStream os){
    	interpreter.setOut(os);
    }
    
    public static PythonRunner createRunner(String code, Object... objects) {
        PythonRunner pr = new PythonRunner(code);
        for (int i = 1; i < objects.length; i += 2) {
            pr.expose((String) objects[i - 1], objects[i]);
        }
        return pr;
    }
    
}
