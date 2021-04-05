package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

public class PlaceOrder implements Order, Serializable {
    protected static final long serialVersionUID = 8L;
    private Character actionName;
    private String des;
    private Troop troop;

    public PlaceOrder(String des, Troop troop) {
        this.actionName = 'P';
        this.des = des;
        this.troop = troop;
    }

    @Override
    public Character getActionName() {
        return actionName;
    }

    @Override
    public String getSrcName() {
        // TODO Auto-generated method stub
        return null;
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