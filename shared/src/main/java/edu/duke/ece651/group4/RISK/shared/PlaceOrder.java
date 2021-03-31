package edu.duke.ece651.group4.RISK.shared;

public class PlaceOrder implements Order {
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

    public String getDesName() {
        return des;
    }

    public Troop getActTroop() {
        return troop;
    }
}