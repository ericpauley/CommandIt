package org.zone.commandit.lua.util;

import java.io.IOException;


public class LuaUtil {
    
    
    
    public static LuaRunner createRunner(String code, Object... objects) throws IOException{
        LuaRunner sr = new LuaRunner(code);
        for (int i = 1; i < objects.length; i += 2) {
            sr.expose((String) objects[i-1], objects[i]);
        }
        return sr;
    }
    
}
