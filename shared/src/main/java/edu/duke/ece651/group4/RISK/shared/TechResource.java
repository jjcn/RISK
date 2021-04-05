package edu.duke.ece651.group4.RISK.shared;

public class TechResource extends Resource {
    public TechResource() {
        this(0);
    }

    public TechResource(int quantity) {
        super("tech", quantity);
    }
}
