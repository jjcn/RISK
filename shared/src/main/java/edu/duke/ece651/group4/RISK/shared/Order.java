package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

public interface Order extends Serializable {
    Character getActionName();
    
    public String getSrcName();

    public String getDesName();

    public Troop getActTroop();
}
