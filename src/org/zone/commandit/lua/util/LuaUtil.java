package org.zone.commandit.lua.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.vm.KahluaTable;

public class LuaUtil {
    
    private final static KahluaConverterManager converterManager = new KahluaConverterManager();
    private final static J2SEPlatform platform = new J2SEPlatform();
    private final static LuaJavaClassExposer exposer = new LuaJavaClassExposer(converterManager, platform, null);
    
    private static final Map<Class<?>, Class<?>> primitives = new HashMap<Class<?>, Class<?>>();
    static {
        primitives.put(boolean.class, Boolean.class);
        primitives.put(byte.class, Byte.class);
        primitives.put(char.class, Character.class);
        primitives.put(short.class, short.class);
        primitives.put(int.class, Integer.class);
        primitives.put(long.class, Long.class);
        primitives.put(float.class, Float.class);
        primitives.put(double.class, Double.class);
    }
    
    public static J2SEPlatform getPlatform() {
        return platform;
    }
    
    public static KahluaTable createEnvironment(Object... objects) {
        KahluaTable env = platform.newEnvironment();
        for (int i = 1; i < objects.length; i += 2) {
            if (primitives.keySet().contains(objects[i].getClass()) || primitives.values().contains(objects[i].getClass()))
                env.rawset(objects[i - 1], objects[i]);
            else
                env.rawset(objects[i - 1], expose(objects[i]));
        }
        return env;
    }
    
    public static KahluaTable expose(Object o) {
        KahluaTable table = platform.newTable();
        Class<?> clazz = o.getClass();
        for (Method method : clazz.getMethods()) {
            exposer.exposeGlobalObjectFunction(table, o, method);
        }
        return table;
    }
    
    public static KahluaTable expose(Object[] array) {
        KahluaTable table = platform.newTable();
        for (int i = 0; i < array.length; i++) {
            if (primitives.keySet().contains(array[i].getClass()) || primitives.values().contains(array[i].getClass()))
                table.rawset(i, array[i]);
            else
                table.rawset(i, expose(array[i]));
        }
        return table;
    }
    
    public static KahluaTable expose(Collection<?> collection){
        return expose(collection.toArray());
    }
    
}
