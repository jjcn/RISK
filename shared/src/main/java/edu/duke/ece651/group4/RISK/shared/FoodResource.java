package edu.duke.ece651.group4.RISK.shared;

public class FoodResource extends Resource {
    public FoodResource() {
        super("food", 0);
    }

    public FoodResource(int quantity) {
        super("food", quantity);
    }
}
