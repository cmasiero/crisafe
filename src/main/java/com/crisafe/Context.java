package com.crisafe;

public class Context {

    private State currentState;
    private boolean running = true;

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

}
