package edu.duke.ece651.group4.RISK.server;

import java.io.Serializable;

public class State implements Serializable {
    private static final long serialVersionUID = 10000L;
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
