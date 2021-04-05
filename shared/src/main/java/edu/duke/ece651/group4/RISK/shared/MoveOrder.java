package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

public class MoveOrder implements Order, Serializable {
    protected static final long serialVersionUID = 6L;
    
    private Character actionName;
    private String src;
    private String des;
    private Troop troop;
    
    public MoveOrder(String src, String des, Troop troop, Character name) {
        this.src = src;
        this.des = des;
        this.troop = troop;
        this.actionName = name;
    }

    @Override
    public Character getActionName() {
        return actionName;
    }

    @Override
    public String getSrcName() {
        return src;
    }

    @Override
    public String getDesName() {
        return des;
    }

    @Override
    public Troop getActTroop() {
        return troop;
    }

 
}



