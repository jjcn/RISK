package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

public class FoodResource extends Resource implements Serializable {
    protected static final long serialVersionUID = 3L;
    public FoodResource() {
        this(0);
    }

    public FoodResource(int quantity) {
        super("food", quantity);
    }
}
