package com.crisafe;

public class Main {

    public static void main(String[] args) {

        Context context = new Context();
        context.setState(new MainMenuState());

        while (context.isRunning()) {
            State state = context.getState();
            String selection = state.display();
            state.handleInput(selection, context);
        }

    }

}
