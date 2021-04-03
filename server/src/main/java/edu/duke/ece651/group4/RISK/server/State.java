package edu.duke.ece651.group4.RISK.server;

public class State {
    String state;

    State(String s){
        this.state = s;
    }

    public String getState(){
        return state;
    }

    public void updateStateTo(String s){
        this.state = s;
    }
}
