package com.crisafe.pattern;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private State currentState;
    private boolean running = true;
    private final Map<String, String> attributes = new HashMap<>();

    public void setState(State state) {
        this.currentState = state;
    }

    public State getState() {
        return currentState;
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

}
