package edu.duke.ece651.group4.RISK.shared;

public class BasicOrder implements Order {
    private Character actionName;
    private String src;
    private String des;
    private Troop troop;
    
    public BasicOrder(String src, String des, Troop troop, Character name) {
        this.src = src;
        this.des = des;
        this.troop = troop;
        this.actionName = name;
    }

    @Override
    public Character getActionName() {
        return actionName;
    }

    public String getSrcName() {
        return src;
    }

    public String getDesName() {
        return des;
    }

    public Troop getActTroop() {
        return troop;
    }

 
}



