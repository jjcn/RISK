package edu.duke.ece651.group4.RISK.shared;

public class FoodResource extends Resource {
    public FoodResource() {
        this(0);
    }

    public FoodResource(int quantity) {
        super("food", quantity);
    }
}
