package com.crisafe.pattern;

public interface State {
    String display();
    void handleInput(String input, Context context);
}
