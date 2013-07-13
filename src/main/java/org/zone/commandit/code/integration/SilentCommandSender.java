package org.zone.commandit.code.integration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.bukkit.command.CommandSender;

/**
 * Class to silence any interaction with the sendMessage() CommandSender method
 */
public class SilentCommandSender implements InvocationHandler {
    private static final Class<CommandSender> csClass = CommandSender.class;
    
    /**
     * @param sender The CommandSender that's issuing a command
     * @return A CommandSender with all sendMessage() methods muted
     */
    public CommandSender emulate(CommandSender sender) {
        return (CommandSender) Proxy.newProxyInstance(
                csClass.getClassLoader(),
                csClass.getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if (method.getName() == "sendMessage") {
            // Swallow the message and do nothing
            return null;
        } else {
            return method.invoke(o, args);
        }
    }
}
