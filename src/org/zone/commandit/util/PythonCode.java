package org.zone.commandit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PythonCode implements Iterable<String> {
    
    private boolean enabled = true;
    private String owner;
    private List<String> code;
    private Map<String, Long> timeouts = new HashMap<String, Long>();
    
    public PythonCode(String owner) {
        this.owner = owner;
        code = new ArrayList<String>();
    }
    
    /**
     * Add a line of code
     * @param string
     */
    public void addLine(String string) {
        code.add(string);
    }
    
    /**
     * Safely duplicate this object
     * @param owner New owner
     * @return LuaCode instance with identical code, but optional new owner and no timeouts
     */
    public PythonCode clone(String owner) {
        PythonCode code = new PythonCode(owner);
        for (String s : code) {
            code.getLines().add(s);
        }
        return code;
    }
    
    /**
     * @return The number of lines of code available
     */
    public int count() {
        int size = code.size();
        // Count from last to first, stop whenever a non-blank
        // is found, or if the size hits 'zero'
        while (size > 0 && getLine(size) == "")
            size--;
        return size;
    }
    
    /**
     * Get a line of code
     * @param index Line number >= 1
     * @return
     */
    public String getLine(int index) {
        // Internal list is ZERO indexed, one indexed externally only
        return code.get(index - 1);
    }
    
    /**
     * @return All available code
     */
    public List<String> getLines() {
        return this.code;
    }
    
    /**
     * @return Owner of the lua code
     */
    public String getOwner() {
        return owner;
    }
    
    /**
     * If commands exist in the code that require knowing when
     * a user lasted executed the code, their name and epoch
     * timestamp appears in this map.
     */
    public Map<String, Long> getTimeouts() {
        return timeouts;
    }
    
    /**
     * @return True if the code is click-enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Insert code at a specific line, moving subsequent lines down
     * @param index Line number >= 1
     * @param line Code to enter
     */
    public void insertLine(int index, String line) {
        int total = code.size();
        // Move lines down
        for (int i = total; i > index; i--) {
            setLine(i + 1, getLine(i));
        }
        // Insert line
        setLine(index, line);
    }
    
    @Override
    public Iterator<String> iterator() {
        return code.iterator();
    }
    
    /**
     * Remove a line of code
     * @param index Line number >= 1
     */
    public void removeLine(int index) {
        if (index >= 1 && index <= code.size()) {
            code.remove(index - 1);
        }
    }
    
    /**
     * Set whether the code is click-enabled
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Add code at a specific line
     * @param index Line number >= 1
     * @param line Code to enter
     */
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
        	if(!string.equals(""))
        		string += "\n";
            string += s;
        }
        return string;
    }
    
    /**
     * Remove excess whitespace
     */
    public void trim() {
        int blank;
        while ((blank = code.lastIndexOf("")) >= 0)
            code.remove(blank);
        for (String line : code) {
            line.trim();
        }
    }
}
