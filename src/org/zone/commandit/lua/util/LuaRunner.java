package org.zone.commandit.lua.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaClosure;

public class LuaRunner {
    
    private final KahluaConverterManager converterManager = new KahluaConverterManager();
    private final J2SEPlatform platform = new J2SEPlatform();
    private final KahluaTable env = platform.newEnvironment();
    private final KahluaThread thread = new KahluaThread(platform, env);
    private final LuaCaller caller = new LuaCaller(converterManager);
    private final LuaJavaClassExposer exposer = new LuaJavaClassExposer(converterManager, platform, env);
    LuaClosure closure;
    
    Set<Class<?>> exposedClasses = new HashSet<Class<?>>();
    
    public LuaRunner(String code) throws IOException {
        closure = LuaCompiler.loadstring(code, "", env);
    }
    
    public void run() throws IOException {
        caller.protectedCall(thread, closure);
    }
    
    public void expose(String name, Object o) {
        
        env.rawset(name, o);
    }
    
    public void expose(Class<?> clazz){
        if (!exposedClasses.contains(clazz)) {
            exposer.exposeClass(clazz);
            exposedClasses.add(clazz);
            for(Method m:clazz.getMethods()){
                expose(m.getReturnType());
            }
        }
    }
}