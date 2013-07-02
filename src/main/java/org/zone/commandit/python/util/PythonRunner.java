package org.zone.commandit.python.util;

import java.io.IOException;

import org.python.core.PyBoolean;
import org.python.util.PythonInterpreter;

public class PythonRunner {
    
    private final PythonInterpreter interpreter = new PythonInterpreter();
    
    String code;
    
    public PythonRunner(String code) throws IOException {
        this.code = code;
    }
    
    public boolean run() throws IOException {
    	interpreter.set("success", true);
        interpreter.exec(code);
        return ((PyBoolean)interpreter.get("success")).getBooleanValue();
    }
    
    public void expose(String name, Object o) {
        interpreter.set(name, o);
    }
    
    public static PythonRunner createRunner(String code, Object... objects) throws IOException {
        PythonRunner pr = new PythonRunner(code);
        for (int i = 1; i < objects.length; i += 2) {
            pr.expose((String) objects[i - 1], objects[i]);
        }
        return pr;
    }
    
}
