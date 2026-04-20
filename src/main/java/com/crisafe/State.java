package com.crisafe;

public interface State {
    String display();
    void handleInput(String input, Context context);
}
