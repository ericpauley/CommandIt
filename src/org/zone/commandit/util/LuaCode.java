package org.zone.commandit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LuaCode implements Iterable<String> {
    
    private boolean enabled = true;
    private String owner;
    private List<String> code;
    private final Map<String, Long> timeouts = new HashMap<String, Long>();
    
    public LuaCode(String owner) {
        this.owner = owner;
        code = new ArrayList<String>();
    }
    
    public void addLine(String string) {
        code.add(string);
    }
    
    public LuaCode clone(String owner) {
        LuaCode code = new LuaCode(owner);
        for (String s : code) {
            code.getLines().add(s);
        }
        return code;
    }
    
    public int count() {
        int size = code.size();
        // Count from last to first, stop whenever a non-blank
        // is found, or if the size hits 'zero'
        while (size > 0 && getLine(size) == "")
            size--;
        return size;
    }
    
    // Internal list is ZERO indexed, one indexed externally only
    public String getLine(int index) {
        return code.get(index - 1);
    }
    
    public List<String> getLines() {
        return this.code;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public Map<String, Long> getTimeouts() {
        return timeouts;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public Iterator<String> iterator() {
        return code.iterator();
    }
    
    public void removeLine(int index) {
        if (index >= 1 && index <= code.size()) {
            code.remove(index - 1);
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setLine(int index, String line) {
        while (code.size() < index) {
            code.add("");
        }
        code.set(index - 1, line);
    }
    
    @Override
    public String toString() {
        String string = "";
        for (String s : code) {
            string += s;
        }
        return string;
    }
    
    public void trim() {
        int blank;
        while ((blank = code.lastIndexOf("")) >= 0)
            code.remove(blank);
        for (String line : code) {
            line.trim();
        }
    }
}
